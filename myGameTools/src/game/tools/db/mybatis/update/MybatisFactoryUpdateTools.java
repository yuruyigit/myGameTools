package game.tools.db.mybatis.update;
import java.util.ArrayList;
import java.util.HashMap;
import game.data.conf.entity.Achievements;
import game.data.conf.entity.BookworldCommons;
import game.data.conf.entity.CardStarUps;
import game.data.conf.mapper.AchievementsMapper;
import game.data.conf.mapper.BookworldCommonsMapper;
import game.data.conf.mapper.CardStarUpsMapper;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.db.mybatis.plush.MybatisTransactionInterceptor;
import game.tools.db.mybatis.plush.transction.MybatisTransaction;
import game.tools.utils.Util;

public class MybatisFactoryUpdateTools
{
	private static final MybatisFactoryUpdateObject MYBATIS_FACTORY_UPDATE_OBJECT = new MybatisFactoryUpdateObject(1);
	
	public static int registerUpdate(long id , String mybatisFactorySessionKey, Object o)
	{
		return MYBATIS_FACTORY_UPDATE_OBJECT.registerUpdate(id, mybatisFactorySessionKey, o);
	}
	
	public static void update()
	{
		MYBATIS_FACTORY_UPDATE_OBJECT.update();
	}
	
	public static void update(long id)
	{
		MYBATIS_FACTORY_UPDATE_OBJECT.update(id);
	}
	
	public static void clear(long id)
	{
		MYBATIS_FACTORY_UPDATE_OBJECT.clear(id);
	}
	
	
	public static void main(String[] args)
	{
		try {
			
			MybatisTransactionInterceptor mybatisTransactionInterceptor = new MybatisTransactionInterceptor("127.0.0.1" , 6379 , null);
			
			String sessionKey = MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , mybatisTransactionInterceptor);
			String sessionKey1 = MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , mybatisTransactionInterceptor);
			
			CardStarUps cardStarUps = MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).selectByPrimaryKey(104);
			cardStarUps.setDescription(cardStarUps.getDescription() + "_test");
			
			MybatisTransaction.start();
//			cardStarUps.setId(Util.getRandomInt(99999, 9999999));
//			MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).insert(cardStarUps);
//			MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).deleteByPrimaryKey(106);
			cardStarUps.setDescription("；离开家阿萨德管理会计地方克拉斯地方了看到发生离开");
			MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).updateByPrimaryKey(cardStarUps);
//			MybatisTransaction.end();
			MybatisTransaction.rollback();
			
			Achievements achievement = MybatisFactoryTools.getMapper(sessionKey, AchievementsMapper.class).selectByPrimaryKey(10101);
			achievement.setDescription(achievement.getDescription() + "_test");
			Achievements achievement1 = MybatisFactoryTools.getMapper(sessionKey, AchievementsMapper.class).selectByPrimaryKey(10102);
			achievement1.setDescription(achievement1.getDescription() + "_test");
			
			ArrayList<BookworldCommons> commonList = MybatisFactoryTools.getMapper(MybatisFactoryTools.getFristSessionKey(), BookworldCommonsMapper.class).selectBy1000();
			for (BookworldCommons bookworldCommons : commonList) 
				bookworldCommons.setDescription(bookworldCommons.getDescription() + "_fortest");
			
			ArrayList<Achievements> list = new ArrayList<>();
			list.add(achievement);
			list.add(achievement1);
			
			HashMap<Integer, BookworldCommons> achMap = new HashMap<>();
			for (BookworldCommons bookworldCommons : commonList) 
				achMap.put(bookworldCommons.getId(), bookworldCommons);
			
			Thread.sleep(3000);
			
			new Thread(()->
			{
				MybatisFactoryUpdateTools.registerUpdate(1, sessionKey , cardStarUps);
			},"t1").start();
			
			new Thread(()->
			{
				MybatisFactoryUpdateTools.registerUpdate(1, sessionKey , list);
			},"t2").start();
			
			new Thread(()->
			{
				MybatisFactoryUpdateTools.registerUpdate(1, sessionKey , achMap);
			},"t3").start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
