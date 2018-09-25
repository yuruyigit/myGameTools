package game.tools.db.mybatis.plush.transction;

import java.util.HashMap;
import java.util.Map;

import game.data.conf.entity.Achievements;
import game.data.conf.mapper.AchievementsMapper;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.db.mybatis.plush.MybatisTransactionInterceptor;
import game.tools.http.HttpServer;
import game.tools.http.protocol.HttpCmd;
import game.tools.http.protocol.HttpCmdServlet;
import game.tools.http.protocol.HttpPackage;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;

public class MybatisTransactionMainServer 
{
	public static void main(String[] args)
	{
		test();
//		testRedis();
	}
	
	private static void testRedis() 
	{
		RedisOper.connection("127.0.0.1" , 6379 , null);
		
		int segment = 100;  
        Map<String, String> kvMap = new HashMap<String, String>();  
        for (int i = 1; i <= 100000; i++) 
        {  
            String key = "f:" + String.valueOf(i % segment);  
            String value = "v:" + i;  
            kvMap.put(key, value);  
            if (i % segment == 0) 
            {  
                System.out.println(i);  
                int hash = (i - 1) / segment;  
                String tkey = "u:" + String.valueOf(hash);
                RedisOper.execute(RedisCmd.hmset, tkey, kvMap);
                kvMap = new HashMap<String, String>();  
            }  
        }  
	}

	private static void test()
	{
		HttpServer httpServer = new HttpServer("transaction", 8888);
		HttpCmdServlet.setHttpCmdServletScanClassPackage("game.tools.db.mybatis.plush.transction");
		httpServer.registerServlet(HttpCmdServlet.class);
	}
	
	@HttpCmd(cmdNo = 1111)
	private static boolean testTran(HttpPackage pkg)
	{
		String transctionId = pkg.get(1);
		
		MybatisTransaction.start(transctionId);	//继续执行对应id的事务
		
		String sessionKey = MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , new MybatisTransactionInterceptor("127.0.0.1" , 6379 , null));
		
		Achievements achievement = MybatisFactoryTools.getMapper(sessionKey, AchievementsMapper.class).selectByPrimaryKey(10101);
		achievement.setDescription("dsafasdfasdfasdfasdfsfasf事务");
		MybatisFactoryTools.getMapper(sessionKey, AchievementsMapper.class).updateByPrimaryKey(achievement);
		
//		MybatisTransaction.rollback(transctionId);
		
//		return false;
		return true;
	}

}
