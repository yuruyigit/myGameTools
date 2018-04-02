package game.tools.db.mybatis.plush.sqlittable;

public enum SqlCmd 
{
	
	SELECT("SELECT") , UPDATE("UPDATE") , DELETE("DELETE") , INSERT("INSERT");
	
	private String cmd ;
	private SqlCmd(String cmd) 
	{
		this.cmd = cmd;
	}
	public String getCmd() {
		return cmd;
	}
	
	
}
