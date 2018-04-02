package game.tools.fork;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;
import com.alibaba.fastjson.JSONObject;

import game.tools.log.LogUtil;

public class ForkJoinTools
{
	private Integer threadCount = new Integer(3);
	
	private ForkJoinPool forkJoinPool;
	
	private HashMap<Long, Object> map = new HashMap<>();
	
	public ForkJoinTools()
	{
		
	}
	
	public ForkJoinTools(int threadCount)
	{
		this.threadCount = threadCount;
	}
	
	public void addTasks(SubForkTask ...subTask)
	{
		long threadId = Thread.currentThread().getId();
		
		RootForkTask rootForkTask = (RootForkTask)this.map.get(threadId);
		
		if(rootForkTask == null)
		{
			rootForkTask = new RootForkTask();
			this.map.put(threadId, rootForkTask);
		}
		
		rootForkTask.addTasks(subTask);

		createForkJoinPool();
	}
	
	public <T> T submit()
	{
		try
		{
			if(forkJoinPool == null)
			{
				throw new Exception("ForckJoinPool Is Null !!");
			}
			
			if(forkJoinPool.isTerminated())				//如果已经结束了
			{
				throw new Exception("ForckJoinPool Is Terminated !!");
			}
			
			Object object = map.remove(Thread.currentThread().getId());
			
			if(object != null)
			{
				if(object instanceof RootForkTask)
				{
					RootForkTask rootForkTask = (RootForkTask)object;
					
					forkJoinPool.submit(rootForkTask);
					
					T result = (T)rootForkTask.get();
					
					rootForkTask.clearTask();
					
					return  result;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
		
		return null;
	}
	
	private ForkJoinPool createForkJoinPool()
	{
		if(forkJoinPool == null)
		{
			synchronized (threadCount) 
			{
				if(forkJoinPool == null)
					forkJoinPool = new ForkJoinPool(threadCount);
			}
		}
		
		return forkJoinPool;
	}
	
	/**
	 *  结束
	 */
	public void terminated()
	{
		if(forkJoinPool != null)
		{
			forkJoinPool.shutdown();
			forkJoinPool = null;
		}
		
		if(map != null)
		{
			map.clear();
			map = null;
		}
	}
	
	private static ForkJoinTools jlt = new ForkJoinTools(5);
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("main 1 2 3 ");
		for (int i = 0; i < 1; i++) 
		{
			Thread t = new Thread(()->{
				t1();
			});
			
			Thread t1 = new Thread(()->{
				t2();
			});
			
			t.start();
			t1.start();
		}
//		t2.start();
		
//		jlt.terminated();
		
//		t1();
//		t2();
		
		Thread.sleep(5000L);
	}
	
	private static void t2()
	{
		SubForkTask sft = new SubForkTask()
		{
			@Override
			protected String compute()
			{
				try
				{
					Thread.sleep(1000L);
					
					System.out.println("t2 compute " + Thread.currentThread().getName());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return "t2-sft";
			}
		};
		
		
		long startTime = System.currentTimeMillis();
		
		jlt.addTasks(sft , sft, sft, sft);
		
		Object o = jlt.submit();
		
		long endTime = System.currentTimeMillis();
		
		
		System.out.println("t2 gapTime = " + (endTime - startTime) + " o = " + JSONObject.toJSONString(o));
		
	}
	
	
	private static void t1()
	{
		SubForkTask sft = new SubForkTask()
		{
			@Override
			protected String compute()
			{
				try
				{
					Thread.sleep(1000L);
					
					System.out.println("t1 compute " + Thread.currentThread().getName());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return "t1-sft";
			}
		};
		
		SubForkTask sft1 = new SubForkTask()
		{
			@Override
			protected String compute()
			{
				try
				{
					Thread.sleep(1500L);
					
					System.out.println("t1 compute " + Thread.currentThread().getName());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return "t1-sft1";
			}
		} ;
		
		long startTime = System.currentTimeMillis();
		
		jlt.addTasks(sft , sft1, sft, sft, sft);
		
		Object o = jlt.submit();
		
		long endTime = System.currentTimeMillis();
		
		
		System.out.println("t1 gapTime = " + (endTime - startTime) + " o = " + JSONObject.toJSONString(o));
	}
}
