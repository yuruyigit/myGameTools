package game.tools.db.mybatis.plush.sqlittable;

class ParamIndex
{
	private int paramIndex;

	int getParamIndex() {
		return paramIndex;
	}

	void setParamIndex(int paramIndex) 
	{
		if(paramIndex == 0)
			return ;
		
		this.paramIndex = paramIndex;
	}
	
	void clear()
	{
		this.paramIndex = 0;
	}
	
}

class SqlCondition 
{
	private String keyword;
	private String value;
	
	public SqlCondition(String keyword) 
	{
		this.keyword = keyword;
	}
	
	public String getValue() 
	{
		return value;
	}
	
	public void setValue(String value) 
	{
		this.value = value;
	}
	
	public String getKeyword() 
	{
		return keyword;
	}
	
	public void setKeyword(String keyword) 
	{
		this.keyword = keyword;
	}
	
	
}
