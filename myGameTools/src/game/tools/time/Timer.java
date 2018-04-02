package game.tools.time;

public class Timer 
{
	private int year;
	private int month;
	private int day;
	
	private int hour;
	private int minute;
	private int second;
	
	private long timestamp ;
	
	
	public Timer(int year , int month , int day , int hour , int minute , int second , long timestamp) 
	{
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		
		this.timestamp = timestamp;
	}


	public int getYear() {
		return year;
	}



	public int getMonth() {
		return month;
	}



	public int getDay() {
		return day;
	}



	public int getHour() {
		return hour;
	}



	public int getMinute() {
		return minute;
	}



	public int getSecond() {
		return second;
	}


	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() 
	{
		return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
	}

}
