package game.tools.threadpool;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 批量队列,该工具，则会异步在每秒中传递指定的数量列表。
 * @author zhibing.zhou
 * 
 */
public class ThreadQueueBatch 
{
	private final static  ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<Object>();
	
	/** 2016年4月28日上午11:47:26  单位 秒*/
	private final static int GAP_TIME = 10;			
	/** 2016年4月22日下午9:02:15  每秒检查发送的数量 ， 默认10个*/
	private int checkCount = 10;
	/** 2016年4月22日下午9:04:27 检查后发送执行的命令 */
	private IWork work;
	
	/**
	 * @param checkCount 每秒检查发送的数量 ， 默认10个
	 * @param exec  检查后发送执行的命令
	 */
	public ThreadQueueBatch(int checkCount , IWork work ) 
	{
		this.checkCount = checkCount;
		this.work = work;
		
		init();
	}
	
	private void init() 
	{
		initCheckThread();
	}
	
	private void initCheckThread()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				try 
				{
					ArrayList<Object> checkList = new ArrayList<Object>(checkCount);
					
					long execTime = System.currentTimeMillis();
					long nowTime=0;
					
					while(true)
					{
						while(!queue.isEmpty())
						{
							Object obj = get();
							
							if(checkList.size() < checkCount)			//添加指定数量的内容
								checkList.add(obj);
							if(checkList.size() >= checkCount)
							{
								executeWork(checkList);
								checkList.clear();
								
								execTime= System.currentTimeMillis();
							}
						}
						
						if( (nowTime-execTime) >= 1000*GAP_TIME && checkList.size()>0)
						{
							executeWork(checkList);
							checkList.clear();
							
							execTime= System.currentTimeMillis();
						}
						
						nowTime= System.currentTimeMillis();
						Thread.sleep(1000);
					}
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		},"ThreadQueueBatch"+this.toString()).start();
	}
	
	public void executeWork(ArrayList<Object> checkList)
	{
		if(checkList == null || checkList.size() == 0)
			return ;
		
		ArrayList<Object> workList = new ArrayList<Object>();
		workList.addAll(checkList);
		
		Threads.execute(new Runnable()
		{
			public void run() 
			{
				work.work(workList);
			}
		});
	}

	public void add(Object obj)
	{
		queue.offer(obj);
	}
	
	public <T> T get()
	{
		return (T)queue.poll();
	}
	
}
