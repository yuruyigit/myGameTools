package game.tools.threadpool;

import java.util.concurrent.ConcurrentHashMap;

import game.tools.db.cache.expire.ExpireCacheDataMap;

public class ThreadLocal 
{
	/** 2016年6月24日上午10:24:06 某个 线程中的参数map<线程ID， <对应线程的参数名，参数对象>>,*/
	private static final ExpireCacheDataMap<Long, ConcurrentHashMap<String , Object>> THREAD_PARAMS_MAP = new ExpireCacheDataMap<Long , ConcurrentHashMap<String , Object>>();
	
	public static void setLocal(String key ,Object obj)	
	{
		if(key == null )
			return ;
		
		long tId = Thread.currentThread().getId();
		
		ConcurrentHashMap<String, Object> paramMap = THREAD_PARAMS_MAP.get(tId);
		
		if(paramMap == null)
		{
			paramMap =  new ConcurrentHashMap<String , Object>();
			THREAD_PARAMS_MAP.put(tId, paramMap);
		}
		
		paramMap.put(key, obj);
	}
	
	public static <T> T getLocal(String key)	
	{
		long tId = Thread.currentThread().getId();
		
		ConcurrentHashMap<String, Object> paramMap = THREAD_PARAMS_MAP.get(tId);
		
		if(paramMap != null)
		{
			T t = (T)paramMap.get(key);
			
			if(paramMap.size() == 0)
				THREAD_PARAMS_MAP.remove(tId);
			
			return t;
		}
		return null;
	}
	
	
	public static <T> T remove(String key)	
	{
		long tId = Thread.currentThread().getId();
		
		ConcurrentHashMap<String, Object> paramMap = THREAD_PARAMS_MAP.get(tId);
		
		if(paramMap != null)
		{
			T t = (T)paramMap.remove(key);
			
			if(paramMap.size() == 0)
				THREAD_PARAMS_MAP.remove(tId);
			
			return t;
		}
		return null;
	}
	
	
}
