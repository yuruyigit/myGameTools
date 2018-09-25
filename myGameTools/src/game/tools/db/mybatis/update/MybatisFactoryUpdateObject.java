package game.tools.db.mybatis.update;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.session.SqlSession;
import game.tools.db.mybatis.MybatisFactoryTools;
import game.tools.log.LogUtil;
import game.tools.threadpool.ThreadGroupFactory;

public class MybatisFactoryUpdateObject
{
	/** 更新的间隔时间，这里是5分钟。*/
	private long updateGapTime = 5 * 60 * 1000;
	/** 线程睡眠的时间  这里是10秒钟*/
	private long threadSleep = 10 * 1000;
	/**处理更新数据库的线程池数量*/
	private int threadCount = 10;
	/** 2018年5月3日 下午4:38:07 是否开始*/
	private boolean start;
	/** 2018年5月3日 下午4:55:18 id与更新对象的映射*/
	private ConcurrentHashMap<Long, Updates> updateObjectMap = new ConcurrentHashMap<Long, Updates>();
	
	private ExecutorService threadPool;
	
	public MybatisFactoryUpdateObject()
	{
		this(5 , 10);
	}
	
	/**
	 * @param updateGapTime 对应更新对象，多次执行更新的间隔。单位:分钟，默认为5分钟
	 */
	public MybatisFactoryUpdateObject(long updateGapTime)
	{
		this(updateGapTime , 10);
	}
	
	/**
	 * @param updateGapTime 对应更新对象，多次执行更新的间隔。单位:分钟，默认为5分钟
	 * @param threadCount 更新时，操作数据库的线程连接池的数量。默认为10。
	 */
	public MybatisFactoryUpdateObject(long updateGapTime , int threadCount)
	{
		this.updateGapTime = updateGapTime * 60 * 1000;
		this.threadCount = threadCount;
		
		init();
	}

	private void init() 
	{
		this.threadPool =  new ThreadPoolExecutor(threadCount , threadCount * 3, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>() ,new ThreadGroupFactory("MybatisFactoryUpdateObject-UpdateThread"), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * @param id 
	 * @param sqlSession
	 * @param o 传入的要管理更新数值对象，支持单个对象，list,map。
	 * @return
	 */
	public int registerUpdate(long id , String sessionKey, Object o )
	{
		try 
		{
			SqlSession sqlSession = MybatisFactoryTools.getSqlSession(sessionKey);
			
			if(sqlSession == null)
				throw new Exception("error : registerUpdate sqlSession is null !!!");
			
			Updates updates = getUpdates(id , sqlSession);
			
			if(updates == null)
				throw new Exception("error : registerUpdate updates is null , or id error !!!");
			
			updates.add(o);
			
			start();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
		
		return 1;
	}
	
	private synchronized Updates getUpdates(long id , SqlSession sqlSession)
	{
		Updates updates = updateObjectMap.get(id);
		if(updates == null)
		{
			updates = new Updates(id , sqlSession);
					
			updateObjectMap.put(updates.getId(), updates);
		}
		
		return updates;
	}
	
	private synchronized void start()
	{
		if(start)
			return ;
		
		if(!start)
		{
			start = true;
			execute();
		}
	}
	
	public void update(long id)
	{
		Updates updateObject = updateObjectMap.get(id);
		updateObject.update();
	}
	
	
	public void update()
	{
		for(Updates updateObject : updateObjectMap.values())
		{
			updateObject.update();
		}
	}
	
	public void clear(long id)
	{
		Updates updates = updateObjectMap.remove(id);
		if(updates != null)
			updates.distory();
	}
	
	private void execute() 
	{
		Thread t = new Thread(() -> 
		{
			try 
			{
				long nowTime = 0;
				
				long gapTime = 0;
				
				while(start)
				{
					nowTime = System.currentTimeMillis();
					
					for(Updates updateObject : updateObjectMap.values())
					{
						gapTime = nowTime - updateObject.getLastUpdateTime();
						
						if(gapTime < this.updateGapTime)
							continue ;
						
						this.threadPool.execute(()->
						{
							updateObject.update();
						});
					}
					
					Thread.sleep(threadSleep);
				}
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		} , "MybatisFactoryUpdateObject-Execute");
		
		t.start();
	}
}
