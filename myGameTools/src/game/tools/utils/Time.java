package game.tools.utils;

import java.util.Calendar;

public class Time
{
	private Calendar cal;
	
	public Time()
    {
		cal = Calendar.getInstance();
		setTime();
    }
	
	public Time(long time)
    {
		cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
    }
	
	public void setTime()
	{
		cal.setTimeInMillis(System.currentTimeMillis());
	}
	
	public void setTime(long timestamp)
	{
		cal.setTimeInMillis(timestamp);
	}
	
	public long getNowTime()
	{
		return cal.getTimeInMillis();
	}
	
	public int getYear()
	{
		return cal.get(Calendar.YEAR);
	}
	
	public int getMonth()
	{
		return cal.get(Calendar.MONTH) + 1;
	}
	
	public int getDayOfMonth()
	{
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public int getHours()
	{
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute()
	{
		return cal.get(Calendar.MINUTE);
	}

	public int getSecond()
	{
		return cal.get(Calendar.SECOND);
	}
	
	public int getDay()
	{
		return cal.get(Calendar.DATE);
	}
	
	
	@Override
	public String toString() 
	{
		return DateTools.getCurrentTimeString(cal.getTimeInMillis());
	}

}
