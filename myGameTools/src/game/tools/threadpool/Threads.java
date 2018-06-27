package game.tools.threadpool;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zzb
 */
public class Threads 
{
	private static ExecutorService THREAD_POOL = null;
	/*** zzb  线程最小数量*/
	private static final int MIN_THREAD_COUNT = 25;
	
	static
	{
		int threadCount = (int)(Runtime.getRuntime().availableProcessors() * 3);
		threadCount = threadCount < MIN_THREAD_COUNT ? MIN_THREAD_COUNT : threadCount;
		THREAD_POOL = Executors.newFixedThreadPool(threadCount);
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 10, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}
	
	/**
	 * zzb  线程池内执行函数
	 * @param run 
	 */
	public static void execute(Runnable run)
	{
		THREAD_POOL.execute(run);
	}
	
	/**
	 * @param checkCount 每秒检查发送的数量 ， 默认10个
	 * @param work  检查后发送执行的命令
	 * @return 返回一个批量对列
	 */
	public static ThreadQueueBatch createThreadQueueBatch(int checkCount , IWork work)
	{
		return new ThreadQueueBatch(checkCount, work);
	}
	
	

	/**
	 * @return 返回所有线程信息
	 */
	public static String getAllThreadInfo()
	{
		StringBuffer sb = new StringBuffer();
		
		for (Map.Entry<Thread,StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet())
        {
            Thread thread = entry.getKey();

            StackTraceElement[] stackTraceElements = entry.getValue();

            if (thread.equals(Thread.currentThread()))
            {
                continue;
            }

            
            sb.append("线程： ").append(thread.getName()).append(" ").append(thread.getState().toString()).append("\n");
            
            for (StackTraceElement element : stackTraceElements)
            {
            	sb.append("\t").append(element).append("\n");
            }
            sb.append("\n");
        }
		
		return sb.toString();
	}
	
	
	public static void main(String[] args) throws Exception 
	{
		ExecutorService es = Executors.newSingleThreadExecutor();
		
		Future<Integer> f = es.submit(new Callable<Integer>() 
		{
			@Override
			public Integer call() throws Exception 
			{
				Thread.sleep(3000);
				return 10;
			}
		});
		
//		System.out.println("f.isDone() = " + f.isDone());
		while(!f.isDone())
		{
			System.out.print("*");
			Thread.sleep(100);
		}
		
		System.out.println("结果:" + f.get());
		
		es.shutdown();
		
//		Thread.sleep(1000000L);
	}
}

