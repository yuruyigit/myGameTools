package game.tools.println;

public class Println 
{
	
	/** 2016年10月27日上午10:31:50 开启打印 */
	private static boolean OPEN_PRINT = true;
	
	
	public static void open()
	{
		OPEN_PRINT = true;
	}
	
	public static void close()
	{
		OPEN_PRINT = false;
	}
	
	public static void println(Object obj)
	{
		if(!OPEN_PRINT)
			return ;
		
		System.out.println(obj);
	}
	
	public static void errPrintln(Object obj)
	{
		if(!OPEN_PRINT)
			return ;
		
		System.err.println(obj);
	}
	
	
	
}
