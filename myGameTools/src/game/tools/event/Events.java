package game.tools.event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import game.tools.log.LogUtil;
import game.tools.threadpool.ThreadGroupFactory;
import game.tools.utils.DateTools;

public class Events 
{
	
	private static final List<Event> EVENT_LIST =  Collections.synchronizedList(new ArrayList<Event>(5));

	private static final Calendar CALE = Calendar.getInstance();
	
	private static ExecutorService THREAD_POOL;
	
	private static boolean START ;
	
	private static Thread EVENT_THREAD ;
	
	private static String EVENT_THREAD_NAME = "Events.execute";
	
	static
	{
		checkExecuteWorkThread();
	}
	
	public static void addEvent(Event event)
	{
		EVENT_LIST.add(event);
		
		if(EVENT_LIST.size() > 0)
			start();
	}
	
	public static void addEvents(Event ...events)
	{
		for (Event event : events)
			EVENT_LIST.add(event);
		
		if(EVENT_LIST.size() > 0)
			start();
	}
	
	
	public static boolean removeEvent(Event event)
	{
		return EVENT_LIST.remove(event);
	}
	
	public synchronized static void start()
	{
		if(START)
			return ;
		
		startup();
		execute();
	}
	
	private static void startup()
	{
		stopExistThread(EVENT_THREAD_NAME);			//在开始的时候，检测是否已经存在线程，如果存在则关掉。 
		START = true;
	}
	
	private static void closeup()
	{
		START = false;
	}
	
	public synchronized static void stop()
	{
		closeup();
		
		EVENT_LIST.clear();
		
		if(THREAD_POOL != null)
			THREAD_POOL.shutdown();
		
		THREAD_POOL = null;
		
		if(EVENT_THREAD != null)
			EVENT_THREAD.interrupt();
		
		EVENT_THREAD = null;
	}
	
	/**
	 * @return 返回年月日时分秒周数组
	 */
	private static int []  getDate()
	{
//		Calendar cale = Calendar.getInstance();
//		cale.setFirstDayOfWeek(Calendar.MONDAY);
		
		int [] dateArr = new int[7];
		
		Calendar cale = CALE;
		cale.setTimeInMillis(System.currentTimeMillis());
		
		dateArr[0] = cale.get(Calendar.YEAR);
		dateArr[1] = cale.get(Calendar.MONTH) + 1;
		dateArr[2] = cale.get(Calendar.DAY_OF_MONTH);
		dateArr[3] = cale.get(Calendar.HOUR_OF_DAY);
		dateArr[4] = cale.get(Calendar.MINUTE);
		dateArr[5] = cale.get(Calendar.SECOND);
		dateArr[6] = getWeekDay(cale);
		
		return dateArr;
	}
	
	private static int getWeekDay(Calendar cale)
	{
		int week =  cale.get(Calendar.DAY_OF_WEEK);
//		return week;
		
		switch (week) 
		{
		  case 1:
//			 Tools.println("星期日");
			 return 7;
		  case 2:
//			 Tools.println("星期一");
			 return 1;
		  case 3:
//		   	 Tools.println("星期二");
			 return 2;
		  case 4:
//			 Tools.println("星期三");
			 return 3;
		  case 5:
//			 Tools.println("星期四");
			 return 4;
		  case 6:
//		  	 Tools.println("星期五");
			 return 5;
		  case 7:
//		   	 Tools.println("星期六");
			 return 6;
		  }
		
		return -1;
	}
	
	private static void execute()
	{
		EVENT_THREAD = new Thread(()-> 
		{
			try 
			{
				while(START)
				{
					int [] dateArr = getDate();
					
					synchronized (EVENT_LIST) 
					{
						for (Event event : EVENT_LIST) 
						{
							if(event.isExecute(dateArr))
							{
								THREAD_POOL.execute(event.getRunable());
							}
						}
					}
					
					Thread.sleep(1000L);
				}
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
				LogUtil.error(e);
			}
		} , EVENT_THREAD_NAME);
		
		EVENT_THREAD.start();
	}
	
	private static void checkExecuteWorkThread()
	{
		if(THREAD_POOL == null)
		{
			synchronized(CALE)
			{
				if(THREAD_POOL == null)
				{
					int cpuCount = Runtime.getRuntime().availableProcessors();
					THREAD_POOL = new ThreadPoolExecutor(cpuCount, cpuCount * 3, 60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>() , new ThreadGroupFactory(EVENT_THREAD_NAME));
				}
			}
		}
	}
	
	private static void stopExistThread(String threadName)
	{
		for (Map.Entry<Thread,StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet())
        {
            Thread thread = entry.getKey();
            
            if(thread.getName().equalsIgnoreCase(threadName))
            {
            	thread.interrupt();
            	break;
            }
        }
	}
	
	public static boolean isSTART() 
	{
		return START;
	}
	
	
	public static String eventInfo()
	{
		String eventName = "";
		
		for (Event event : EVENT_LIST) 
		{
			eventName += " [" + event.getEventName() + "] , ";
		}
		return eventName;
		
	}
	
	public static void main(String[] args) 
	{
		
//		int dateArr [] = getDate();
//		System.out.println(Arrays.toString(dateArr));
		
		Events.addEvents(
				new Event(()->
				{
					System.out.println("测试事件1 " + DateTools.getCurrentTimeMSString() );
				} , 
				"* * * * -2 0 *", 
				"* * * * -5 10 *"),
				new Event(()->
				{
					System.out.println("测试事件2 " + DateTools.getCurrentTimeMSString() );
				} , 
				"* * * * -2/-5 0/10 *"));
		
//		Events.addEvent(new Event("* * -2 0 0 0 *", ()->{
//			System.out.println("每隔2天0点0分0分，执行开启赛季时间。" + DateTools.getCurrentTimeMSString());
//		}));
		
		
//		Events.addEvent(new Event("* * * * -3 5 *", new Runnable() {
//			public void run() {
//				System.out.println("Event 每3分钟隔到时间5秒执行 " + DateTools.getCurrentTimeMSString());
//			}
//		}));
//		Events.addEvent(new Event("* * * * * 2 *", new Runnable() {
//			public void run() {
//				System.out.println("Event 时间秒到2执行 " + DateTools.getCurrentTimeMSString());
//			}
//		}));
		
//		Events.start();
//		String sb = Threads.getAllThreadInfo();
//		System.out.println(sb);
	}
}
