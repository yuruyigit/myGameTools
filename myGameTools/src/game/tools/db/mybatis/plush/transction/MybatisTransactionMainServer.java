package game.tools.db.mybatis.plush.transction;

import game.data.conf.entity.Achievements;
import game.data.conf.entity.CardStarUps;
import game.data.conf.mapper.AchievementsMapper;
import game.data.conf.mapper.CardStarUpsMapper;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.db.mybatis.plush.MybatisTransactionInterceptor;
import game.tools.http.HttpServer;
import game.tools.http.HttpTools;
import game.tools.http.protocol.HttpCmd;
import game.tools.http.protocol.HttpCmdServlet;
import game.tools.http.protocol.HttpPackage;
import game.tools.utils.Util;

public class MybatisTransactionMainServer 
{
	public static void main(String[] args)
	{
		test();
	}
	
	private static void test()
	{
		HttpServer httpServer = new HttpServer("transaction", 8888);
		HttpCmdServlet.setHttpCmdServletScanClassPackage("game.tools.db.mybatis.plush.transction");
		httpServer.registerServlet(HttpCmdServlet.class);
	}
	
	@HttpCmd(cmdNo = 1111)
	private static void testTran(HttpPackage pkg)
	{
		MybatisTransaction.start(pkg.get(1));	//继续执行对应id的事务
		
		String sessionKey = MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , new MybatisTransactionInterceptor("127.0.0.1" , 6379 , null));
		
		Achievements achievement = MybatisFactoryTools.getMapper(sessionKey, AchievementsMapper.class).selectByPrimaryKey(10101);
		achievement.setDescription("dsafasdfasdfasdfasdfsfasf事务");
		MybatisFactoryTools.getMapper(sessionKey, AchievementsMapper.class).updateByPrimaryKey(achievement);
	}

}
