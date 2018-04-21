package game.tools.db.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.ibatis.session.SqlSession;

public class MybatisUpdateQueue 
{
	private static final HashMap<Object, HashMap<Class<?>, LinkedHashMap<Long , Object>>> UPDATE_QUEUQ_MAP = new HashMap<Object, HashMap<Class<?>, LinkedHashMap<Long , Object>>>(10);
	
	
	public static void updateQueue(Object updateObject)
	{
		updateQueue(MybatisFactoryTools.getFristSessionKey(), updateObject);
	}
	
	public static void updateQueue(Object sessionNo, Object updateObject)
	{
		HashMap<Class<?>, LinkedHashMap<Long , Object>> sessionMap = UPDATE_QUEUQ_MAP.get(sessionNo);
		
		LinkedHashMap<Long , Object> updaeQueneMap = sessionMap.get(updateObject.getClass());
		
		long id = getId(updateObject);
		
		updaeQueneMap.put(id , updateObject);
	}
	
	private static long update(Object sessionNo, Object updateObject)
	{
//		Class<?> clzss = updateObject.getClass();
//		
//		Object mapperObject = MybatisFactoryTools.getMapper(sessionNo, clzss);
//		
//		Object idObject = null;
//		try {
//			Method method = mapperObject.getMethod("getId");
//			method.setAccessible(true);
//			
//			idObject = method.invoke(updateObject);
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//		if(idObject != null)
//			return (long)idObject;
		return 0;
	}
	
	
	private static long getId(Object updateObject)
	{
		Class<?> clzss = updateObject.getClass();
		Object idObject = null;
		try {
			Method method = clzss.getMethod("getId");
			method.setAccessible(true);
			
			idObject = method.invoke(updateObject);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		if(idObject != null)
			return (long)idObject;
		return 0;
	}
	
	
	
}
