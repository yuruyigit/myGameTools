package game.tools.db.mybatis.plush.transction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import com.alibaba.fastjson.JSONObject;
import game.tools.db.cache.expire.ExpireCacheDataMap;
import game.tools.db.cache.expire.IExpire;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.log.LogUtil;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;

class MybatisTransactionRollback 
{
	private static Object LOCK = new Object();
	private static boolean START = false;
	
	/** 5秒去检查回滚事务， 1分钟检查超时事务*/
	private static final long ROLLBACK_SLEEP = 5000 , TIMEOUT_SLEEP = 60 * 1000;
	
	/** 事务超时时间  这里为30分钟*/
	private static final long TRANSACTION_TIMEOUT = 30 * 60 * 1000;
	
	/** 缓存数组库连接列表*/
	private static final ExpireCacheDataMap<String, Connection> CONNECTION_MAP = new ExpireCacheDataMap<>(new IExpire<Connection>() 
	{
		@Override
		public void expire(Connection conn) 
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	});
	
	public static void start()
	{
		if(START)
			return;
		
		synchronized (LOCK) 
		{
			if(START)
				return;
			
			rollbackStart();
			timeoutRollbackStart();
		}
	}

	/**
	 * @return 返回缓存的数据连接
	 */
	private static Connection getConnection(String mysqlKey)
	{
		Connection conn = CONNECTION_MAP.get(mysqlKey);
		
		if(conn != null)
			return conn;
		
		synchronized (CONNECTION_MAP) 
		{
			conn = CONNECTION_MAP.get(mysqlKey);
			
			if(conn != null)
				return conn;
			
			if(conn == null)
			{
				SqlSessionTemplate sqlSession =  (SqlSessionTemplate)MybatisFactoryTools.getSqlSession(mysqlKey);
				conn = sqlSession.getSqlSessionFactory().openSession().getConnection();
				
				CONNECTION_MAP.put(mysqlKey, conn);
			}
		}
		
		return conn;
		
//		SqlSessionTemplate sqlSession =  (SqlSessionTemplate)MybatisFactoryTools.getSqlSession(mysqlKey);
//		return sqlSession.getSqlSessionFactory().openSession().getConnection();
		
	}
	
	/**
	 * 回滚操作
	 */
	private static void rollbackStart() 
	{
		new Thread(() -> 
		{
			Connection conn = null;
			PreparedStatement statment = null;
			
			String mysqlKey = null , sql = null;
			
			while(START)
			{
				try 
				{
					String transctionRollbackKey = MybatisTransactionHandler.getTransactionRollBackKey();
					
					List<String> rollbackList = RedisOper.execute(RedisCmd.lget, transctionRollbackKey);
					
					if(rollbackList == null || rollbackList.isEmpty())
						continue;
					
					for(int i = rollbackList.size() - 1 ; i >= 0 ; i --)			//按倒序，先执行列表最先开始的sql。
					{
						String jsonString = rollbackList.get(i);
						
						JSONObject o = JSONObject.parseObject(jsonString);
						
						mysqlKey = o.getString("mysqlUrl");
						
						sql = o.getString("rollbackSql");
						
						conn = getConnection(mysqlKey);
						
						statment = conn.prepareStatement(sql);
						
						statment.execute();
						
						RedisOper.execute(RedisCmd.lrem, transctionRollbackKey , jsonString);
					}
					
					Thread.sleep(ROLLBACK_SLEEP);
				}
				catch (Exception e) 
				{
					String errorSql = "exception sql : " + sql;
					
					System.err.println(errorSql);
					e.printStackTrace();
					
					LogUtil.error(e , errorSql);
				}
			}
			
		}, "MybatisTransaction-Rollback").start();
		
		START = true;
	}
	
	/**
	 *  执行超时回滚操作
	 */
	private static void timeoutRollbackStart()
	{
		new Thread(() -> 
		{
			String match = MybatisTransactionHandler.getTransctioningValue()+"*";
			
			while(START)
			{
				try
				{
					ArrayList<String> list = RedisOper.scan(match);		//获取当前处理的事务列表
					
					while(!list.isEmpty())
					{
						for (String key : list) 
						{
							List<String> valueList = RedisOper.execute(RedisCmd.lget, key);
							
							String value = valueList.get(valueList.size() - 1);
							
							JSONObject o = JSONObject.parseObject(value);
							
							long time = o.getLongValue("timelong");
							
							long gapTime = System.currentTimeMillis() - time;
							
							if(gapTime >= TRANSACTION_TIMEOUT)
							{
								String transctionId = key.split("-")[1];
								MybatisTransactionHandler.rollback(transctionId);
							}
						}
						
						list = RedisOper.scan(match);
						
						Thread.sleep(1000L);
					}
					
					Thread.sleep(TIMEOUT_SLEEP);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					LogUtil.error(e);
				}
			}
		}, "MybatisTransaction-TimeoutRollback").start();
		
	}
}
