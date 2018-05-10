package game.tools.event;

public class Event 
{
	private String eventName;
	private int [] triggerArr ;
	private Runnable job;
	
	/** 2018年5月10日 下午4:56:05  表示不判断这个数值*/
	private static final int EMPTY = 99;
	/** 2018年5月10日 下午5:00:31 表示不判断这个数值的字符串*/
	private static final String EMPTY_STRING = "*";
	
	/**
	 * @param trigger 触发器条件,格式：年 月 日 时 分 秒 周, 未指定:"*"。 <br/>
	 * <b>注意：正整数为绝对时间点触发，负整数为指定间隔点触发。</b> <br/>
	 * 如：("* * * 3 15 0 1") 则表示，每周一3点15分0秒去执行 。<br/>
	 * 如：("* * * -3 * * *") 则表示，每隔3个小时去执行次。<br/>
	 * 如：("* * * -3 -5 * *") 则表示，每隔3个小时内每5分钟去执行次。<br/>
	 * 如：取消使用该时段则使用"*"来表示。<br/>
	 * @param job 需要执行的任务
	 */
	public Event(String trigger, Runnable job) 
	{
		this.eventName = trigger;
		String [] triggerStrArr = trigger.split(" ");
		this.triggerArr = new int [triggerStrArr.length];
		
		for (int i = 0; i < triggerStrArr.length; i++) 
		{
			String triggerStr = triggerStrArr[i];
			
			if(triggerStr.equals(EMPTY_STRING))
				triggerArr[i] = EMPTY;
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
			
			if(trigger == EMPTY)
			{
				resultArr[i] = true;
			}
			else if(trigger >= 0)
			{
				if(dateArr[i] == trigger)
					resultArr[i] = true;
			}
			else if(trigger < 0)
			{
				if(dateArr[i] % trigger == 0)
					resultArr[i] = true;
			}
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
