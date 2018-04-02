package game.tools.threadpool;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import game.tools.log.LogUtil;
import game.tools.utils.Time;

/**
 * 线程间隔调用，在每秒执行一次传入的回调函数。
 * @author zhibing.zhou
 * 
 */
public class ThreadInterval 
{
	private static boolean START = false;
	
	private static long SLEEP_GAP = 1000L;
			
	private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
	
	private static final ArrayList<IWork<Time>> WORK_LIST = new ArrayList<IWork<Time>>();

	private static final Time TIME = new Time();
	
	private static ThreadQueueSingle<Long> CHECK_QUEUE = new ThreadQueueSingle<Long>( (timestamp)-> 
	{
		TIME.setTime((long)timestamp);
		
		for (IWork<Time> work : WORK_LIST) 
		{
			THREAD_POOL.execute(()->
			{
				work.work(TIME); 
			});
		}
		
		
	}, "ThreadInterval-ThreadQueueSingle" );
	
	public static void addRunnable(IWork<Time> work)
	{
		WORK_LIST.add(work);
		
		if(WORK_LIST.size() > 0)
			start();
	}
	
	public static boolean removeRunnable(Runnable run)
	{
		return WORK_LIST.remove(run);
	}
	
	
	public static void stop()
	{
		if(!START)
			return;
		
		START = false;
		WORK_LIST.clear();
		THREAD_POOL.shutdown();
	}
	
	public static void start()
	{
		if(START)
			return ;
		
		START = true;
		execute();
	}
	
	
	private static void execute()
	{
		Thread t = new Thread(()->
		{
			try 
			{
				while(START)
				{
					CHECK_QUEUE.addQueue(System.currentTimeMillis());
					Thread.sleep(SLEEP_GAP);
				}
			}
			catch (Exception e) 
			{
				stop();
				LogUtil.error(e);
				e.printStackTrace();
				
			}
		},"ThreadInterval");
		
		t.start();
	}
	
	
	public static void main(String[] args) throws Exception
	{
		ThreadInterval.addRunnable((o)->{
			System.out.println("run " + o);
//			try {
//				Thread.sleep(3000L);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		});
	}
}
