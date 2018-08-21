package game.tools.db.mybatis.plush.transction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import com.alibaba.fastjson.JSONObject;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;

class MybatisTransactionRollback 
{
	private static Object LOCK = new Object();
	private static boolean START = false;
	
	private static final long SLEEP = 3000;

	
	public static void start()
	{
		if(START)
			return;
		
		synchronized (LOCK) 
		{
			if(START)
				return;
			
			rollbackStart();
		}
	}

	private static void rollbackStart() 
	{
		new Thread(() -> 
		{
			SqlSessionTemplate sqlSession = null;
			Connection conn = null;
			PreparedStatement statment = null;
			
			String mysqlKey = null , sql = null;
			try 
			{
				while(START)
				{
					String transctionRollbackKey = MybatisTransactionHandler.getTransactionRollBackKey();
					
					List<String> rollbackList = RedisOper.execute(RedisCmd.lget, transctionRollbackKey);
					
					if(rollbackList == null || rollbackList.isEmpty())
						continue;
					
					for (String jsonString : rollbackList) 
					{
						JSONObject o = JSONObject.parseObject(jsonString);
						
						mysqlKey = o.getString("mysqlUrl");
						
						sql = o.getString("rollbackSql");
						
						sqlSession =  (SqlSessionTemplate)MybatisFactoryTools.getSqlSession(mysqlKey);
						
						conn = sqlSession.getSqlSessionFactory().openSession().getConnection();
						
						statment = conn.prepareStatement(sql);
						
						statment.execute();
						
						RedisOper.execute(RedisCmd.lrem, transctionRollbackKey , jsonString);
					}
					
					Thread.sleep(SLEEP);
				}
			}
			catch (Exception e) 
			{
				System.out.println("exception sql : " + sql );
				e.printStackTrace();
			}
		}, "MybatisTransactionRollback").start();
		
		START = true;
	}
}
