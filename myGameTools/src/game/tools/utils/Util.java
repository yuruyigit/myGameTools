package game.tools.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class Util 
{
	
	/** zzb 2014-4-22 上午11:05:47 随机对象 */
	private static final Random RANDOM = new Random();
	
	/**
	 * @param map
	 * @return 返回map key-value 为字符串类型 
	 */
	public static <T> Map<T , T> toMapKeyValueString(Map<T , T> map)
	{
		Iterator<T> iterator = map.keySet().iterator();
        
        while (iterator.hasNext()) 
        {
        	String key = String.valueOf(iterator.next());
        	String value = String.valueOf(map.get(key));
        	map.put((T)key, (T)value);
        }
		return map;
	}
	
	
	
	/**
	 * @param clzss
	 * @param map
	 * @return 返回一个解析map键值对的java对象
	 */
	public static <T> T parseMaptoJavaObject(Class<T> clzss , Map<String , String> map)
	{
		try 
		{
			Iterator<String> iterator = map.keySet().iterator();
	        
			if(map.size() <= 0)
				return null;
			
			T t = clzss.newInstance();
			Field [] fields = clzss.getDeclaredFields();
	        while (iterator.hasNext()) 
	        {
	        	String name = iterator.next();
	        	String value = map.get(name);
	        	for (Field field : fields) 
	        	{
	        		field.setAccessible(true);
	        		if(field.getName().equals(name))
	        		{
	        			if(value.equals("null"))
	        				continue;
	        			
	        			if (field.getType().toString().equals("int"))
							field.set(t, Integer.parseInt(value));
						else if (field.getType().toString().equals("double"))
							field.set(t, Double.parseDouble(value));
						else if (field.getType().toString().equals("long"))
							field.set(t, Long.parseLong(value));
						else if (field.getType().toString().equals("float"))
							field.set(t, Float.parseFloat(value));
						else if (field.getType().toString().equals("class java.lang.String"))
							field.set(t, value);
						else if (field.getType().toString().equals("boolean"))
							field.set(t, Boolean.parseBoolean(value));
	        			break;
	        		}
				}
	        }
	        
			return t;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static String getErrorString(Exception e)
	{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("\n");
		strBuf.append("\t").append(e.toString()).append("\n");
		StackTraceElement[] ste = e.getStackTrace();
		for (int i = 0; i < ste.length; i++)
			strBuf.append("\t\t").append(ste[i].toString()).append("\n");
		
		return strBuf.toString();
	}
	
	public static String getStrackStr()
	{
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for (int i = 0; i < ste.length; i++)
			sb.append("\t").append(ste[i].toString()).append("\n");
		
		return sb.toString();
	}
	
	public static int getRandomInt(int min , int max ) 
	{		
		max = max - min + 1;		
		return RANDOM.nextInt(max) + min;	
	}
	
	
	/**
	 * @param min
	 * @param max
	 * @param n
	 * @return 返回一个不重复的随机数组
	 */
	public static int[] getRandomArray(int min,int max,int n)
	{
		int [] index = new int[n];
		
		if(n > max)
		{
			for (int i = 0; i <= max; i++)
				index[i] = i;
			return index;
		}
		
		boolean repeat = false;
		for (int i = 0; i < index.length; ) 
		{
			repeat = false;
			int p = getRandomInt(min,  max);
			for (int j = 0; j < index.length; j++ ) 
			{
				if(index[j] == p)
				{
					p = getRandomInt(min , max);
					j = 0;
					repeat = true;
					continue;
				}
			}
			
			if(!repeat)
				index[i++] = p;
		}
		
		return index;
	}
	
	public static boolean stringIsEmpty(String params)
	{
		return StringTools.empty(params);
	}
	
	
	public static void println(String string)
	{
		System.out.println(string);
	}
	
	
	/**
	 * 是否是数字
	 * zzb 2015年4月15日上午10:15:06 
	 * @param str
	 * @return 
	 */
	public static boolean isNumeric(String str)
	{ 
		if(StringTools.empty(str))
			return false;
		
		for (int i = 0; i < str.length(); i++) 
		{
			if(!Character.isDigit(str.charAt(i)))
				return false;
		}
		return true;
	 } 
	
	public static String pringStrack()
	{
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for (int i = 0; i < ste.length; i++)
			sb.append("\t").append(ste[i].toString()).append("\n");
		
		System.out.println(sb.toString());
		
		return sb.toString();
		
		
//		throw new NullPointerException("Print Strack");
	}
	
	public static String numberFormat(int num)
	{
		return numberFormat(num , null);
	}
	
	public static String numberFormat(int num , String format)
	{
		if(StringTools.empty(format))
			format = "0000.00";
		DecimalFormat decimalFormat = new DecimalFormat(format);  
		
		String s = decimalFormat.format(num);
		
		return s;
	}
	
	private static String getCrc(byte[] data) 
	{  
        int high;  
        int flag;  
  
        // 16位寄存器，所有数位均为1  
        int wcrc = 0xffff;  
        for (int i = 0; i < data.length; i++) {  
            // 16 位寄存器的高位字节  
            high = wcrc >> 8;  
            // 取被校验串的一个字节与 16 位寄存器的高位字节进行“异或”运算  
            wcrc = high ^ data[i];  
  
            for (int j = 0; j < 8; j++) {  
                flag = wcrc & 0x0001;  
                // 把这个 16 寄存器向右移一位  
                wcrc = wcrc >> 1;  
                // 若向右(标记位)移出的数位是 1,则生成多项式 1010 0000 0000 0001 和这个寄存器进行“异或”运算  
                if (flag == 1)  
                    wcrc ^= 0xa001;  
            }  
        }  
  
        return Integer.toHexString(wcrc);  
	}
	
	/**
	 * 检查生成文件目录路径
	 * @param path  传入可能生成的路径
	 * @return
	 */
	public static boolean catalog(String path)
	{
		return generateFileCatalog(path);
	}
	
	public static boolean generateFileCatalog(String path)
	{
		if(StringTools.empty(path))
			return false;
		
		String [] pathArray = null;
		if(path.indexOf("/") >= 0)
			pathArray = path.split("/");
		else
			pathArray = path.split("\\");

		String genPath = "";
		
		for (String p : pathArray) 
		{
			genPath += p + "/";
			
			File f = new File(genPath);
			if(!f.exists())
				f.mkdirs();
		}
		return false;
	}
	
	public static String getLocalPath() 
	{
	    String path = System.getProperty("user.dir");
	    return path.replace("\\\\", "\\");
	}
	 
	public static void main(String[] args) 
	{
		String crc = getCrc("2".getBytes());
		System.out.println("crc = " + crc);
	}
	
}
