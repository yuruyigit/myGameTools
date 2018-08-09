package game.tools.db.mybatis.plush;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
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
import com.alibaba.fastjson.JSONObject;

import game.tools.db.mybatis.plush.transction.MybatisTransactionHandler;
import game.tools.db.mybatis.plush.transction.MybatisTransactionRollback;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;
import game.tools.threadpool.ThreadLocal;
import game.tools.utils.IP;
import game.tools.utils.UUIDTools;
import game.tools.utils.Util;

/**
 * mybatis 事务插件
 */
@Intercepts
({ 
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }), 
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) ,
	@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class})
})
public class MybatisTransactionInterceptor implements Interceptor
{
	public MybatisTransactionInterceptor(String redisHost , int redisPort , String redisPassword) 
	{
		RedisOper.connection(redisHost, redisPort, redisPassword);
	}
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable 
	{
		MybatisTransactionHandler.doTransction(invocation);
		
		return invocation.proceed();
	}
	
	@Override
	public Object plugin(Object target) 
	{
		if (target instanceof Executor)
			return Plugin.wrap(target, this);
		return target;
	}

	@Override
	public void setProperties(Properties arg0) 	{			}

}
