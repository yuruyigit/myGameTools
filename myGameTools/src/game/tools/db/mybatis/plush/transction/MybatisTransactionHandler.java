package game.tools.db.mybatis.plush.transction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.alibaba.fastjson.JSONObject;

import game.tools.db.mybatis.MybatisTools;
import game.tools.log.LogUtil;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;
import game.tools.threadpool.ThreadLocal;
import game.tools.utils.DateTools;
import game.tools.utils.IP;
import game.tools.utils.UUIDTools;
import game.tools.utils.Util;

public class MybatisTransactionHandler 
{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String TRANSCTION = "TRANSCTION";
	private static final String TRANSACTION_ROLLBACK = TRANSCTION + "-ROLLBACK";
	
	private static final String  SERVER_SIGN = "[" + IP.getLocalIp() + "-" + Util.getLocalPath() + "]";

	public static void doTransction(Invocation invocation) 
	{
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		
		if(mappedStatement.getSqlCommandType() == SqlCommandType.SELECT)					//查询语句的话，就直接返回。
			return ;
		
		String transctionId = getTransctionId();											//获取当前线程事务ID
		
		if(transctionId == null)
			return ;
		
		String originalSql = getSql(invocation);											//获取原始sql语句
		
		String rollbackSql = getRollbackSql(invocation , originalSql);						//解析回滚语句
		
		doRecordRedis(invocation , transctionId , originalSql , rollbackSql);							//记录redis
		
	}
	
	/**
	 * @return 通过原始sql获取回滚sql
	 */
	private static String getRollbackSql(Invocation invocation , String sql)
	{
		String rollbackSql = "";
		
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		
		CachingExecutor cachingExecutor = (CachingExecutor)invocation.getTarget();
		
		if(mappedStatement.getSqlCommandType() == SqlCommandType.INSERT)		//如果是插入语句
		{
			Object parameterObject = invocation.getArgs()[1];
			BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
			Configuration configuration = mappedStatement.getConfiguration();
			List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			
			ArrayList<String> colunmList = new ArrayList<String>();		//列名列表
			ArrayList<Object> valuesList = new ArrayList<Object>();		//对应列名值的列表
			
			for (Object object : configuration.getResultMaps()) 
			{
				if(object instanceof ResultMap)
				{
					ResultMap resultMap = (ResultMap)object;
					
					if(resultMap.getId().indexOf(parameterObject.getClass().getSimpleName()) >= 0)
					{
						for (ResultMapping resultMapping : resultMap.getPropertyResultMappings()) 
							colunmList.add(resultMapping.getColumn());
						
						break;
					}
				}
			}
			
			if (parameterMappings != null)
			{
				for (int i = 0; i < parameterMappings.size(); i++)
				{
					ParameterMapping parameterMapping = parameterMappings.get(i);
					if (parameterMapping.getMode() != ParameterMode.OUT)
					{
						Object value = null;
						
						String propertyName = parameterMapping.getProperty();
						if("paramString".equals(propertyName))
						{
							
						}
						if (boundSql.hasAdditionalParameter(propertyName))
							value = boundSql.getAdditionalParameter(propertyName);
						else if (parameterObject == null)
							value = null;
						else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass()))
							value = parameterObject;
						else
						{
							MetaObject metaObject = configuration.newMetaObject(parameterObject);
							value = metaObject.getValue(propertyName);
						}
						valuesList.add(value);
					}
				}
			}
			
			String tableName = boundSql.getSql().split("\\(")[0].split("into")[1].trim();
			
			rollbackSql = "delete from " + tableName + " where";
			
			Object value = null;
			
