package game.tools.db.mybatis;

public class MybatisTools 
{

	/**
	 * @param url
	 * @return 返回以mysql的址url [ip:port->数据库名] 的唯一key
	 */
	public static String getMysqlKey(String url)
	{
		String [] dbUlrArr = url.split(":");
		
		String [] dbPortNameArr = null;
		if(dbUlrArr[3].indexOf("?") > 0)
			dbPortNameArr = dbUlrArr[3].substring(0, dbUlrArr[3].indexOf("?")).split("/");
		else
			dbPortNameArr = dbUlrArr[3].split("/");
		
		String dbIp = dbUlrArr[2].substring(2);
		String dbPort = dbPortNameArr[0];
		String dbName = dbPortNameArr[1];
		
		String key = dbIp+":"+dbPort+"->"+dbName;
		
		return key;
	}
	
}
