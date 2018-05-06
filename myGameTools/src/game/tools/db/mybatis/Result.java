package game.tools.db.mybatis;

public class Result 
{
	private String dbNo ; 
	private Object result;
	
	Result(String dbNo , Object result)
	{
		this.dbNo = dbNo;
		this.result = result;
	}

	/**
	 * @return 返回这个数据节点编号
	 */
	public String getDbNo() {
		return dbNo;
	}

	/**
	 * @return 返回对应执行的结果数据
	 */
	public <T> T getResult() {
		return (T)result;
	}
}
