package game.tools.db.mybatis.update;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ibatis.session.SqlSession;
import game.data.conf.entity.Achievements;
import game.data.conf.entity.BookworldCommons;
import game.data.conf.entity.CardStarUps;
import game.data.conf.mapper.AchievementsMapper;
import game.data.conf.mapper.BookworldCommonsMapper;
import game.data.conf.mapper.CardStarUpsMapper;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.db.mybatis.plush.MybatisSqlTimeInterceptor;

public class MybatisFactoryUpdate
{
	/** 更新的间隔时间，这里是5分钟。*/
	private static long UPDATE_GAP_TIME = 5 * 60 * 1000;
	/** 线程睡眠的时间  这里是10秒钟*/
	private static long THREAD_SLEEP = 10 * 1000;
	/** 2018年5月3日 下午4:38:07 是否开始*/
	private static boolean START;
	/** 2018年5月3日 下午4:55:18 id与更新对象的映射*/
	private static final ConcurrentHashMap<Long, Updates> UPDATE_OBJECT_MAP = new ConcurrentHashMap<Long, Updates>();
	
	private static final Object LOCK = new Object() , START_LOCK = new Object();
	
	public static int registerUpdate(long id , SqlSession sqlSession, Object o )
	{
		Updates updates = getUpdates(id , sqlSession);
		
		if(updates == null)
			return 0;
		
		updates.add(o);
		
		start();
		
		return 1;
	}
	
	private static Updates getUpdates(long id , SqlSession sqlSession)
	{
		Updates updates = UPDATE_OBJECT_MAP.get(id);
		if(updates == null)
		{
			synchronized(LOCK) 
			{
				updates = UPDATE_OBJECT_MAP.get(id);
				
				if(updates == null)
				{
					updates = new Updates(id , sqlSession);
					
					UPDATE_OBJECT_MAP.put(updates.getId(), updates);
				}
			}
		}
		
		return updates;
	}
	
	private static void start()
	{
		if(!START)
		{
			synchronized (START_LOCK) 
			{
				if(!START)
				{
					START = true;
					execute();
				}
			}
		}
	}
	
	public static void update()
	{
		for(Updates updateObject : UPDATE_OBJECT_MAP.values())
		{
			updateObject.update();
		}
	}
	
	public static void clear(long id)
	{
		Updates updates = UPDATE_OBJECT_MAP.remove(id);
		if(updates != null)
			updates.distory();
	}
	
	private static void execute() 
	{
		Thread t = new Thread(() -> 
		{
			try 
			{
				while(START)
				{
					long nowTime = System.currentTimeMillis();
					
					for(Updates updateObject : UPDATE_OBJECT_MAP.values())
					{
						updateObject.update(nowTime);
					}
					Thread.sleep(THREAD_SLEEP);
				}
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		} , "MybatisFactoryUpdate-execute");
		
		t.start();
	}


	public static long getUPDATE_GAP_TIME() 
	{
		return UPDATE_GAP_TIME;
	}

	/**
	 * @param updateGapTime 设置更新间隔时间，这里默认为5分钟
	 */
	public static void setUPDATE_GAP_TIME(long updateGapTime) 
	{
		UPDATE_GAP_TIME = updateGapTime * 60 * 1000;
	}
	
	public static void main(String[] args)
	{
		MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://127.0.0.1:3306/static_dev" ,"root", "root" , new MybatisSqlTimeInterceptor());
		
		CardStarUps cardStarUps = MybatisFactoryTools.getMapper(MybatisFactoryTools.getFristSessionKey(), CardStarUpsMapper.class).selectByPrimaryKey(101);
		cardStarUps.setDescription(cardStarUps.getDescription() + "_test");
		
		Achievements achievement = MybatisFactoryTools.getMapper(MybatisFactoryTools.getFristSessionKey(), AchievementsMapper.class).selectByPrimaryKey(10101);
		achievement.setDescription(achievement.getDescription() + "_test");
		Achievements achievement1 = MybatisFactoryTools.getMapper(MybatisFactoryTools.getFristSessionKey(), AchievementsMapper.class).selectByPrimaryKey(10102);
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
		 
		MybatisFactoryUpdate.setUPDATE_GAP_TIME(1);
		MybatisFactoryUpdate.registerUpdate(1, MybatisFactoryTools.getSqlSession(MybatisFactoryTools.getFristSessionKey()) , cardStarUps);
		MybatisFactoryUpdate.registerUpdate(1, MybatisFactoryTools.getSqlSession(MybatisFactoryTools.getFristSessionKey()) , list);
		MybatisFactoryUpdate.registerUpdate(1, MybatisFactoryTools.getSqlSession(MybatisFactoryTools.getFristSessionKey()) , achMap);
	}
}
