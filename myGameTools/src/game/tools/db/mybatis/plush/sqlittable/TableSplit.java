package game.tools.db.mybatis.plush.sqlittable;

import java.sql.Connection;
import java.sql.PreparedStatement;

class TableSplit 
{
	/** 2016年4月22日上午12:34:01  */
	private String tableName ;
	
	/** 2016年4月22日下午6:39:28 原始表名 */
	private final String originTableName;
	
	private Tables tables;
	
	public TableSplit(Connection connecion , String tableName) 
	{
		this.originTableName = tableName;
		this.tableName = tableName;
		
		this.tables = new Tables(connecion);
	}
	
	public void tableConnection(Connection connecion )
	{
		this.tables.setConnection(connecion);
	}
	
	public boolean select(String sql) throws Exception
	{
		
		return false;
	}
	
	
	public String getSelectSql(String sql) throws Exception
	{
		sql = Sqls.getSelectSql(this.tables , this.originTableName, sql);
		
		return sql;
	}
	
	
	public String getSelectSql(String sql , Object [] paramsArray) throws Exception
	{
		sql = Sqls.getSelectSql(this.tables , this.originTableName, sql , paramsArray);
		
		return sql;
	}
	
	
	
	public String insert(String originTableName , String sql) throws Exception
	{
		boolean  result = false;
		
		this.tableName = tables.addTableRowCount(originTableName, tableName);
		
		if(!originTableName.equals(tableName))
			sql = sql.replace(originTableName, tableName);
		
//		System.out.println("sql = " + sql);
//		PreparedStatement stmt = tables.getConnection().prepareStatement(sql);
//		result = stmt.execute();
		
		return sql;
	}
	
	
	public boolean insert(String sql) throws Exception
	{
		this.tableName = tables.addTableRowCount(originTableName, tableName);
		
		if(!originTableName.equals(tableName))
			sql = sql.replace(originTableName, tableName);
		
//		System.out.println("sql = " + sql);
//		PreparedStatement stmt = tables.getConnection().prepareStatement(sql);
//		return stmt.execute();
		
		return false;
	}

	public String getInsertSql(String sql) throws Exception
	{
		this.tableName = tables.addTableRowCount(originTableName, tableName);
		
		if(!originTableName.equals(tableName))
			sql = sql.replace(originTableName, tableName);
		
		return sql;
	}
}
