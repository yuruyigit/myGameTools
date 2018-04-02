package game.tools.db.mybatis.plush;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import game.tools.utils.DateTools;

/**
 * MyBatis 性能拦截器，用于输出每条 SQL 语句及其执行时间
 */
@Intercepts
({ 
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }), 
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) 
})
public class MybatisSqlTimeInterceptor implements Interceptor
{

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Object intercept(Invocation invocation) throws Throwable
	{
		Object result = printSqlTime(invocation);

		return result;
	}

	/**
	 * zzb 打印sql执行时间
	 * @param invocation
	 * @return
	 * @throws Throwable 
	 */
	private Object printSqlTime(Invocation invocation) throws Throwable
	{
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameterObject = null;
		if (invocation.getArgs().length > 1)
		{
			parameterObject = invocation.getArgs()[1];
		}

		String statementId = mappedStatement.getId();
		BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
		Configuration configuration = mappedStatement.getConfiguration();
		String sql = getSql(boundSql, parameterObject, configuration);
//		String originalSql = boundSql.getSql().trim(); 
//		Connection connection=mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
		
		long start = System.currentTimeMillis();

		Object result = invocation.proceed();

		long end = System.currentTimeMillis();
		long timing = end - start;
		
		System.out.println(DateTools.getCurrentTimeMSString() + " 耗时：" + timing + " ms" + " - id:" + statementId + " - Sql:" + sql);

		return result;
	}
	
	
	 /** 
      * 复制BoundSql对象 
      */  
	  private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) 
	  {  
		    BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),sql, boundSql.getParameterMappings(), boundSql.getParameterObject());  
		    for (ParameterMapping mapping : boundSql.getParameterMappings()) 
		    {  
		        String prop = mapping.getProperty();  
		        if (boundSql.hasAdditionalParameter(prop)) 
		            newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));  
		    }  
		    return newBoundSql;  
	  }  
	  
	  
	  
	  /** 
	   * 复制MappedStatement对象 
	   */  
	  private MappedStatement copyFromMappedStatement(MappedStatement ms,SqlSource newSqlSource) 
	  {  
	    Builder builder = new Builder(ms.getConfiguration(),ms.getId(),newSqlSource,ms.getSqlCommandType());  
	      
	    builder.resource(ms.getResource());  
	    builder.fetchSize(ms.getFetchSize());  
	    builder.statementType(ms.getStatementType());  
	    builder.keyGenerator(ms.getKeyGenerator());  
//	    builder.keyProperty(ms.getKeyProperty());  
	    builder.timeout(ms.getTimeout());  
	    builder.parameterMap(ms.getParameterMap());  
	    builder.resultMaps(ms.getResultMaps());  
	    builder.resultSetType(ms.getResultSetType());  
	    builder.cache(ms.getCache());  
	    builder.flushCacheRequired(ms.isFlushCacheRequired());  
	    builder.useCache(ms.isUseCache());  
	      
	    return builder.build();  
	  }  
	
	@Override
	public Object plugin(Object target)
	{
		if (target instanceof Executor)
		{
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties)
	{
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
					if("paramString".equals(propertyName))
						System.out.println();
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
}
