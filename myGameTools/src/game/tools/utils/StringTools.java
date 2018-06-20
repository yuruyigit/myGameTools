package game.tools.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class StringTools 
{

    /**
     *	  该正则表达式可以匹配所有的数字 包括负数 
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+.?[0-9]+");
    
	/**
	 * @param params
	 * @return 为空
	 */
	public static boolean empty(String params)
	{
		if( null == params || Symbol.EMPTY.equals(params) || params.equalsIgnoreCase("null"))
			return true;
		return false;
	}
	
	
	/**
	 * @param params
	 * @return 不为空
	 */
	public static boolean has(String params)
	{
		if( null != params || !Symbol.EMPTY.equals(params))
			return true;
		return false;
	}
	
	
	/**
	 * @param params
	 * @return 返回这个参数是否存在
	 */
	public static boolean has(Object params)
	{
		if( null != params || !Symbol.EMPTY.equals(params))
			return true;
		return false;
	}
	
	
	public static boolean isNumber(String str) 
	{  
//		str = str.trim();
//		try 
//		{
//			Float.parseFloat(str);
//		}
//		catch (Exception e) 
//		{
//			return false;
//		}
//		return true;
		str = str.trim();
		
		for(int i=str.length();--i>=0;)
		{
	        int chr=str.charAt(i);
	        if(chr<48 || chr>57)
	            return false;
	    }
		return true;
		
//		  String bigStr;
//        try 
//        {
//            bigStr = new BigDecimal(str).toString();
//        }
//        catch (Exception e) 
//        {
//            return false;//异常 说明包含非数字。
//        }
//	      return NUMBER_PATTERN.matcher(bigStr).matches();  
	}
	 
}
