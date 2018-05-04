package game.tools.db.mybatis.update;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import game.tools.db.UpdateTable;
import game.tools.log.LogUtil;
import game.tools.utils.DateTools;

class UpdateObject
{
	private List<Object> updateObjectList;
	
	private List<ResultMapping> resultMapperList;
	
	private String tableName;
	
	private String className;
	
	private Class clzss;

	public UpdateObject(Object updateObject , SqlSession sqlSession)
	{
		init(updateObject , sqlSession);
	}
	
	private void init(Object updateObject, SqlSession sqlSession)
	{
		this.updateObjectList = convertToList(updateObject);
		
		initResultMaping(updateObjectList , sqlSession);
		
		initTableName(this.className , sqlSession);
		
	}
	
	private void initResultMaping(List<Object> list, SqlSession sqlSession) 
	{
		Object o = list.get(0);
		
		this.clzss = o.getClass();
		
		this.className = getClassName(o);
		
		if(className == null)
			return ;
					
		String mapperName = className.replaceAll("entity", "mapper") + "Mapper.BaseResultMap";
		
		ResultMap resultMap  = sqlSession.getConfiguration().getResultMap(mapperName);
		
		this.resultMapperList = resultMap.getResultMappings();
	}

	private String getClassName(Object o)
	{
		return o.getClass().getName();
	}
	
	private String initTableName(String clzssName, SqlSession sqlSession)
	{
		String statementId = clzssName.replaceAll("entity", "mapper") + "Mapper.deleteByPrimaryKey";
		
		String sqlString = sqlSession.getConfiguration().getMappedStatement(statementId).getBoundSql("").getSql();
		
		String [] sqlArray = sqlString.split("from");
		
		String tableName = sqlArray[1];
		
		if(tableName.indexOf("\n") >= 0)
			tableName = tableName.split("\n")[0].trim();
		else if(tableName.indexOf(" ") >= 0)
			tableName = tableName.split(" ")[0].trim();
		
		this.tableName = tableName;
		
		return tableName;
	}
	
	/**
	 * @return 返回该对象转成list
	 */
	private List<Object> convertToList(Object o )
	{
		List<Object> list = null;
		
		if(o instanceof List)
		{
			list = (List<Object>)o;
		}
		else if(o instanceof Map)
		{
			Map<Object , Object> map = (Map<Object , Object>)o;
			list = new ArrayList<Object>(map.values());
		}
		else
		{
			list = new ArrayList<Object>(1);
			list.add(o);
		}
		return list;
	}
	

	public List<ResultMapping> getResultMapperList() {		return resultMapperList;	}
	public String getTableName() {		return tableName;	}
	public String getClassName() {		return className;	}
	public List<Object> getUpdateObjectList() {		return updateObjectList;	}
	public Class getClzss() {		return clzss;	}
}

class Updates 
{
	private long id;
	
	private SqlSession sqlSession;
	
	private long lastUpdateTime;
	
	/** 2018年5月4日 上午9:29:03 要更新的表对象列表<类全名,更新对象>*/
	private HashMap<String , UpdateObject > updateObjectMap = new HashMap<>(16);

	Updates(long id, SqlSession sqlSession) 
	{
		this.id = id;
		this.sqlSession = sqlSession;
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

	void update(long nowTime) 
	{
		long gapTime = nowTime - this.lastUpdateTime;
		
		if(gapTime < MybatisFactoryUpdate.getUPDATE_GAP_TIME())
			return ;
		
		if(updateObjectMap.isEmpty())
			return ;
		
		update();
	}
	 
	void update()
	{
		long startTime = System.currentTimeMillis();
		
		List<String> sqlStringList = getSqlString();
		
		if(sqlStringList.isEmpty())
			return;
		
		try
		{
			SqlSessionTemplate sqlSession = (SqlSessionTemplate)this.sqlSession;
			Connection conn = sqlSession.getSqlSessionFactory().openSession().getConnection();
			
			Statement statment = conn.createStatement();
			for (String sqlString: sqlStringList) 
				statment.addBatch(sqlString);
			
			statment.executeBatch();
			statment.clearBatch();
			statment.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
		finally
		{
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
				UpdateTable table = new UpdateTable(upObject.getTableName());
				
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
	
	public long getLastUpdateTime() {  return lastUpdateTime; }
	public Long getId() {		return this.id;	}
}
