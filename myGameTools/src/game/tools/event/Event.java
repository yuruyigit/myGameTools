package game.tools.event;

import java.util.Calendar;

public class Event 
{
	private String eventName;
	private int [] triggerArr ;
	private Runnable job;
	
	
	/**
	 * @param trigger 触发器条件,格式：年 月 日 时 分 秒 周。 未指定:"-" <br/>
	 * 如：("- - - 3 15 0 1")  则表示，每周一3点15分0秒去执行 <br/>
	 * 如：取消使用该时段则使用"-"来表示<br/>
	 * @param job 需要执行的任务/
	 */
	public Event(String trigger, Runnable job) 
	{
		this.eventName = trigger;
		String [] triggerStrArr = trigger.split(" ");
		this.triggerArr = new int [triggerStrArr.length];
		for (int i = 0; i < triggerStrArr.length; i++) 
		{
			String triggerStr = triggerStrArr[i];
			if(triggerStr.equals("-"))
				triggerArr[i] = -1;
			else
				triggerArr[i] = Integer.parseInt(triggerStr);
		}
		this.job = job;
	}
	
	/**
	 * @return 是否可以执行
	 */
	public boolean isExecute(int [] dateArr)
	{
//		System.out.println("dateArr = " + Arrays.toString(dateArr) + " triggerArr = " + Arrays.toString(triggerArr));
		
		boolean [] resultArr = new boolean[triggerArr.length];
		
		for (int i = 0; i < triggerArr.length; i++) 
		{
			int trigger = triggerArr[i];
			
			if(dateArr[i] == trigger || trigger == -1 )
				resultArr[i] = true;
		}
		
		for (boolean result : resultArr) 
		{
			if(result == false)
				return false;
		}
		
		return true;
	}
	
	public Runnable getRunable() {		return job;	}
	public String getEventName() {		return eventName;	}
	
}
