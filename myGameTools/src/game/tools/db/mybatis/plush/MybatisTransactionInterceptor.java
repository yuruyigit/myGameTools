package game.tools.db.mybatis.plush;
import java.sql.Connection;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import game.tools.db.mybatis.plush.transction.MybatisTransactionHandler;
import game.tools.redis.RedisOper;

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
