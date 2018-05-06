package game.tools.db.mybatis.update;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;

import game.data.conf.entity.Achievements;
import game.data.conf.entity.BookworldCommons;
import game.data.conf.entity.CardStarUps;
import game.data.conf.mapper.AchievementsMapper;
import game.data.conf.mapper.BookworldCommonsMapper;
import game.data.conf.mapper.CardStarUpsMapper;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.db.mybatis.plush.MybatisSqlTimeInterceptor;

public class MybatisFactoryUpdateTools
{
	private static final MybatisFactoryUpdateObject MYBATIS_FACTORY_UPDATE_OBJECT = new MybatisFactoryUpdateObject(5);
	
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
	
	
	public static void main(String[] args) throws Exception
	{
		String sessionKey = MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , new MybatisSqlTimeInterceptor());
		
		CardStarUps cardStarUps = MybatisFactoryTools.getMapper(sessionKey, CardStarUpsMapper.class).selectByPrimaryKey(101);
		cardStarUps.setDescription(cardStarUps.getDescription() + "_test");
		
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
		 
		Thread.sleep(5000);
		
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
	}
}
