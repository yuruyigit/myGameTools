package game.tools.db.mybatis.plush.sqlittable;


import java.sql.Connection;
import java.util.HashMap;

public class SqlActionControl 
{
	private HashMap<String, TableSplit> tableSplitMap = new HashMap<>();
	
	
	public String doAction(String cmd , Connection connection , String originalSql, Object... paramsArray)  
	{
		String sql = null;
		
		String tableName = getTableNameBySql(cmd , originalSql);
		
		try
		{
			sql = doAlloc(connection ,tableName , cmd , originalSql , paramsArray);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sql;
	}
	
	

	private String getTableNameBySql(String cmd , String originalSql)
	{
		String tableName = null;
		if(cmd.equals(SqlCmd.SELECT.getCmd()))
		{
			
			String lowerFrom = "from " , upperFrom = "FROM ";
			String [] arr = originalSql.split(lowerFrom);
			if(arr == null)
				arr = originalSql.split(upperFrom);
			
			String [] fromArr = arr[1].trim().split(" ");
			
			tableName = fromArr[0].trim();
		}
		else if(cmd.equals(SqlCmd.INSERT.getCmd()))
		{
			String lowerFrom = "insert into " , upperFrom = "INSERT INTO ";
			String [] arr = originalSql.split(lowerFrom);
			if(arr == null)
				arr = originalSql.split(upperFrom);
			
			String [] fromArr = arr[1].trim().split(" ");
			
			tableName = fromArr[0].trim();
		}
			
		return tableName;
	}
	
	
	private String doAlloc(Connection connection ,String tableName , String cmd , String originalSql, Object[] paramsArray)  throws Exception
	{
		String sql = originalSql;
		
		TableSplit tableSplit = getTableSplit(connection ,tableName);
		
		if(cmd.equals(SqlCmd.SELECT.getCmd()))
			sql = tableSplit.getSelectSql(sql, paramsArray);
		else if(cmd.equals(SqlCmd.INSERT.getCmd()))
			sql = tableSplit.getInsertSql(sql);
		
		return sql;
	}
	
	
	private TableSplit getTableSplit(Connection connection , String tableName)
	{
		TableSplit tableSplit = tableSplitMap.get(tableName);
		
		if(tableSplit == null)
		{
			tableSplit = new TableSplit(connection ,tableName);
			tableSplitMap.put(tableName, tableSplit);
		}
		else
			tableSplit.tableConnection(connection);
		
		return tableSplit;
	}
}
