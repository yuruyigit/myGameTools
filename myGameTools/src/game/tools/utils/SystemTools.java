package game.tools.utils;

public class SystemTools 
{
	public static String machineInfo()
	{
		Runtime runTime = Runtime.getRuntime();
		StringBuilder info = new StringBuilder();
		info.append("Max Memory:").append(runTime.maxMemory()).append(Symbol.WRAP);
		info.append("Free Memory:").append(runTime.freeMemory()).append(Symbol.WRAP);
		info.append("Total Memory:").append(runTime.totalMemory()).append(Symbol.WRAP);
//		info.append("Total Memory:").append(runTime).append(Symbol.WRAP);
		
		
		return info.toString();
	}
}
