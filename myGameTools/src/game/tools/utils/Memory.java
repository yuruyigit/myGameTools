package game.tools.utils;

public class Memory 
{
	private static long rate = 1000_000;			//MB
	
	public static void printMemory()
	{
//		System.out.println("maxMemory : " + Runtime.getRuntime().maxMemory());
		System.out.println("totalMemory : " + Runtime.getRuntime().totalMemory());
		long freeMemory = Runtime.getRuntime().freeMemory(); 
//		if(freeMemory < rate)
//		{
//			System.out.println("freeMemory : " + (Runtime.getRuntime().freeMemory() / 1000) + "KB");	
//		}
		System.out.println("freeMemory : " + (Runtime.getRuntime().freeMemory() / 1000/ 1000) + "MB");
		System.out.println("useMemory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000) + "MB");
	}
}
