package game.tools.properties;

public class GameToolsProperties extends Properties
{
	public static String db_url;
	public static String db_user;
	public static String db_pwd;
	public static String db_initialSize;
	public static String db_minPoolSize;
	public static String db_maxPoolSize;
	public static String db_maxIdleTime;
	public static String db_retryAttempts;
	public static String db_acquireIncrement;
	
	
	static 
	{
		initProperties("conf/jdbc.properties", GameToolsProperties.class);
	}


	public static String getDb_url()	{		return db_url;	}
	public static String getDb_user()	{		return db_user;	}
	public static String getDb_pwd()	{		return db_pwd;	}
	public static String getDb_initialSize()	{		return db_initialSize;	}
	public static String getDb_minPoolSize()	{		return db_minPoolSize;	}
	public static String getDb_maxPoolSize()	{		return db_maxPoolSize;	}
	public static String getDb_maxIdleTime()	{		return db_maxIdleTime;	}
	public static String getDb_retryAttempts()	{		return db_retryAttempts;	}
	public static String getDb_acquireIncrement()	{		return db_acquireIncrement;	}
	
}
