package game.tools.db.mybatis;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import com.alibaba.druid.pool.DruidDataSource;

import game.data.conf.entity.PrfWarZoneTitle;
import game.data.conf.mapper.PrfWarZoneTitleMapper;
import game.tools.fork.ForkJoinTools;
import game.tools.fork.SubForkTask;
import game.tools.utils.StringTools;
import game.tools.utils.Util;

public class MybatisFactoryToolsObject 
{
	/** 2017年4月22日 下午1:42:31			 mysql driver字符串*/
	private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	/** 2017年2月16日 上午11:13:35 mybatis的session列表集合<数据源Key， 数据源会话对象>			 */
	private ConcurrentHashMap<Object, SqlSessionTemplate> MYBATIS_SESSION_MAP = new ConcurrentHashMap<Object, SqlSessionTemplate>();

	/**
	 * 第一个注册的数据编号
	 */
	private Object FIRST_SESSION_KEY;
	
	/**
	 * 程序项目运行的路径
	 */
	private String RUN_PROJECT_PATH;
	
	/**
	 * 获取 getMapper 是否打印数据库的连接地址信息 dburl
	 */
	private boolean printDbUrl;
	
	/**
	 * 初始化时的数据库连接 , 最大连接 ， 连接处理等待时间 , 回收时间(单位:秒)
	 */
	private int initialSize , maxActive , maxWait , clearTimeout;
	
	/**
	 *  如果多个数据库，则会使用该工具进行分支多线程处理
	 */
	private ForkJoinTools forkJoinTools;
	
	public MybatisFactoryToolsObject() 
	{
		this.initialSize = 10;
		this.maxActive = 500;
		this.maxWait = 5000;
		this.clearTimeout = 180;
	}
	
	/**
	 * @param initialSize	初始化时的数据库连接
	 * @param maxActive		最大连接
	 * @param maxWait		连接处理等待时间(单位:秒)
	 * @param clearTimeout	回收时间(单位:秒)
	 */
	public MybatisFactoryToolsObject(int initialSize,int maxActive, int maxWait , int clearTimeout) 
	{
		this.initialSize = initialSize;
		this.maxActive = maxActive;
		this.maxWait = maxWait * 1000;
		this.clearTimeout = clearTimeout;
	}

	private DataSource createDataSource(String username, String password, String jdbcUrl)
	{
		return createDbcpDataSource(username, password, jdbcUrl, initialSize, maxActive, maxWait);
	}

	private DataSource createDbcpDataSource(String username, String password, String jdbcUrl, int initialSize,int maxActive, int maxWait) 
	{
		// System.out.println("createDbcpDataSource " + jdbcUrl );

		BasicDataSource dds = new BasicDataSource();
		dds.setDriverClassName(MYSQL_DRIVER);
		dds.setUsername(username);
		dds.setPassword(password);
		dds.setUrl(jdbcUrl + "?autoReconnect=true&autoReconnectForPools=true&QuseUnicode=true&characterEncoding=utf-8&serverTimezone=UTC");

		dds.setMaxActive(maxActive);
		dds.setInitialSize(initialSize);

		dds.setMaxWait(maxWait);
		dds.setMinIdle(initialSize / 2);
		
		//#在取出连接时进行有效验证
		dds.setValidationQuery("select 1"); // 用来检测连接是否有效的sql，要求是 一个查询语句。
//		dds.setTestWhileIdle(true);
//		dds.setTestOnBorrow(false);
		
//		dds.getDefaultAutoCommit();
		dds.setNumTestsPerEvictionRun(10);
		dds.setRemoveAbandoned(true);
		dds.setRemoveAbandonedTimeout(clearTimeout);
		dds.setMinEvictableIdleTimeMillis(clearTimeout * 1000);

		return dds;
	}

