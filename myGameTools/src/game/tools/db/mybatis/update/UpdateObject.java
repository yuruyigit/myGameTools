package game.tools.db.mybatis.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;

import game.tools.db.UpdateTable;

class UpdateObject
{
	/** 2018年5月8日 下午10:39:30 要更新的对象列表*/
	private List<Object> updateObjectList;
	
	private List<ResultMapping> resultMapperList;
	
	private String tableName;
	
	private String className;
	
	private Class clzss;
	
	private UpdateTable updateTable;

	UpdateObject(Object updateObject , SqlSession sqlSession)
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
		
		updateTable = new UpdateTable(tableName);
		
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
	

	List<ResultMapping> getResultMapperList() {		return resultMapperList;	}
	String getTableName() {		return tableName;	}
	String getClassName() {		return className;	}
	List<Object> getUpdateObjectList() {		return updateObjectList;	}
	Class getClzss() {		return clzss;	}
	UpdateTable getUpdateTable() {		return updateTable;	}
}