package game.tools.utils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
public class DateTools 
{
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd"), 
										   DTF_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") , 
										   DTF_TIMES = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static String getCurrentDateString() {		return DTF.format(LocalDateTime.now());	}
	public static String getCurrentTimeString() {		return DTF_TIME.format(LocalDateTime.now());	}
	public static String getCurrentTimeMSString() {		return DTF_TIMES.format(LocalDateTime.now());	}
	
	public static String getCurrentDateString(long time) {		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time) , ZoneId.systemDefault()).format(DTF);	}
	public static String getCurrentTimeString(long time) {		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time) , ZoneId.systemDefault()).format(DTF_TIME);	}
	public static String getCurrentTimeMSString(long time) {		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time) , ZoneId.systemDefault()).format(DTF_TIMES);	}
	
	public static long getCurrentDateLong(String date)	{		return LocalDate.parse(date,DTF).atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();	}
	public static long getCurrentTimeLong(String dateTime)	{		return LocalDateTime.parse(dateTime,DTF_TIME).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();	}
	
	public static long getCurrentTimeLong() {		return System.currentTimeMillis();	}
	
	
	
	/** @return 返回月份中的日期号*/
	public static int getMonthDay() 
	{
		Calendar CALENDAR = Calendar.getInstance();
		return CALENDAR.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * zzb 2014-4-29 下午8:51:27
	 * 返回是否是同一天
	 * @param time1	时间点1
	 * @param time2  时间点2
	 * @return 是否是同一天
	 */
	public static boolean isSameDay(long time1, long time2)
	{
		if(time1 == 0 && time2 != 0 || 
				time1 != 0 && time2 == 0)
			return false;
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(time2);
		
		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	    boolean isSameMonth = isSameYear&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
	    boolean isSameDate = isSameMonth&& cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
	    
	    return isSameDate;
	       
	}
	
	
	/**@return 返回当前星期几 */
	public static int getWeekDay()
	{
		Calendar CALENDAR = Calendar.getInstance();
		return getWeekDay(CALENDAR);
	}
	
	/** @return 返回指定时间戳是星期几*/
	public static int getWeekDay(long time)
	{
		Calendar CALENDAR = Calendar.getInstance();
		CALENDAR.setTimeInMillis(time);
		return getWeekDay(CALENDAR);
	}
	
	private static int getWeekDay(Calendar cale)
	{
		int week =  cale.get(Calendar.DAY_OF_WEEK);
		
		switch (week) 
		{
			  case 1: return 7;			//星期日
			  case 2: return 1;			//星期一
			  case 3: return 2;			//星期二
			  case 4: return 3;			//星期三
			  case 5: return 4;			//星期四
			  case 6: return 5;			//星期五
			  case 7: return 6;			//星期六
		 }
		
		return -1;
	}
	
	
	/**
	 * zzb 2015年4月15日上午10:19:51 <br/> 
	 * 判断两个时间点字符串，是否是同一工作周 格式：yyyy-MM-dd
	 * @param date1 日期点1字符串
	 * @param date2 日期点2
	 * @return 
	 */
	public static boolean isSameWeek(String date1, String date2) 
	{
		long long1 = DateTools.getCurrentDateLong(date1);
		long long2 = DateTools.getCurrentDateLong(date2);
				
		return isSameWeek(long1, long2);
	}
	
	/**
	 * zzb 2015年4月15日上午10:19:51 <br>
	 * 判断两个时间点，是否是同一工作周
	 * @param time1 时间戳1
	 * @param time2 时间戳2
	 * @return 
	 */
	public static boolean isSameWeek(long time1, long time2) 
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);
		cal1.setFirstDayOfWeek(Calendar.MONDAY);
		cal2.setFirstDayOfWeek(Calendar.MONDAY);
		
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		// subYear==0,说明是同一年
		if (subYear == 0) 
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		// 例子:cal1是"2005-1-1"，cal2是"2004-12-25"
		// java对"2004-12-25"处理成第52周
		// "2004-12-26"它处理成了第1周，和"2005-1-1"相同了
		// 大家可以查一下自己的日历
		// 处理的比较好
		// 说明:java的一月用"0"标识，那么12月用"11"
		else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11)
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		// 例子:cal1是"2004-12-31"，cal2是"2005-1-1"
		else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11) 
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}
	
	
	/**
	 * @param time1
	 * @param time2
	 * @return 返回两个时间点，是否相同的月份
	 */
	public static boolean isSameMonth(long time1, long time2) 
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);
		
		if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) 
		{
			if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
				return true;
		}
		
		return false;
	}
	
	
	/**
	 * @return 返回年月日时分秒周数组
	 */
	public static int []  getCalenderArray()
	{
		return getCalenderArray(getCurrentTimeLong());
	}
	
	/**
	 * @return 返回年月日时分秒周数组
	 */
	public static int []  getCalenderArray(long time)
	{
		int [] dateArr = new int[7];
		
		Calendar cale = Calendar.getInstance();
		cale.setTimeInMillis(time);
		cale.setFirstDayOfWeek(Calendar.MONDAY);
		
		dateArr[0] = cale.get(Calendar.YEAR);
		dateArr[1] = cale.get(Calendar.MONTH) + 1;
		dateArr[2] = cale.get(Calendar.DAY_OF_MONTH);
		dateArr[3] = cale.get(Calendar.HOUR_OF_DAY);
		dateArr[4] = cale.get(Calendar.MINUTE);
		dateArr[5] = cale.get(Calendar.SECOND);
		dateArr[6] = getWeekDay(cale);
		
		return dateArr;
	}
	
	
	/**
	 * @return 返回年月日时分秒周数组
	 */
	public static int [] getDateTimeArray(long time)
	{
		return getCalenderArray(time);
	}
	
	/**
	 * zzb 
	 * @param time1
	 * @param time2
	 * @return  根据两个时间点，获取之间隔了几周
	 */
	public static int getWeekCount(long time1, long time2) 
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);
		return Math.abs((int)(cal1.get(Calendar.WEEK_OF_YEAR) - cal2.get(Calendar.WEEK_OF_YEAR)));
	}
	
	
	/**
	 * @param nowTime
	 * @return 返回指定日期的自然的天数
	 */
	public static final double getNatureDay(long nowTime)
	{
		return (double)nowTime / 1000 / 60 /60 / 24 ;
	}
	
	/**
	 * @return 返回获取的自然的天数
	 */
	public static final int getNatureDay()
	{
		return (int)getNatureDay(getCurrentTimeLong());
	}
	
	/**
	 * 
	 * zzb  获取该日期本地的0点时间戳
	 */
	public static final long getDateZeroTimeLong(long time)
	{
		Calendar createCale = Calendar.getInstance();
		createCale.setTimeInMillis(time);
		createCale.set(createCale.get(Calendar.YEAR), createCale.get(Calendar.MONTH), createCale.get(Calendar.DATE),0,0,0);
		return createCale.getTimeInMillis();			//获取0点的时间戳
	}
	
	/**
	 * @param time1
	 * @param time2
	 * @return 返回两个日期时间的天数 
	 */
	public static final int getDateTimeDayCount(long time1 , long time2)
	{
		return (int)Math.abs(Math.ceil(getNatureDay(time1)) - getNatureDay(time2));
	}
	
	
	/**
	 * @param date
	 * @return 返回字符串日期的时间对象 格式 ： yyyy-MM-dd HH:mm:ss
	 */
	public static Date getStringToDate(String date)
	{
		return Date.from(LocalDateTime.parse(date , DTF_TIME).atZone(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * @param date
	 * @return 返回字符串日期的时间对象 格式 ： yyyy-MM-dd 
	 */
	public static Date getDateForDateString(String date)
	{
		return Date.from(LocalDateTime.parse(date , DTF).atZone(ZoneId.systemDefault()).toInstant());
	}
	
	private static Calendar getCalendarByDate(java.util.Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	private static Date calendar2date(Calendar cal)
	{
		return new Date(cal.getTimeInMillis());
	}
	
	public static String getBeforeDay(java.util.Date date, int num)
	{
		Calendar beforeday = getCalendarByDate(date);
		beforeday.add(Calendar.DATE, num);
		
		return getCurrentDateString(beforeday.getTimeInMillis());
	}
	
	public static  int[] getTimeArray()
	{
		Calendar cal = Calendar.getInstance();
		 // 当前年  
	    int year = cal.get(Calendar.YEAR);  
	    // 当前月  
	    int month = (cal.get(Calendar.MONTH)) + 1;  
	    // 当前月的第几天：即当前日  
	    int day_of_month = cal.get(Calendar.DAY_OF_MONTH);  
	    // 当前时：HOUR_OF_DAY-24小时制；HOUR-12小时制  
	    int hour = cal.get(Calendar.HOUR_OF_DAY);  
	    // 当前分  
	    int minute = cal.get(Calendar.MINUTE);  
	    // 当前秒  
	    int second = cal.get(Calendar.SECOND);  
	    // 0-上午；1-下午  
	    int ampm = cal.get(Calendar.AM_PM);  
	    // 当前年的第几周  
	    int week_of_year = cal.get(Calendar.WEEK_OF_YEAR);  
	    // 当前月的第几周  
	    int week_of_month = cal.get(Calendar.WEEK_OF_MONTH);  
	    // 当前年的第几天  
	    int day_of_year = cal.get(Calendar.DAY_OF_YEAR);  
	    
	    return new int [] {year , month , day_of_month , hour , minute , second };
	}
	
	
	public static void main(String[] args) 
	{
		long s = DateTools.getCurrentTimeLong("2018-04-03 10:15:35");
		String time = DateTools.getCurrentTimeString(s);
		System.out.println("s = " + s + " time = " + time);
		
		s = DateTools.getCurrentDateLong("2018-04-03");
		System.out.println("s1 = " + s);
		
		String m = DateTools.getCurrentTimeString(1522684800000L);
		System.out.println("m = " + m);
		
		System.out.println("mone " + getMonthDay());
		
		System.out.println(DateTools.getCurrentDateString());
		System.out.println(DateTools.getCurrentTimeString());
		System.out.println(DateTools.getCurrentTimeMSString());
		
//		LocalDateTime.ofInstant(Instant.ofEpochMilli(getCurrentTimeLong()) , ZoneId.systemDefault());
		
//		DTF.format(LocalDateTime.now(new systemclock)
				
//		for (int j = 0; j < 20; j++) {
//			long startTime = System.currentTimeMillis();
//			
//			for (int i = 0; i < 1000; i++) {
//				String time = DTF.format(LocalDateTime.now());
////				String time = DateTools.getCurrentTimeString();
//			}
//			long endTime = System.currentTimeMillis();
//			
//			System.out.println("time = " + (endTime - startTime));
//		}
		
		
		
//		System.out.println(Arrays.toString(getTimeArray()));
//		
//		Runnable r = ()->{
//			while(true)
//			{
//				System.out.println(Thread.currentThread().getId() + " " + DateTools.getCurrentTimeString());
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		
//		Thread t1 = new Thread(r);
//		Thread t2 = new Thread(r);
//		Thread t3 = new Thread(r);
//		
//		t1.start();
//		t2.start();
//		t3.start();
//		System.out.println(getBeforeDay("2016-06-23 00:00:01"));
//		System.out.println(getBeforeDay(new Date(),-1));
		
//		System.out.println(getBeforeDay(DateFormatStringToDate("2016-06-23"),-1));
		
		
		
//		long createTime = 1461554006656L; 
//		long nowTime = getStringToLong("2016-06-23 00:00:01");
//		System.out.println(nowTime);
//		System.out.println(getCurrentTimeStr(createTime));
//		System.out.println(System.currentTimeMillis());
//		System.out.println((System.currentTimeMillis()-nowTime));
//		
//		System.out.println("createTime = " + getNatureDay(createTime) + " nowTime = " + getNatureDay(nowTime));
//		System.out.println(getDateTimeDayCount(nowTime , createTime));
	}
	
	/**
	 * @return 获取当日24点的时间戳
	 */
	public static long get24HourLong() 
	{
		Calendar calendar2 = Calendar.getInstance();  
        calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),23, 59, 59);
        return calendar2.getTimeInMillis();
	}
	
	public static LocalDateTime date2LocalDateTime(Date date)
	{
		LocalDateTime time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		return time;
	}
	
}
