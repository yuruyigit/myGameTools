package game.tools.platform;

import java.util.HashMap;
public class PlatformID
{
	
	private static final HashMap<String, Object> PLATFORM_MAP = new HashMap<>();
	
	/**
	 * 注册平台
	 * @param platformId 平台类型
	 * @param platformObj
	 */
	protected static void registerPlatform(String platformId , Object platformObj)
	{
		PLATFORM_MAP.put(platformId, platformObj);
	}
	
	/**
	 * 注册平台
	 * @param className 传入的实现的平台java类的完整路径，如,game.platform.Test。
	 * 该参数为数组，平台类型从1开始，则对应的平台类型，就是传入数组的下标前后顺序 。
	 * @throws Exception
	 */
	protected static void registerPlatform(String ... className) throws Exception
	{
		synchronized (PLATFORM_MAP) 
		{
			for (int i = 0; i < className.length; i++) 
			{
				Class clzss = Class.forName(className[i]);
				Platforms platform = (Platforms)clzss.newInstance();
				platform.setPlatformId(String.valueOf(i));
				
				PLATFORM_MAP.put(String.valueOf(i), platform);
				
			}
		}
	}
	
	/**
	 * 注册平台
	 * @param clzssArray 传入的实现的平台java类。
	 * 该参数为数组，平台类型从1开始，则对应的平台类型，就是传入数组的下标前后顺序 。
	 * @throws Exception
	 */
	protected static void registerPlatform(Class ...clzssArray) throws Exception
	{
		synchronized (PLATFORM_MAP) 
		{
			for (int i = 1; i <= clzssArray.length; i++) 
			{
				Class clzss = clzssArray[i-1];
				Platforms platform = (Platforms)clzss.newInstance();
				platform.setPlatformId(String.valueOf(i));
				
				PLATFORM_MAP.put(String.valueOf(i), platform);
				
			}
		}
	}
	
	
	/**
	 * @param loginType
	 * @return 返回对应的平台处理对象
	 */
	public static <T> T getPlatform(String loginType)
	{
		return (T)PLATFORM_MAP.get(loginType);
	}
	
	
	
}