			for (int i = 0; i < colunmList.size(); i++) 
			{
				value = valuesList.get(i);
				if(value instanceof String)
				{
					value = "'" + value + "'";
				}
				
				if(value == null)
				{
					if(i < colunmList.size() - 1 )
						rollbackSql += " `" + colunmList.get(i) + "` is null and";
					else
						rollbackSql += " `" + colunmList.get(i) + "` is null ";
				}
				else
				{
					if(i < colunmList.size() - 1 )
						rollbackSql += " `" + colunmList.get(i) + "` = " + value + " and";
					else
						rollbackSql += " `" + colunmList.get(i) + "` = " + value;
				}
			}
		}
		else if(mappedStatement.getSqlCommandType() == SqlCommandType.DELETE)
		{
			String tableName = sql.substring(sql.indexOf("from") , sql.indexOf("where")).split(" ")[1].trim();
			
			String whereSql = sql.substring(sql.lastIndexOf("where"));
			
			try 
			{
				PreparedStatement statement = cachingExecutor.getTransaction().getConnection().prepareStatement("select * from " + tableName + " " + whereSql);
				ResultSet resultSet = statement.executeQuery();
				
				int columnCount = resultSet.getMetaData().getColumnCount();
				
				String insertTitleSql = "insert into " + tableName + " values ("; 
				String insertSql = "" ;
				Object value = null;
				
				while(resultSet.next()) 
				{
					insertSql += insertTitleSql;
					
					for (int j = 0; j < columnCount; j++) 
					{
						value = resultSet.getObject(j + 1);
						
						if(value instanceof String)
							value = "'" + value + "'";
						
						if(j < columnCount - 1 )
							insertSql += value + ",";
						else
							insertSql += value + ")";
					}
					
					insertSql += " ; \n";
				}
				
				rollbackSql = insertSql;
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		}
		else if(mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE)
		{
			String tableName = sql.substring(sql.indexOf("update "), sql.indexOf(" set")).split(" ")[1].trim();
			
			String whereSql = sql.substring(sql.lastIndexOf("where"));
			
			try 
			{
				PreparedStatement statement = cachingExecutor.getTransaction().getConnection().prepareStatement("select * from " + tableName + " " + whereSql);
				ResultSet resultSet = statement.executeQuery();
				
				int columnCount = resultSet.getMetaData().getColumnCount();
				
				String updateTitleSql = "update " + tableName + " set "; 
				String udpateSql = ""  , column = "";
				Object value = null;
				
				while(resultSet.next()) 
				{
					udpateSql += updateTitleSql;
					
					for (int j = 0; j < columnCount; j++) 
					{
						column = resultSet.getMetaData().getColumnName(j + 1);
						value = resultSet.getObject(j + 1);
						
						if(value instanceof String)
							value = "'" + value + "'";
						
						if(j < columnCount - 1 )
							udpateSql += "`" + column + "` = " + value + ",";
						else
							udpateSql += "`" + column + "` = " + value;
					}
					
					udpateSql += " " + whereSql + " ; \n";
				}
				
				rollbackSql = udpateSql;
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("command " + mappedStatement.getSqlCommandType() + " rollbackSql = " + rollbackSql);
		
		return rollbackSql;
	}
	
	private static String getSql(Invocation invocation)
	{
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameterObject = null;
		if (invocation.getArgs().length > 1)
		{
			parameterObject = invocation.getArgs()[1];
		}

		BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
		Configuration configuration = mappedStatement.getConfiguration();
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		
		if (parameterMappings != null)
		{
			for (int i = 0; i < parameterMappings.size(); i++)
			{
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT)
				{
					Object value;
					String propertyName = parameterMapping.getProperty();
					if("paramString".equals(propertyName))
					{
						
					}
					if (boundSql.hasAdditionalParameter(propertyName))
					{
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (parameterObject == null)
					{
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass()))
					{
						value = parameterObject;
					} else
					{
						MetaObject metaObject = configuration.newMetaObject(parameterObject);
						value = metaObject.getValue(propertyName);
					}
					sql = replacePlaceholder(sql, value);
				}
			}
		}
		
		return sql.toLowerCase();
	}
	
	private static String replacePlaceholder(String sql, Object propertyValue)
	{
		String result;
		if (propertyValue != null)
		{
			if (propertyValue instanceof String)
				result = "'" + propertyValue + "'";
			else if (propertyValue instanceof Date)
				result = "'" + DATE_FORMAT.format(propertyValue) + "'";
			else
				result = propertyValue.toString();
		}
		else
		{
			result = "null";
		}
		return sql.replaceFirst("\\?", result);
	}
	
	private static void doRecordRedis(Invocation invocation, String transctionId, String originalSql, String rollbackSql) 
	{
		if(rollbackSql == null)
			return;
		
		JSONObject o = new JSONObject();
		o.put("time", DateTools.getCurrentTimeMSString());
		o.put("serverSign", SERVER_SIGN);
		o.put("mysqlUrl", getMysqlUrl(invocation));
		o.put("rollbackSql", rollbackSql);
		o.put("originalSql", originalSql);
		
		String transctionKey = getTransctionKey(transctionId);
		
		RedisOper.execute(RedisCmd.lpush, transctionKey, o.toJSONString());
	}
	

	private static String getMysqlUrl(Invocation invocation)
	{
		CachingExecutor cachingExecutor = (CachingExecutor)invocation.getTarget();
		try 
		{
			String url = cachingExecutor.getTransaction().getConnection().getMetaData().getURL();
			
			return MybatisTools.getMysqlKey(url);
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
		return null;
	}	
	
	public static String transction(String transctionId)
	{
		ThreadLocal.setLocal(TRANSCTION, transctionId);
		
		return transctionId;
	}
	

	public static String getTransctionId()	{		return ThreadLocal.getLocal(TRANSCTION);	}
	public static String getTransctionKey(String tranId)	{		return TRANSCTION + "-" + tranId ;	}
	public static String getTransactionRollBackKey(String serverSign)	{		return TRANSACTION_ROLLBACK + "-" + serverSign;	}
	public static String getTransactionRollBackKey()	{		return getTransactionRollBackKey(SERVER_SIGN);	}
	
	
	
	
}
