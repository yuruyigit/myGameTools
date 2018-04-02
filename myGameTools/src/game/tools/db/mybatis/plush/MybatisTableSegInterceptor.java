package game.tools.db.mybatis.plush;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import game.tools.db.mybatis.plush.sqlittable.SqlActionControl;
import game.tools.db.mybatis.plush.sqlittable.SqlCmd;

/**
 * @author zhibing.zhou <br/>
 * MyBatis 拦截器 ， 主要用于插入，查询，进行的自动按行分表。如果配置100000(10W)行数据，进行分表。
 */
@Intercepts
(
	{ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class}) }
)
public class MybatisTableSegInterceptor implements Interceptor
{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	 
    private static SqlActionControl SQL_ACTION_CONTROL ;

	private static long SPLIT_COUNT = 5000000;
	
	/** <pre>
	 * 	进行指定行数量表分割，默认<b>500W</b>行数据。
	 * 	注意：定好的行数量，进行已分割的表，唯一ID，已经生成。
	 * 
	 * 	<b>强烈不建议在定好的行数量表，进行行数的修改扩容。</b>
	 * 
	 * 	比如：100行的表，唯自增ID已生成，这时，行数量由100修改为200。
	 *	则导致，两个表会出现相同的ID唯key。
	 *	如表table有个150号 , 表table_0 ， 表table_1 都会有个150号的ID自增key。
	 *  
	 * 	<b>因为，在次插入的时候，则会优先定位第一个表，进行定位插入。
	 *	这样，每一个表的ID，又从100，继续自增至200。</b>
	 * </pre>
     * @param splitCount 需要多少行进行分割表 , 默认1000w行分表。
     */
	public MybatisTableSegInterceptor()    {    }
	
	/** <pre>
	 * 	进行指定行数量表分割，默认<b>500W</b>行数据。
	 * 	注意：定好的行数量，进行已分割的表，唯一ID，已经生成。
	 * 
	 * 	<b>强烈不建议在定好的行数量表，进行行数的修改扩容。</b>
	 * 
	 * 	比如：100行的表，唯自增ID已生成，这时，行数量由100修改为200。
	 *	则导致，两个表会出现相同的ID唯key。
	 *	如表table有个150号 , 表table_0 ， 表table_1 都会有个150号的ID自增key。
	 *  
	 * 	<b>因为，在次插入的时候，则会优先定位第一个表，进行定位插入。
	 *	这样，每一个表的ID，又从100，继续自增至200。</b>
	 * </pre>
     * @param splitCount 需要多少行进行分割表 , 默认1000w行分表。
     */
    public MybatisTableSegInterceptor(long splitCount) 
    {
    	MybatisTableSegInterceptor.SPLIT_COUNT = splitCount;
    }
    
	@Override
	public Object intercept(Invocation invocation) throws Throwable
	{
		Connection connection = (Connection) invocation.getArgs()[0];
		
		String sql = null;
		
		if(SQL_ACTION_CONTROL == null)
			SQL_ACTION_CONTROL = new SqlActionControl();
		
		StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		String originalSql = boundSql.getSql();
		
		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,DEFAULT_OBJECT_WRAPPER_FACTORY);
        MappedStatement mappedStatement = (MappedStatement)metaStatementHandler.getValue("delegate.mappedStatement");
        String mappedStatementId = mappedStatement.getId();
        
//        DefaultObjectFactory factory =  (DefaultObjectFactory)metaStatementHandler.getObjectFactory();
//        System.out.println("mappedStatementId = " + mappedStatementId);
//        String sql = getSql(boundSql, parameterObject, configuration);
        
        boolean isAction = true;
		Object [] paramsArray = null;
		String cmd = mappedStatement.getSqlCommandType().name();
		
		if(cmd.equals(SqlCmd.SELECT.getCmd()))				//如果是select查询语句，则去获取参数数组
		{
			Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
		    Object parameterObject = metaStatementHandler.getValue("delegate.boundSql.parameterObject");
		        
			paramsArray = getParamsArray(boundSql, parameterObject, configuration);
		}
		else if(cmd.equals(SqlCmd.INSERT.getCmd()))			//如果是插入语句
		{
			if(originalSql.split("\\(").length > 3)			//如果是批量插入语句
			{
				isAction = false;
				System.out.println("批量插入 mappedStatementId = " + mappedStatementId);
//				System.out.println("originalSql = " + originalSql);
			}
		}
		
		if(isAction)			//允许执行sql的action
		{
			sql = SQL_ACTION_CONTROL.doAction(cmd ,connection , originalSql , paramsArray);
			
			metaStatementHandler.setValue("delegate.boundSql.sql" , sql);
		}
		
		return invocation.proceed();
	}
	
	@Override
	public Object plugin(Object target)
	{
		if(target instanceof StatementHandler)
			return Plugin.wrap(target, this);
		return target;
	}

	@Override
	public void setProperties(Properties arg0)
	{
	}
	
	
	private Object [] getParamsArray(BoundSql boundSql, Object parameterObject, Configuration configuration)
	{
				
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		if (parameterMappings != null)
		{
			Object [] paramsArray = new Object[parameterMappings.size()];
			
			for (int i = 0; i < parameterMappings.size(); i++)
			{
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT)
				{
					Object value;
					String propertyName = parameterMapping.getProperty();
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
					paramsArray[i] = value;
				}
			}
			return paramsArray;
		}
		return null;
	}
	
	
	private String getSql(BoundSql boundSql, Object parameterObject, Configuration configuration)
	{
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
		return sql;
	}
	
	
	private String replacePlaceholder(String sql, Object propertyValue)
	{
		String result;
		if (propertyValue != null)
		{
			if (propertyValue instanceof String)
			{
				result = "'" + propertyValue + "'";
			} else if (propertyValue instanceof Date)
			{
				result = "'" + DATE_FORMAT.format(propertyValue) + "'";
			} else
			{
				result = propertyValue.toString();
			}
		} else
		{
			result = "null";
		}
		return sql.replaceFirst("\\?", result);
	}

	public static long getSPLIT_COUNT() {		return SPLIT_COUNT;	}
	
	/**
	 * 获取字段值
	 * @param propertyName
	 * @param isMutiPara
	 * @return
	 */
//	public Object getFieldValue(String propertyName,boolean isMutiPara)
//	{
//		MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
//		Object value;
//		if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
//			value = boundSql.getAdditionalParameter(propertyName);
//		} else if (parameterObject == null) {
//			value = null;
//		} else if (typeHandlerRegistry.hasTypeHandler(parameterObject
//				.getClass())) {
//			if(isMutiPara)//多个参数，这情况就不应该匹配了
//				return null;
//			value = parameterObject;
//		} else {
//			value = metaObject == null ? null : metaObject
//					.getValue(propertyName);
//		}
//		return value;
//	}

}
