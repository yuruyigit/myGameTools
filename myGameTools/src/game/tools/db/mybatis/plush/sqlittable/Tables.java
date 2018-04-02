package game.tools.db.mybatis.plush.sqlittable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.alibaba.fastjson.JSONObject;

import game.tools.db.mybatis.plush.MybatisTableSegInterceptor;
import game.tools.utils.Util;

class Tables 
{
	private HashMap<String , Integer> TABLE_NAME_MAP ;
	/** 2016年4月30日下午11:03:50  创建生成子表的编号前缀*/
	private static final String GAP_STR = "_";
	
	private Connection connecion; 
	
	public Tables(Connection connecion) 
	{
		this.connecion = connecion;
		
		try {
			initTableCountMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void setConnection(Connection connecion) 
	{
		this.connecion = connecion;
	}
	
	/**
	 * 初始化所有表的数量
	 * @throws Exception
	 */
	private void initTableCountMap() throws Exception
	{
		ResultSet set = executeQuery("show tables");
		
		TABLE_NAME_MAP = new HashMap<>(set.getRow());
		
		while(set.next())
		{
			String tableName = set.getString(1);
			int count = getDataRowCount(tableName) ;
			
			TABLE_NAME_MAP.put(tableName, count);
		}
		
//		System.out.println(JSONObject.toJSONString(TABLE_NAME_MAP));
	}

	/**
	 * @param tableName
	 * @return 返回，获取指定表的行数 
	 * @throws Exception
	 */
	private int getDataRowCount(String tableName) throws Exception
	{
		String sql = "select count(*) from " + tableName;
		
		ResultSet set = executeQuery(sql);
		
		int count = 0;
		while(set.next())
			count = set.getInt(1);
		
		
//		set.close();
		
		return count;
	}
	
	/**
	 * 添加表的数据
	 * @param originTableName 原表名
	 * @param tableName 现表名
	 * @return  执行添加表数据 ，后返回执行新的现表名
	 * @throws Exception
	 */
	String addTableRowCount(String originTableName , String tableName)  throws Exception
	{
		if(Util.stringIsEmpty(originTableName) || Util.stringIsEmpty(tableName))
			return null;
		
		synchronized (tableName) 
		{
			Integer oldCountObj = TABLE_NAME_MAP.get(tableName);
			
			int count = 0 ;
			if(oldCountObj == null)
				TABLE_NAME_MAP.put(tableName,count);	
			else
			{
				count = (oldCountObj==null?0:oldCountObj);
				
				if(isFull(count))							//如果单表满指定行数
				{
					TABLE_NAME_MAP.put(tableName.trim(),count);
					
					tableName = initTableIndex(originTableName);
					
					addTableRowCount(originTableName , tableName);
					
					return tableName ;
				}
				
				count ++;
				TABLE_NAME_MAP.put(tableName,count);
				
			}
		}
		
		System.out.println(TABLE_NAME_MAP.get(tableName));
		
		return tableName ;
		
	}
	
	
	private String initTableIndex(String originTableName) throws Exception
	{
		String tableName = null;
		
		Integer count = TABLE_NAME_MAP.get(originTableName);
		
		if(isFull(count))
		{
			for (int index = 0; true; index++) 
			{
				tableName = getNewTableName(originTableName , index);
				count = TABLE_NAME_MAP.get(tableName);
				
				if(count == null)
				{
					tableName = createTable(originTableName , tableName , index);
					break;
				}
				else if(isFull(count))
					continue;
				else
					break;
			}
		}
		else
			tableName = originTableName;
				
		return tableName;
	}
	
	
	private static String getNewTableName(String originTableName, int index) 
	{
		return originTableName+GAP_STR+index;
	}



	/**
	 * 创建新表
	 * @param originTableName 原表名
	 * @param tableName 现表名
	 * @param index 
	 * @return 返回一个创建子表后的表名
	 * @throws Exception
	 */
	private String createTable(String originTableName , String tableName, int index) throws Exception
	{
		synchronized (originTableName) 
		{
			System.out.println("createTable at " + tableName);
			
			String sql = " create table "+tableName+" like "+originTableName;
			
			executeSql(sql);
			
			joinSeed(originTableName , tableName , index);
			
			addTableRowCount(originTableName , tableName);
			
			return tableName;
		}
		
	}
	
	/**
	 * 接着上一个表的主键ID种子
	 * @param tableName 
	 * @param tableName2 
	 * @param index
	 */
	private void joinSeed(String originTableName, String tableName, int index ) throws Exception
	{
		long sumCount = getSumRowCount(originTableName , index);
		
		String sql = "ALTER TABLE "+tableName+" auto_increment="+(sumCount + 1);
		
		executeSql(sql);
	
	}
	
	private ResultSet executeQuery(String sql)throws Exception
	{
		synchronized (connecion) 
		{
			
			ResultSet set = null;
			try
			{
				PreparedStatement stmt = connecion.prepareStatement(sql);
				
				set = stmt.executeQuery(sql);
			}
			catch (Exception e) 
			{
				throw e;
			}
			finally
			{
//				if(!connecion.isClosed())
//					connecion.close();
			}
			
			return set;
		}
	}
	
	private void executeSql(String sql) throws Exception
	{
		synchronized (connecion) 
		{
			try
			{
				PreparedStatement stmt = connecion.prepareStatement(sql);
				
				stmt.execute(sql);
			}
			catch (Exception e) 
			{
				throw e;
			}
			finally
			{
//				if(!connecion.isClosed())
//					connecion.close();
			}
		}
		
	}
	
	
	/**
	 * @param index
	 * @return 返回当前原表子表的总数量，计算下个表的种子
	 */
	private long getSumRowCount(String originTableName, int index)
	{
		int count = TABLE_NAME_MAP.get(originTableName);
		
		int i = 0 ;
		while( i < index )
		{
			count += TABLE_NAME_MAP.get(originTableName + GAP_STR +i);
			
			i ++;
		}
		return count ;
	}
	
	
	private static  boolean isFull(int count)
	{
		return count >= MybatisTableSegInterceptor.getSPLIT_COUNT();
	}
	
	
	/**
	 * 返回这个原表下面的子表列表
	 * @param originTableName 原表名
	 * @return 返回这个原表下面的子表列表
	 */
	ArrayList<String> getThisTableNameList(String originTableName)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		Iterator<String> iterator = TABLE_NAME_MAP.keySet().iterator();
		while(iterator.hasNext())
		{
			String tableName = iterator.next();
			if(isTable(originTableName.trim() ,tableName.trim()))
				list.add(tableName);
		}
		return list;
	}
	
	private static boolean isTable(String originTableName, String tableName)
	{
		if(originTableName.equals(tableName))
			return true;
		
		for (int i = 0; i < 99; i++) 
		{
			if(tableName.indexOf(originTableName) >= 0)				//如果是该原表的扩容表
			{
				if(tableName.equals(originTableName+Tables.GAP_STR+i))
					return true;
			}
			else
				return false;
		}
		return false;
	}
}
