package game.tools.db.mybatis.update;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import game.tools.db.UpdateTable;
import game.tools.log.LogUtil;
import game.tools.utils.DateTools;

class Updates 
{
	private long id;
	
	private SqlSession sqlSession;
	
	private long lastUpdateTime;
	
	/** 2018年5月4日 上午9:29:03 要更新的表对象列表<类全名,更新对象>*/
	private HashMap<String , UpdateObject> updateObjectMap = new HashMap<>(16);

	Updates(long id, SqlSession sqlSession) 
	{
		this.id = id;
		this.sqlSession = sqlSession;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	void add(Object o)
	{
		String className = getClassName(o);
		
		UpdateObject upObject = updateObjectMap.get(className);
		
		if(upObject == null)
		{
			synchronized (updateObjectMap)
			{
				if(upObject == null)
				{
					upObject = new UpdateObject(o, sqlSession);
					this.updateObjectMap.put(className, upObject);
				}
			}
		}
		
		System.out.println("registerUpdate add  by id : " + id + " class : " + className);
	}
	
	private String getClassName(Object o)
	{
		if(o instanceof List)
		{
			List<Object> list = (List<Object>)o;
			return list.get(0).getClass().getName();
		}
		else if(o instanceof Map)
		{
			Map<Object , Object> map = (Map<Object , Object>)o;
			return map.values().iterator().next().getClass().getName();
		}
		else
		{
			return o.getClass().getName();
		}
	}
	 
	void update()
	{
		if(updateObjectMap.isEmpty())
			return ;
		
		long startTime = System.currentTimeMillis();
		
		List<String> sqlStringList = getSqlString();
		
		if(sqlStringList.isEmpty())
			return;
		
		SqlSessionTemplate sqlSession = null;
		Connection conn = null;
		Statement statment = null;
		
		try
		{
			sqlSession = (SqlSessionTemplate)this.sqlSession;
			conn = sqlSession.getSqlSessionFactory().openSession().getConnection();
			
			statment = conn.createStatement();
			for (String sqlString: sqlStringList) 
				statment.addBatch(sqlString);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
		finally
		{
			try 
			{
				statment.executeBatch();
				statment.clearBatch();
				statment.close();
				
				conn.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				LogUtil.error(e);
			}
			
			this.lastUpdateTime = System.currentTimeMillis();
			
			long endTime = System.currentTimeMillis();
			
			System.out.println("Update by " + this.id + " " + DateTools.getCurrentTimeString(lastUpdateTime) + " " + (endTime - startTime));
		}
	}
	
	private List<String> getSqlString()
	{
		String column , prop ;
		
		Object value ;
		
		long id = 0 ;
		
		Field field ;
		
		List<String> sqlStringList = new ArrayList<>(updateObjectMap.size());
		
		try 
		{
			for (UpdateObject upObject: updateObjectMap.values()) 			//表
			{
				UpdateTable table = upObject.getUpdateTable();
				
				table.clear();
				
				for (Object object : upObject.getUpdateObjectList()) 
				{
					for (ResultMapping mapper : upObject.getResultMapperList()) 
					{
						column = "`" + mapper.getColumn() + "`";
						prop = mapper.getProperty();
						
						field = upObject.getClzss().getDeclaredField(prop);
						field.setAccessible(true);
						
						value = field.get(object);
						
						if(prop.equalsIgnoreCase("id"))
							id = Long.valueOf(value.toString());
						else
							table.add(column, id , value);
					}
				}
				
				sqlStringList.add(table.toSqlString());
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return sqlStringList;
	}
	
	
	void distory()
	{
		this.sqlSession = null;
		
		this.updateObjectMap.clear();
		this.updateObjectMap = null;
	}
	
	long getLastUpdateTime() {  return lastUpdateTime; }
	Long getId() {		return this.id;	}
}