	private DataSource createDruidDataSource(String username, String password, String jdbcUrl, int initialSize,int maxActive, int maxWait) 
	{
		DruidDataSource dds = new DruidDataSource();
		dds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dds.setUsername(username);
		dds.setPassword(password);
		dds.setUrl(jdbcUrl + "?autoReconnect=true&useUnicode=true&characterEncoding=utf-8");

		dds.setMaxActive(maxActive);
		dds.setInitialSize(initialSize);

		dds.setMaxWait(maxWait);
		dds.setMinIdle(initialSize);

		dds.setValidationQuery("select 1"); // 用来检测连接是否有效的sql，要求是 一个查询语句。

		dds.setTestWhileIdle(true); // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，
									// 执行validationQuery检测连接是否有效。
		dds.setTestOnBorrow(false); // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
		dds.setTestOnReturn(false); // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。

		dds.setMinEvictableIdleTimeMillis(30000); // 配置一个连接在池中最小生存的时间，单位是毫秒

		dds.setRemoveAbandoned(true); // 开启超时限制
		dds.setRemoveAbandonedTimeout(60); // 超过时间限制多长单位秒
		dds.setTimeBetweenEvictionRunsMillis(60000);
		dds.setLogAbandoned(true);

		dds.setPoolPreparedStatements(true); // 打开PSCache
		dds.setMaxPoolPreparedStatementPerConnectionSize(20); // 打开PSCache，并且指定每个连接上PSCache的大小

		return dds;
	}
	
	private String getProjectPath() 
	{
		if(!StringTools.empty(RUN_PROJECT_PATH))
			return RUN_PROJECT_PATH;
			
		String realPath = null;
		
		URL url = MybatisFactoryTools.class.getClassLoader().getResource("");
		if(url == null)
			url = MybatisFactoryTools.class.getResource("");
		
		realPath = url.getFile();
		
		if(StringTools.empty(realPath))
		{
			java.io.File file = new java.io.File(realPath);
			realPath = file.getAbsolutePath();
		}
		
		if(realPath.indexOf("!") >= 0)
		{
			realPath = realPath.substring(realPath.indexOf("/") , realPath.indexOf("!"));
			realPath = realPath.substring(1 , realPath.lastIndexOf("/"));
		}
		
//		System.out.println("realPath1 = " + realPath);

        RUN_PROJECT_PATH = realPath;
        
        return realPath;
	}
	 
	private String getRunJarPath(String rootMapper) 
	{
		String projectPath = "/" + getProjectPath() + "/" + System.getProperty("java.class.path");
//		String allPath = MybatisFactoryTools.class.getResource("/"+rootMapper.replace('.', '/')+"/").getPath();
////		allPath = System.getProperty("java.class.path"); 
////		allPath = " file:/data/server/gameLogic/gameLogic.jar!/game/data/sys/mapper/";
////		/D:/Tools/apache-tomcat-8.0.38/webapps/doraemonManager/WEB-INF/classes/web/data/mapper/
////		System.out.println("allPath = " + allPath);
//		
//		String osName = System.getProperty("os.name");
//		String projectPath = allPath;
////		
//		if(osName.indexOf("Windows") >= 0)		//如果是window平台
//		{
//			String [] arrayString = allPath.split("!")[0].split(":");
//			projectPath = arrayString[1].split("/")[1] + ":" + arrayString[2];
//		}
//		else
//		{
//			String [] arrayString = allPath.split("!")[0].split(":");
//			projectPath = arrayString[arrayString.length - 1];
//		}
//		System.out.println("projectPath = "  + projectPath);
		
		return projectPath;
	}
	
	
	/**
	 * 是否发布执行
	 */
	private boolean isPublish()
	{
		boolean publish = false;
		
		String urlString = getProjectPath();
		
		if(StringTools.empty(urlString))
		{
			publish = true;
		}
		else
		{
			if(urlString.indexOf("/.metadata") >= 0 || urlString.indexOf("/webapps") >= 0)	//是b/s
			{
				if(urlString.indexOf("/classes") < 0 ) 
					publish = true;
				else
					publish = false;
			}
			else
			{
				if(urlString.indexOf("/bin") < 0) 
					publish = true;
				else
					publish = false;
			}
		}
		
//		System.out.println("publish = " +publish+ " urlString = " + urlString );
		
		return publish;
	}
	
