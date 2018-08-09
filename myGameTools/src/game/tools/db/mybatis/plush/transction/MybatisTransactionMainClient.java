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
import game.tools.utils.Util;

public class MybatisTransactionMainClient 
{
	public static void main(String[] args)
	{
		test1();
	}
	
	private static void test1()
	{
		String sessionKey = MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , new MybatisTransactionInterceptor("127.0.0.1" , 6379 , null));
		
		CardStarUps cardStarUps = MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).selectByPrimaryKey(104);
		cardStarUps.setDescription(cardStarUps.getDescription() + "_test");
		
		String transactionId = MybatisTransaction.start();
		
		cardStarUps.setDescription("测试事务");
		MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).updateByPrimaryKey(cardStarUps);
		
		
		String result = HttpTools.sendPost("http://localhost:8888/transaction/HttpCmdServlet", 1111, transactionId);			//另个服务器继续执行该事务
		
		cardStarUps = new CardStarUps();
		cardStarUps.setId(Util.getRandomInt(99999, 9999999));
		cardStarUps.setDescription("炒在，央工基夺七我五上主主人工土加盟协fjlafj;af");
		
		MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).insert(cardStarUps);
		
		MybatisTransaction.end();
//		MybatisTransaction.rollback();
	}
}
