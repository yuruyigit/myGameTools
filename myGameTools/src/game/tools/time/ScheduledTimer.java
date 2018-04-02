package game.tools.time;

import java.util.Calendar;

import game.tools.threadpool.IWork;
import game.tools.threadpool.ThreadQueueSingle;

public class ScheduledTimer extends Thread
{
	private IWork work;
	
	private boolean start;
	
	private static ThreadQueueSingle<Timer> checkQueue;
	
	public ScheduledTimer(IWork work) 
	{
		this.work = work;
		
		init();
	}
	
	private void init() 
	{
		checkQueue = new ThreadQueueSingle<Timer>(work , "check-work"); 
	}


	public void start()
	{
		this.start = true;
		super.start();
	}
	
	public void end()
	{
		this.start = false;
	}

	@Override
	public void run() 
	{
		try 
		{
			while(start)
			{
				Timer timer = getTimer();
				
				checkQueue.addQueue(timer);
				
				Thread.sleep(1000L);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	private Timer getTimer()
	{
		Calendar cal = Calendar.getInstance(); 
		
		Timer timer = new Timer(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
				cal.getTimeInMillis());
		
		return timer;
	}
}
