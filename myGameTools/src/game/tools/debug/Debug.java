package game.tools.debug;
import game.tools.log.LogUtil;
import game.tools.utils.DateTools;

public class Debug
{
	
	private static String getStrackStr()
	{
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for (int i = 0; i < ste.length; i++)
			sb.append("\t").append(ste[i].toString()).append("\n");
		
		return sb.toString();
	}
	
	public static void printStrack(String methodInfo)
	{
		String content = debugStrack(methodInfo);
		System.out.println(content);
	}
	
	public static String debugStrack(String methodInfo)
	{
		String strack = getStrackStr();
//		String threadName = Thread.currentThread().getName();
//		long threadId = Thread.currentThread().getId();
		
//		String content = " methodInfo "+methodInfo+"\n threadId " +threadId + " threadName " +threadName + "\n strack :" + strack;
		String content =  " methodInfo "+methodInfo+ " \n" + strack;
		
		LogUtil.debug(content);
		
		return content;
		
	}
}
