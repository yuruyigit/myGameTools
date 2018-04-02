package game.tools.threadpool;

import java.util.ArrayList;

/**
 * 该工具，检测列表中是存在数据，获取并移除当前这个传递给回调
 * @author zhibing.zhou
 * @param <T>
 */
public class ThreadQueueSingle<T>
{
	/*** zzb 最小的单执行数量 */
	private static int MIN_CHECK_EXEC_SIZE = 10;
	private static int CHECK_EXEC_SIZE = MIN_CHECK_EXEC_SIZE , CHECK_EXEC_SLEEP = 100;
	
	private  Thread checkThread ;
	private  ArrayList<T> checkList= new ArrayList<T>(10);
	
	/*** zzb  工作内容*/
	private IWork<T> workContent ;
	private String name;
	public ThreadQueueSingle(IWork<T> workContent , String name) 
	{
		this.workContent = workContent;
		this.name = name;
		
		if(this.workContent != null)
			executeCheck();
	}
	
	public void addQueue(T t)
	{
		checkList.add(t);
		if(checkThread != null)
		{
			if(checkThread.getState() == Thread.State.WAITING)
			{
				synchronized (checkThread)
				{
					checkThread.notify();
				}
			}
		}
	}
	
	/**
	 * zzb 单次执行的数量大小 
	 * @param size 
	 */
	public void setExecSize(int size)
	{
		CHECK_EXEC_SIZE = size < MIN_CHECK_EXEC_SIZE ? MIN_CHECK_EXEC_SIZE : size;
	}
	
	private void executeCheck()
	{
		checkThread = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				while(true)
				{
					try 
					{
						if(checkList.size() <= 0 )
						{
							synchronized (checkThread)
							{
								checkThread.wait();
							}
						}
						
						for (int i = 0; i < CHECK_EXEC_SIZE; i++)
						{
							if(i >= checkList.size())
								break;
							
							T o = checkList.remove(0);
							
							workContent.work(o);
						}
						
						Thread.sleep(CHECK_EXEC_SLEEP);
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		},name);
		
		checkThread.start();
	}
	
}