	/**
	 * @param rootMapper  映射类根目录
	 * @return 返回对应根目录下映射类列表
	 */
	private ArrayList<Class> getMapperClassList(String rootMapper) 
	{
		boolean publish = isPublish();
		
		if(publish)
		{
			ArrayList<Class> clslist = getJarFileClassList(rootMapper);
			if(clslist != null)
				return clslist;
			else
				return null;
		}
		else
		{
			URL url = MybatisFactoryToolsObject.class.getClassLoader().getResource("");
			
			String path = url.getPath();
			
			String mapperFile  = path.trim() + rootMapper.replace(".", "/");
			File rootF = new File(mapperFile);
			File[] fileArray = rootF.listFiles();

			if (fileArray == null)
				return null;

			ArrayList<Class> clsList = new ArrayList<Class>(fileArray.length);

			try 
			{
				for (int i = 0; i < fileArray.length; i++)
				{
					File f = fileArray[i];
					String[] nameArray = f.getName().split("\\.");
					String name = nameArray[0];
					String suffix = nameArray[1];

					if (suffix.equalsIgnoreCase("java") || suffix.equalsIgnoreCase("class")) 
					{
						String mapperClassName = rootMapper + "." + name;
						Class clzss = Class.forName(mapperClassName);

						if (!clsList.contains(clzss))
							clsList.add(clzss);
					}
				}
			}
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}

			return clsList;
			
		}
		
	}

	/**
	 * @param dataSource  数据源
	 * @param mapperList 映射类列表
	 * @return 返回myBatis的SqlSessionFactory
	 */
	private SqlSessionFactory createSqlSessionFactory(DataSource dataSource, ArrayList<Class> mapperList) 
	{
		// System.out.println(Arrays.toString(mapperList.toArray()));
		TransactionFactory transactionFactory = new SpringManagedTransactionFactory(); // 使用spring
		
//		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//		sessionFactoryBean.setDataSource(dataSource);
//		sessionFactoryBean.getObject();
		
//		TransactionFactory transactionFactory = new JdbcTransactionFactory();			// 来管理事务
//		DataSourceTransactionManager dstm = new DataSourceTransactionManager();
//		dstm.setDataSource(dataSource);
//		System.out.println("Register Mapper Class Count = " + mapperList.size() );
//		new DataSourceTransactionManager().setDataSource(dataSource);

		Environment environment = new Environment("jdbc", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		for (Class mapperClass : mapperList)
			configuration.addMapper(mapperClass);

		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

		return sqlSessionFactory;
	}

	private SqlSessionFactory createSqlSessionFactory(String rootMappers, String username, String password,String url) 
	{
		DataSource dataSource = createDataSource(username, password, url);

		ArrayList<Class> mapperList = getMapperClassList(rootMappers);

		SqlSessionFactory sqlSessionFactory = createSqlSessionFactory(dataSource, mapperList);

		return sqlSessionFactory;
	}

	/**
	 * 注册对应mysql数据源， "IP:PORT->数据库名" 的字符串为数据源的key。
	 * 注：相同IP相同PORT的数据源，不能在次注册，即使数据库名不同。
	 * @param rootMappers 映射类根目录
	 * @param url 数据库地址
	 * @param username 数据源用户名
	 * @param password 数据源密码
	 * @return 返回对应数据源的key
	 */
	public Object registerMyBatisFactory(String rootMappers,String url, String username, String password) 
	{
		Object mysqlKey = MybatisTools.getMysqlKey(url) ;
		
		registerMyBatisFactory(mysqlKey, rootMappers , url , username , password , null);
		
		return mysqlKey;
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
	public Object registerMyBatisFactory(String rootMappers, String url, String username, String password, Interceptor... interceptor) 
	{
		Object mysqlKey = MybatisTools.getMysqlKey(url) ;
		
		registerMyBatisFactory(mysqlKey , rootMappers , url , username , password , interceptor);
		
		return mysqlKey;
	}
	
	private void registerMyBatisFactory(Object sessionNo, String rootMappers, String url, String username, String password, Interceptor... interceptor) 
	{
		if(sessionNo == null)
		{
			try {
				throw new Exception("SessionNo Is Null");
			} catch (Exception e) {
				e.printStackTrace();
				return ;
			}
		}
		
		if (MYBATIS_SESSION_MAP.get(sessionNo) != null)
			return;

		SqlSessionFactory factory = createSqlSessionFactory(rootMappers, username, password, url);

		if(interceptor != null)
		{
			for (int i = 0; i < interceptor.length; i++)
				factory.getConfiguration().addInterceptor(interceptor[i]);
		}

		SqlSessionTemplate session = new SqlSessionTemplate(factory);			//这里使用spring的SqlSessionTemplate为sqlsession的包装类，来管理session,否则运行可能出错
		
		MYBATIS_SESSION_MAP.put(sessionNo, session);
		
		if(MYBATIS_SESSION_MAP.size() == 1)
			FIRST_SESSION_KEY = sessionNo;
			
		
		System.out.println("------------>Mysql Start On = " + Arrays.toString(MYBATIS_SESSION_MAP.keySet().toArray()));
	}

//	public SqlSessionFactory getSessionFactory(Object sessionNo)
//	{
//		return MYBATIS_FACTORY_MAP.get(sessionNo);
//	}

	public int getSessionFactorySize() 
	{
		return MYBATIS_SESSION_MAP.size();
	}

	/**
	 * @param sessionNo 会话编号ID
	 * @param mapperClass 映射类
	 * @return 返回对应的数据源映射对象，数据表的操作
	 */
	public <T> T getMapper(Object sessionNo, Class<T> mapperClass) 
	{
//		SqlSessionFactory factory = MYBATIS_FACTORY_MAP.get(sessionNo);
//		SqlSession session = new SqlSessionTemplate(factory);		//这里使用spring的SqlSessionTemplate为sqlsession的包装类，来管理session,否则运行可能出错
		
		SqlSession session = MYBATIS_SESSION_MAP.get(sessionNo);
		if(session != null)
		{
			if(printDbUrl)
			{
				try {
					System.out.println("db url : " + session.getConfiguration().getEnvironment().getDataSource().getConnection().getMetaData().getURL());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return session.getMapper(mapperClass);
		}
		else
			return null;
		
//		if (session == null) 
//		{
//			session = new SqlSessionTemplate(factory);		//这里使用spring的SqlSessionTemplate为sqlsession的包装类，来管理session,否则运行可能出错
//			MYBATIS_SESSION_MAP.put(sessionNo, (SqlSessionTemplate)session);
//		}
//		
//		return session.getMapper(mapperClass);
	}

	/**
	 * 注册的数据源，会全部执行一次。
	 * @param cmd 传入要调用执行的mapper函数
	 * @return 返回一个所有数据库Result列表 Result{dbNo , result}
	 */
//	public ArrayList<Result> executeAll(MybatisFactoryCmd cmd) 
//	{
//		ArrayList<Result> resultList = new ArrayList<>(MYBATIS_FACTORY_MAP.size());
//
//		Iterator<Long> iterator = MYBATIS_FACTORY_MAP.keySet().iterator();
//
//		while (iterator.hasNext()) 
//		{
//			long dbNo = iterator.next();
//
//			Object result = cmd.doCmd(dbNo, getMapper(dbNo,  cmd.getTClass()));
//			
//			resultList.add(new Result(dbNo, result));
//		}
//		
//		return resultList;
//	}
	
	private void checkForkJoinTool()
	{
		if(forkJoinTools == null)
		{
			synchronized (MYBATIS_SESSION_MAP) 
			{
				if(this.forkJoinTools == null)
					this.forkJoinTools = new ForkJoinTools();
			}
		}
	}
	
	/**
	 * 注册的数据源，会全部执行一次。
	 * @param cmd 传入要调用执行的mapper函数
	 * @return 返回一个所有数据库Result列表 Result{dbNo , result}
	 */
	public ArrayList<Result> executeAll(MybatisFactoryCmd cmd) 
	{
		
		if(MYBATIS_SESSION_MAP.size() == 1)			//如果只有一个数据源的话，则不使用分支多线程处理
		{
			ArrayList<Result> resultList = new ArrayList<>(MYBATIS_SESSION_MAP.size());

			Iterator<Object> iterator = MYBATIS_SESSION_MAP.keySet().iterator();

			while (iterator.hasNext()) 
			{
				Object dbNo = iterator.next();

				Object result = cmd.doCmd(dbNo, getMapper(dbNo,  cmd.getTClass()));
				
				resultList.add(new Result(dbNo, result));
			}
			
			return resultList;
		}
		else					//如果有多个数据源的话， 则使用多线程分支处理
		{
			checkForkJoinTool();
			
			Iterator<Object> iterator = MYBATIS_SESSION_MAP.keySet().iterator();
			
			while (iterator.hasNext()) 
			{
				Object dbNo = iterator.next();
				
				forkJoinTools.addTasks(getSubForkTask(dbNo, cmd.getTClass(), cmd));
			}
			
			return forkJoinTools.submit();
		}
		
	}
	
	/**
	 * @return 返回随机一个数据源结节编号
	 */
	private Object getRandomMybatisFactoryDbNo() 
	{
		if(MYBATIS_SESSION_MAP.size() == 1)
			return FIRST_SESSION_KEY;
		
		int index = 0 , randomIndex = Util.getRandomInt(0, MYBATIS_SESSION_MAP.size() - 1);
		
		Iterator<Object> sessionIterator = MYBATIS_SESSION_MAP.keySet().iterator();
		
		while(sessionIterator.hasNext())
		{
			Object dbNo = sessionIterator.next();
			if(index == randomIndex)
				return dbNo;
			
			index++;
		}
		return null;
	}

	/**
	 * @param cmd 传入要调用执行的mapper函数
	 * @return 返回一个随机数据库Result列表 Result{dbNo , result}
	 */
	public Result executeRandom(MybatisFactoryCmd cmd)
	{
		Class cls = cmd.getTClass();

		Object dbNo = getRandomMybatisFactoryDbNo();

		Object result = cmd.doCmd(dbNo, getMapper(dbNo, cls));

		return new Result(dbNo, result);
	}

	/**
	 * 从注册的数据源，依次执行命令，遇到不为null的命令就直接返回
	 * @param cmd
	 * @return
	 */
	public Result executeReturn(MybatisFactoryCmd cmd) 
	{
		Class cls = cmd.getTClass();

		Iterator<Object> iterator = MYBATIS_SESSION_MAP.keySet().iterator();

		while (iterator.hasNext()) 
		{
			Object dbNo = iterator.next();

			Object result = cmd.doCmd(dbNo, getMapper(dbNo, cls));

			if (result != null) 
			{
				if (result instanceof List) 
				{
					List list = (List) result;
					if (list.size() > 0) 
					{
						return new Result(dbNo, result);
					}
				} 
				else 
				{
					return new Result(dbNo, result);
				}
			}
		}

		return null;
	}
	
	
	private ArrayList<Result> executeReturnNew(MybatisFactoryCmd cmd) 
	{
		ArrayList<Result> resultList = new ArrayList<>();

		Class cls = cmd.getTClass();

		Iterator<Object> iterator = MYBATIS_SESSION_MAP.keySet().iterator();

		while (iterator.hasNext()) 
		{
			Object dbNo = iterator.next();

			Object result = cmd.doCmd(dbNo, getMapper(dbNo, cls));

			if (result != null) 
			{
				if (result instanceof List) 
				{
					List list = (List) result;
					if (list.size() > 0) 
					{
						resultList.add(new Result(dbNo, result));
						return resultList;
					}
				} 
				else 
				{
					resultList.add(new Result(dbNo, result));
					return resultList;
				}
			}
		}

		return resultList;
	}


	/**
	 * @param rootMapper 指定映射类包根目录 
	 * @return 返回jar文件中的类映射列表
	 */
	private ArrayList<Class> getJarFileClassList(String rootMapper) 
	{
		String rootMapperDir = rootMapper.replace(".", "/");
		ArrayList<Class> classList = null;
		try
		{
			String runProjectPath = getRunJarPath(rootMapperDir);
			ZipFile  zipFile = new ZipFile(runProjectPath);
			
			Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();  
            while (enu.hasMoreElements()) 
            {  
                ZipEntry zipElement = (ZipEntry) enu.nextElement();  
                InputStream read = zipFile.getInputStream(zipElement);
                
                String fileName = zipElement.getName();
                
                if (fileName != null && fileName.indexOf(rootMapperDir) >= 0)			//指定rootMapper目录下
                {  
	                	if(fileName.indexOf("class") >= 0)				//属于class文件 
	                	{
	                		if(classList == null)
	                			classList = new ArrayList<>();
	                		
	                		String className = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length() - 6);
	//                		System.out.println("fileName = " + fileName + " className = " + className);
	                		Class cls = Class.forName(rootMapper+"."+className);
	                		classList.add(cls);
	                	}
                	
                }  
                
            }
            
            zipFile.close();
            
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return classList;
	}
	
	public static void main(String[] args) throws Exception 
	{
		
		String path = "file:/C:/Users/zhibing.zhou/Desktop/football_battle_report_server/football_battle_report_server.jar!/game/tools/db/mybatis/";
		
		if(path.indexOf("!") >= 0)
		{
			String [] array = path.split("!");
			
			System.out.println();
		}
//		ArrayList<Class> clslist = getJarFileClassList("game.data.conf.mapper");
//		if(clslist != null)
//			return;
		
//		getJarPath("game.data.conf.mapper");
		
		int gameLogicConf1 = 2;

		MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://182.254.152.149:3306/game_logic_conf" ,
				"innertest01", "innertest01");
		MybatisFactoryTools.registerMyBatisFactory("game.data.conf.mapper", "jdbc:mysql://182.254.152.149:3306/game_logic_conf" ,
				"innertest01", "innertest01");
		
		
		List<Result> listt = MybatisFactoryTools.executeAll(new MybatisFactoryCmd<PrfWarZoneTitleMapper>() {

			@Override
			public Object doCmd(Object dbNo, PrfWarZoneTitleMapper mapper) {
				int size = mapper.selectAll().size();
				
				System.out.println("test cmd " + size );
				try
				{
					Thread.sleep(1000L);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				return null;
			}

		});
		
		System.out.println("start...");
		
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < 1; i++)
		{
			List<PrfWarZoneTitle> lists = new ArrayList<>();

//			List<Result> list1 = MybatisFactoryTools.executeAll(new MybatisFactoryCmd<PrfWarZoneTitleMapper>() {
//
//				@Override
//				public Object doCmd(long dbNo, PrfWarZoneTitleMapper mapper) 
//				{
//					
//					lists.addAll(mapper.selectAll());
//					
//					System.out.println("cmd");
//					try
//					{
//						Thread.sleep(1000L);
//					}
//					catch (InterruptedException e)
//					{
//						e.printStackTrace();
//					}
//					return null;
//				}
//
//			});
			
			
			
			List<Result> list2 = MybatisFactoryTools.executeAll(new MybatisFactoryCmd<PrfWarZoneTitleMapper>() {

				@Override
				public Object doCmd(Object dbNo, PrfWarZoneTitleMapper mapper)
				{
					
					System.out.println("fork cmd");
					try
					{
						Thread.sleep(1000L);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					
					return lists.addAll(mapper.selectAll());
				}

			});

			System.out.println(" i = " + i + " " + lists.size());
		}

		long endTime = System.currentTimeMillis();

		System.out.println("gapTime = " + (endTime - startTime));

//		 PrfWarZoneTitle wt = new PrfWarZoneTitle();
//		 wt.setTitle("titleupdat111e");
//		 wt.setReward("test");
//		 wt.setRankStart(1000);
//		 wt.setRankEnd(10000);
		
//		
//		 PrfWarZoneTitleMapper mapper = getMapper(2 , PrfWarZoneTitleMapper.class);
//		 int no = mapper.insert(wt);
		
		Thread.sleep(3000L);
	}
	
	
	private SubForkTask getSubForkTask(Object dbNo, Class cls  , MybatisFactoryCmd cmd  )
	{
		SubForkTask subForkTask = new SubForkTask()
		{
			/** 2016年10月24日上午11:49:58  */
			private static final long serialVersionUID = 1L;

			@Override
			protected Object compute()
			{
				Object result = cmd.doCmd(dbNo, getMapper(dbNo, cls));
				
				return new Result(dbNo, result);
			}
		};
		
		return subForkTask;
	}

	/**
	 * @return 获取第一个注册的数据编号
	 */
	public Object getFristSessionKey() 
	{
		return FIRST_SESSION_KEY;
	}
	
	public void close() 
	{
//		for (SqlSessionTemplate session : MYBATIS_SESSION_MAP.values()) 
//		{
//			session.close();			//目前关闭不了，由spring管理连接。
//		}
		MYBATIS_SESSION_MAP.clear();
	}
	
	public void openPrintDbUrl()
	{
		printDbUrl = true;
	}
	
	public void closePrintDbUrl()
	{
		printDbUrl = true;
	}
}
