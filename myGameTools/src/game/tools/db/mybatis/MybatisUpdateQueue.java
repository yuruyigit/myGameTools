package game.tools.db.mybatis;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;
import game.data.conf.entity.PrfWarZoneTitle;
import game.tools.db.UpdateTable;

public class MybatisUpdateQueue 
{
	private static final HashMap<Object, HashMap<Class<?>, LinkedHashMap<Long , Object>>> UPDATE_QUEUQ_MAP = new HashMap<Object, HashMap<Class<?>, LinkedHashMap<Long , Object>>>(10);
	
	private static final ArrayList<String> SQL_STRING_LIST = new ArrayList<String>();
	
	public static void updateQueue(Object updateObject)
	{
		updateQueue(MybatisFactoryTools.getFristSessionKey(), updateObject);
	}
	
	public static void updateQueue(Object sessionNo, Object updateObject)
	{
		HashMap<Class<?>, LinkedHashMap<Long , Object>> sessionMap = UPDATE_QUEUQ_MAP.get(sessionNo);
		
		if(sessionMap == null)
		{
			synchronized(UPDATE_QUEUQ_MAP)
			{
				if(sessionMap == null)
				{
					sessionMap = new HashMap<Class<?>, LinkedHashMap<Long , Object>>();
					UPDATE_QUEUQ_MAP.put(sessionNo , sessionMap);
				}
			}
		}
		
		Class clzss = updateObject.getClass();
		
		LinkedHashMap<Long , Object> updaeQueneMap = sessionMap.get(clzss);
		
		long id = getId(updateObject);
		
		if(updaeQueneMap == null)
		{
			synchronized(UPDATE_QUEUQ_MAP)
			{
				if(updaeQueneMap == null)
				{
					updaeQueneMap = new LinkedHashMap<Long , Object>();
					sessionMap.put(clzss, updaeQueneMap);
				}
			}
		}
		
		updaeQueneMap.put(id , updateObject);
		
		try {
			updateByPrimaryKey(sessionNo , clzss , updateObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static long updateByPrimaryKey(Object sessionNo , Class clzss , List updateObjectList) throws Exception
	{
		SqlSession session = MybatisFactoryTools.getSqlSession(sessionNo);
		
		String mapperName = clzss.getName().replaceAll("entity", "mapper") + "Mapper.BaseResultMap";
		
		ResultMap resultMap = session.getConfiguration().getResultMap(mapperName);
		
		List<ResultMapping> resultMapperList = resultMap.getResultMappings();
		
		String column , prop ;
		
		Object value ;
		
		long id = 0 ;
		
		Field field ;
				
		String tableName = getTableName(session);
		
		UpdateTable table = new UpdateTable(tableName);
		
		for (Object updateObject : updateObjectList) 
		{
			for(ResultMapping mapper : resultMapperList)
			{
				column = mapper.getColumn();
				prop = mapper.getProperty();
				
				field = updateObject.getClass().getDeclaredField(prop);
				field.setAccessible(true);
				
				value = field.get(updateObject);
				
				if(prop.equalsIgnoreCase("id"))
					id = Long.valueOf(value.toString());
				else
					table.add(column, id , value);
			}
		}
		
		String sqlString = table.toSqlString();
		
		SQL_STRING_LIST.add(sqlString);
		
		return 0;
	}
	
	private static long updateByPrimaryKey(Object sessionNo , Class clzss , Object updateObject) throws Exception
	{
		SqlSession session = MybatisFactoryTools.getSqlSession(sessionNo);
		
		String mapperName = clzss.getName().replaceAll("entity", "mapper") + "Mapper.BaseResultMap";
		
		ResultMap resultMap = session.getConfiguration().getResultMap(mapperName);
		
		List<ResultMapping> resultMapperList = resultMap.getResultMappings();
		
		String column , prop ;
		
		Object value ;
		
		long id = 0 ;
		
		Field field ;
				
		String tableName = getTableName(session);
		
		UpdateTable table = new UpdateTable(tableName);
		
		for(ResultMapping mapper : resultMapperList)
		{
			column = mapper.getColumn();
			prop = mapper.getProperty();
			
			field = updateObject.getClass().getDeclaredField(prop);
			field.setAccessible(true);
			
			value = field.get(updateObject);
			
			if(prop.equalsIgnoreCase("id"))
				id = Long.valueOf(value.toString());
			
			table.add(column, id , value);
		}
		
		String sqlString = table.toSqlString();
		
//		resultMap.getPropertyResultMappings()
//		session.getConfiguration().getResultMap("game.data.conf.mapper.PrfWarZoneTitleMapper.BaseResultMap").getPropertyResultMappings().get(0).getc
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
	
	private static String getTableName(SqlSession session)
	{
		String sqlString = session.getConfiguration().getMappedStatement("insert").getBoundSql("").getSql();
		
		String [] sqlArray = sqlString.split(" ");
		
		return sqlArray[2];
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
			return Long.valueOf(idObject.toString());
		return 0;
	}
	
	public static void main(String [] args)
	{
		MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://192.168.56.31:3306/game_dev" ,"root", "rootroot");
		
		PrfWarZoneTitle pwzt = new PrfWarZoneTitle();
		pwzt.setId(1);
		pwzt.setReward("reward");
		pwzt.setFrameSource("framesource");
		pwzt.setMedalSource("medalSource");
		
		MybatisUpdateQueue.updateQueue(pwzt);
		
//		MybatisFactoryTools.getSqlSession("").updateByPrimaryKey(arg0)
	}
	
}
