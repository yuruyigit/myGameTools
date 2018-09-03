package game.tools.event;
import java.util.ArrayList;
import java.util.Arrays;
import com.alibaba.fastjson.JSONObject;

public class Event 
{
	/** 2018年5月10日 下午4:56:05  表示不判断这个数值 , 要表示的日期时间的数组长度*/
	private static final int EMPTY = 99 , DATE_TIME_LEGNTH = 7;
	/** 2018年5月10日 下午5:00:31 表示不判断这个数值的字符串*/
	
	private String eventName;
	
	private int [][] triggerArray;
	
	private boolean [] triggerResultArray;
	
	private Runnable job;
	
	private static final String EMPTY_STRING = "*";
	
	private boolean [] resultArr = null;
	
	private int [] triggers = null;
	
	private boolean isOk = false;
	
	private byte okCount = 0;
	
	private int trigger = 0;
	
	/**
	 * @param work 需要执行的任务。
	 * @param trigger 触发器条件,格式：年 月 日 时 分 秒 周, 未指定:"*", 多条件:"/"。 <br/>
	 * <b>注意：正整数为绝对时间点触发，负整数为指定间隔点触发。</b> <br/>
	 * 如：("* * * 3 15 0 1") 则表示，每周一3点15分0秒去执行 。<br/>
	 * 如：("* * * -3 * * *") 则表示，每隔3个小时去执行次。<br/>
	 * 如：("* * * -3 -5 * *") 则表示，每隔3个小时内每5分钟去执行次。<br/>
	 * 如：("* * * * -2/-6 0 *") 则表示，每隔2分和6分去执行次。<br/>
	 * 如：取消使用该时段则使用"*"来表示。<br/>
	 * <b>注意：如果在同一个Event里面配置，多条相同的事件触发点，则会执行最前配置。</b><br/>
	 * <t>如：("* * * * -2/-6 0 *")该配置则有可能在42分钟的时间点来触发，<br/>
	 * -2与-6，则默认只执行-2的一次事件点。</t>
	 */
	public Event(EventWork work , String ...triggerArray) 
	{
		this.eventName = getName(triggerArray);
		this.triggerResultArray = new boolean [DATE_TIME_LEGNTH];
		
		if(triggerArray.length == 1)
			parseTriggers(triggerArray[0]);
		else
			parseTriggerArray(triggerArray);
		
		initJob(work);
		
//		printTrigger();
	}
	
	private String getName(String ...triggerArray)
	{
		StringBuffer triggerString = new StringBuffer();
		String string = null;
		
		for (int i = 0; i < triggerArray.length; i++) 
		{
			string = triggerArray[i];
			if(i < triggerArray.length - 1 )
				triggerString.append(string).append(",");
			else
				triggerString.append(string);
		}
		
		return eventName = triggerString.toString();
	}
	
	private void initJob(final EventWork work)
	{
		final Event thisObject = this;
		this.job = new Runnable() 
		{
			@Override
			public void run() 
			{
				work.work(thisObject);
			}
		};
	}
	
	private void parseTriggerArray(String []triggerArray)
	{
		String trigger = null;
		
		this.triggerArray = new int [triggerArray.length][DATE_TIME_LEGNTH];
		
		for (int i = 0 ; i < triggerArray.length; i++) 
		{
			trigger = triggerArray[i];
			
			trigger = correctTrigger(trigger);
			
			String [] triggerStrArr = trigger.split(" ");
			
			for (int j = 0; j < triggerStrArr.length; j++) 
			{
				String triggerStr = triggerStrArr[j];
				
				if(triggerStr.equals(EMPTY_STRING))
					this.triggerArray[i][j] = EMPTY;
				else
					this.triggerArray[i][j] = Integer.parseInt(triggerStr);
			}
		}
	}
	
	private void parseTriggers(String trigger)
	{
		trigger = correctTrigger(trigger);
		
		String [] triggerStrArr = trigger.split(" ");
		
		ArrayList<int[]> triggerList = new ArrayList<int[]>(10);
		
		int size = 0 , maxSize = 0;
		
		for (String triggerStr : triggerStrArr) 
		{
			String [] array = triggerStr.split("/");
			size = array.length;
			
			if(maxSize < size)
				maxSize = size;
		}
		
		for (int i = 0; i < maxSize; i++) 
		{
			int [] triggerArray = new int[triggerStrArr.length];
			
			for (int j = 0; j < triggerStrArr.length; j++) 
			{
				String triggerStr = triggerStrArr[j];
				
				if(triggerStr.equals(EMPTY_STRING))
					triggerArray[j] = EMPTY;
				else
				{
					String [] array = triggerStr.split("/");
					
					if(array.length == 1)
						triggerArray[j] = Integer.parseInt(array[0]);
					else if(i < array.length)
						triggerArray[j] = Integer.parseInt(array[i]);
					else
						triggerArray[j] = EMPTY;
				}
			}
			triggerList.add(triggerArray);
		}
		
		this.triggerArray = new int[triggerList.size()][DATE_TIME_LEGNTH];
	
		for (int i = 0; i < triggerList.size(); i++) 
			this.triggerArray[i] = triggerList.get(i); 
	}
	
	/**
	 * @return 返回修正后的trigger
	 */
	private String correctTrigger(String trigger)
	{
		String [] triggerStrArr = trigger.split(" ");
		
		boolean start = false;
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < triggerStrArr.length; i++) 
		{
			String triggerString = triggerStrArr[i];
			
			if(!triggerString.equals("*"))
				start = true;
			else
			{
				if(start)
				{
					if(i == 1)		//月
						triggerString = "1";
					else if(i == 2)	//日
						triggerString = "1";
					else if(i == 3)	//时
						triggerString = "0";
					else if(i == 4)	//分
						triggerString = "0";
					else if(i == 5)	//秒
						triggerString = "0";
				}
			}
			sb.append(triggerString + " ");
		}
		
		return sb.toString().trim();
	}
	
	private void printTrigger()
	{
		System.out.println(JSONObject.toJSONString(triggerArray , true));
	}
	
	
	/**
	 * @return 是否可以执行
	 */
	public boolean isExecute(int [] dateArr)
	{
//		dateArr = new int [] { 0 , 0 , 0 ,11 , 42 , 0 ,4};
		for (int i = 0; i < triggerArray.length; i++) 
		{
			triggers = triggerArray[i];
			
			resultArr = clearResultArray();
			
			okCount = 0; 
			
			isOk = false;
			
			for (int j = 0; j < triggers.length; j++) 
			{
				trigger = triggers[j];
				
				if(trigger == EMPTY)
				{
					resultArr[j] = true;
				}
				else if(trigger >= 0)
				{
					if(dateArr[j] == trigger)
						resultArr[j] = true;
				}
				else if(trigger < 0)
				{
					if(dateArr[j] % trigger == 0)
						resultArr[j] = true;
				}
			}
			
			for (boolean result : resultArr) 
			{
				if(result)
					okCount++;
			}
			
			if(okCount >= resultArr.length)
			{
				isOk = true;
				break;
			}
		}
		
		return isOk;
	}
	
	private boolean [] clearResultArray() 
	{
		for (int i = 0; i < triggerResultArray.length; i++) 
		{
			this.triggerResultArray[i] = false;
		}
		
		return triggerResultArray;
	}

	public Runnable getRunable() {		return job;	}
	public String getEventName() {		return eventName;	}
	
}
