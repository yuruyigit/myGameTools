package game.tools.db.mybatis;

public class Result 
{
	private Object dbNo ; 
	private Object result;
	
	Result(Object dbNo , Object result)
	{
		this.dbNo = dbNo;
		this.result = result;
	}

	/**
	 * @return 返回这个数据节点编号
	 */
	public Object getDbNo() {
		return dbNo;
	}

	/**
	 * @return 返回对应执行的结果数据
	 */
	public <T> T getResult() {
		return (T)result;
	}
}
