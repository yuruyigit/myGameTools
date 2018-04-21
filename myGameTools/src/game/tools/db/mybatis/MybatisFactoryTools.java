package game.tools.db.mybatis;

import java.util.ArrayList;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;

/**
 * mybatis多数据源工具，该数据源采用druid的数据连接池
 * @author zhibing.zhou
 */
public class MybatisFactoryTools {


	private static final MybatisFactoryToolsObject MYBATIS_OBJECT = new MybatisFactoryToolsObject();

	/**
	 * 注册对应mysql数据源， "IP:PORT->数据库名" 的字符串为数据源的key。
	 * 注：相同IP相同PORT的数据源，不能在次注册，即使数据库名不同。
	 * @param rootMappers 映射类根目录
	 * @param url 数据库地址
	 * @param username 数据源用户名
	 * @param password 数据源密码
	 * @return 返回对应数据源的key
	 */
	public static Object registerMyBatisFactory(String rootMappers,String url, String username, String password) 
	{
		return MYBATIS_OBJECT.registerMyBatisFactory(rootMappers, url, username, password);
	}
	
	/**
	 * 注册对应mysql数据源， "IP:PORT->数据库名" 的字符串为数据源的key
	 * @param rootMappers 映射类根目录
	 * @param url  数据库地址
	 * @param username 数据源用户名
	 * @param password 数据源密码
	 * @param interceptor 要添加mybatis的拦截器数组，可多个配置
	 * @return 返回对应数据源的key
	 */
	public static Object registerMyBatisFactory(String rootMappers, String url, String username, String password, Interceptor... interceptor) 
	{
		return MYBATIS_OBJECT.registerMyBatisFactory(rootMappers, url, username, password, interceptor);
	}
	
	/**
	 * @param sessionNo 会话编号ID
	 * @param mapperClass 映射类
	 * @return 返回对应的数据源映射对象，数据表的操作
	 */
	public static <T> T getMapper(Object sessionNo, Class<T> mapperClass) 
	{
		return MYBATIS_OBJECT.getMapper(sessionNo, mapperClass);
	}
	
	/**
	 * 注册的数据源，会全部执行一次。
	 * @param cmd 传入要调用执行的mapper函数
	 * @return 返回一个所有数据库Result列表 Result{dbNo , result}
	 */
	public static ArrayList<Result> executeAll(MybatisFactoryCmd cmd) 
	{
		return MYBATIS_OBJECT.executeAll(cmd);
	}
	
	/**
	 * @param cmd 传入要调用执行的mapper函数
	 * @return 返回一个随机数据库Result列表 Result{dbNo , result}
	 */
	public static Result executeRandom(MybatisFactoryCmd cmd)
	{
		return MYBATIS_OBJECT.executeRandom(cmd);
	}

	/**
	 * 从注册的数据源，依次执行命令，遇到不为null的命令就直接返回
	 * @param cmd
	 * @return
	 */
	public static Result executeReturn(MybatisFactoryCmd cmd) 
	{
		return MYBATIS_OBJECT.executeReturn(cmd);
	}

	/**
	 * @return 获取第一个注册的数据编号
	 */
	public static Object getFristSessionKey() 
	{
		return MYBATIS_OBJECT.getFristSessionKey();
	}
	
	public static int getSessionFactorySize() 
	{
		return MYBATIS_OBJECT.getSessionFactorySize();
	}
	
	public static void close()
	{
		MYBATIS_OBJECT.close();
	}
	
	
	public static void openPrintDbUrl()
	{
		MYBATIS_OBJECT.openPrintDbUrl();
	}
	
	public static void closePrintDbUrl()
	{
		MYBATIS_OBJECT.closePrintDbUrl();
	}
	
	public static SqlSession getSqlSession(Object sessionNo)
	{
		return MYBATIS_OBJECT.getSqlSession(sessionNo);
	}
	
}
