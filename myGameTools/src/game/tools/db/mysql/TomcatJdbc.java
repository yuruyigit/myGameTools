package game.tools.db.mysql;

import java.sql.Connection;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;

public class TomcatJdbc
{
	
	private static final String driver = "com.mysql.jdbc.Driver";
    // 数据库连接地址
	private static final String jdbcUrl = "jdbc:mysql://192.168.1.131:3306/jjdtk_logic_s2";
    // 数据库用户名
	private static final String user = "root";
    // 数据库密码
	private static final String passwd = "root";
    // 连接池初始化大小
	private static final int initialSize = 5;
    // 连接池最小空闲
	private static final int minPoolSize = 10;
    // 连接池最大连接数量
	private static final int maxPoolSize = 50;
    // 最小逐出时间，100秒
	private static final int maxIdleTime = 100000;
    // 连接失败重试次数
	private static final int retryAttempts = 10;
    // 当连接池连接耗尽时获取连接数
	private static final int acquireIncrement = 5;
    
	private DataSource ds = null;
	
	/**
	 * 文件中使用的键，需和下面一致<br/>
	 * 	<br/>
	 *  #登录数据库地址<br/>
	 *	db_url=jdbc:mysql://192.168.1.131:3306/jjdtk_login<br/>
	 * 	#数据库用户名<br/>
	 *	db_user=root<br/>
	 *	#数据库密码<br/>
	 *	db_pwd=root<br/>
	 * 	#连接池初始化大小<br/>
	 *	db_initialSize=5<br/>
	 *	# 连接池最小空闲<br/>
	 *	db_minPoolSize=10<br/>
	 *	#连接池最大连接数量<br/>
	 *	db_maxPoolSize=50<br/>
	 *	#最小逐出时间，100秒<br/>
	 *	db_maxIdleTime=100000<br/>
	 *	#连接失败重试次数<br/>
	 *	db_retryAttempts=10<br/>
	 *	#当连接池连接耗尽时获取连接数<br/>
	 *	db_acquireIncrement=5<br/>
	 * @param p
	 */
	TomcatJdbc(Properties p)
	{
		tomcatDataSourceByProperties(p);
	}
	
	TomcatJdbc(String db_url , String db_user , String db_pwd)
	{
		tomcatDataSource(db_url , db_user , db_pwd);
	}
	
	/**
	 *  创建一个根据配置获取数据源连接池
	 */
	private void tomcatDataSourceByProperties(Properties p )
	{
        ds = new DataSource();
        ds.setUrl(p.getProperty("db.url"));
        ds.setUsername(p.getProperty("db.user"));
        ds.setPassword(p.getProperty("db.pwd"));
        ds.setDriverClassName(driver);
        ds.setInitialSize(Integer.parseInt(p.getProperty("db.initialSize")));
        ds.setMaxIdle(Integer.parseInt(p.getProperty("db.minPoolSize")));
        ds.setMaxActive(Integer.parseInt(p.getProperty("db.maxPoolSize")));
        ds.setTestWhileIdle(false);
        ds.setTestOnBorrow(false);
        ds.setTestOnConnect(false);
        ds.setTestOnReturn(false);
    }
	
	
	
	/**
	 * @param db_url 传的数据地址
	 * @param db_user 数据库用户名
	 * @param db_pwd 数据密码
	 * @return 返回一个采用默认配置的数据源连接池
	 */
	private  void tomcatDataSource(String db_url , String db_user , String db_pwd)
	{
		ds = new DataSource();
        ds.setUrl(db_url);
        ds.setUsername(db_user);
        ds.setPassword(db_pwd);
        ds.setDriverClassName(driver);
        ds.setInitialSize(initialSize);
        ds.setMaxIdle(minPoolSize);
        ds.setMaxActive(maxPoolSize);
        ds.setTestWhileIdle(false);
        ds.setTestOnBorrow(false);
        ds.setTestOnConnect(false);
        ds.setTestOnReturn(false);
    }



	Connection getConnection() throws Exception	
	{	
		return ds.getConnection();	
	}
}
