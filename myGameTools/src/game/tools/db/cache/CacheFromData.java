package game.tools.db.cache;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSONObject;
import game.tools.log.LogUtil;
import game.tools.utils.Util;

/**
 * 策划配置表数据库缓存数据工具
 * @author zhibing.zhou
 */
public class CacheFromData 
{
	/** 2016年4月21日上午6:21:38  大字英文字母*/
	private static final char [] UPPER_CASE = {'A' , 'B' , 'C', 'D', 'E', 'F', 'G' , 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	
	/** 2017年4月22日 下午1:42:31			 mysql driver字符串*/
	private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	/** 2016年4月21日上午7:24:20 缓存表数据 */
	private final ConcurrentHashMap<String, ConcurrentHashMap<Long, Object>> CACHE_TABLE_DATA = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Object>>();
	
	/** 要生成属性类的配置表名列表*/
	private ArrayList<String> attributeTableList;
	/** 2016年6月15日上午1:35:07实体对象类根路径 , java 实体类源文件路径 , bin编译class的路径*/
	private String entityPackage , srcEntityPath , binClassPath;
	/** 2016年6月15日上午5:59:56 是否创建实体类文件 */
	private boolean isCreateFile;
	/** 2016年7月18日下午5:56:56  mysql地址，用户名， 密码*/
	private String mysqlUrl , username , password;
	/** 2016年7月18日下午6:10:14  数据连接*/
	private Connection connection ;
	
	
	
	
	
	public static void main(String[] args) 
	{
		
		CacheFromData cfd1 = new CacheFromData("game.tools.db.entity" , true ,"jdbc:mysql://localhost:3306/static_dev" , "root" , "root" );
//		CacheFromData cfd2 = new CacheFromData("game.tools.db.entity" , true ,"jdbc:mysql://192.168.1.131:3306/game_manager" , "innertest01" , "innertest01" );
		
//		cfd.initCacheFromData();
		
		cfd1.setAttributeTable("cmd_code_s");
		cfd1.initCacheFromTableData("cmd_code_s" , "constant_s");
//		cfd2.initCacheFromTableData("sys_channel");
		
//		cmd_code_s s = cfd1.getObjectById(TableName.cmd_code_s, -1);
//		System.out.println(s.getS2c_desc());
//		System.out.println(cmd_code_s.ABBBBB[1][2]);
//		prf_card_new obj = cfd.getObjectById("prf_card_new", 2000001);
//		prf_arena_reward obj1 = cfd.getObjectById("prf_arena_reward", 2);
//		System.out.println(obj + " " + obj1);
		Object map = cfd1.getMapByTable("PlanPayGift");
		
		Object obj1 = cfd1.getObjectById("PlanPayGift", 1002);
		System.out.println(obj1);
	}
	
	
	
	
	
	/**
	 * <PRE>
	 * 数据库实体类缓存工具，【支持自动生成实体类对象】。
	 * 该工具会在指定的实体包下映射数据体，如果没有该实体java文件，则会自动生成对应的实体类文件 。
	 * 默认【不创建实体类java文件】。
	 * 
	 * 约定规范：
	 *  &#9; 每张表中【必须要有ID,或id】的唯一流水字段。
	 *  &#9; 用于主键，唯一标识，否则不会根据ID获取该数据项。
	 *  &#9; 如果不存在ID字段，则使用第一列为主键，且第一列必须为数字字段。
	 * </PRE>
	 * @param entityPackage 实体类包路径
	 * @param mysqlUrl mysql数据库地址
	 * @param username 用户名
	 * @param password 密码
	 */
	public CacheFromData(String entityPackage , String mysqlUrl , String username , String password) 
	{
		this(entityPackage , false , mysqlUrl , username , password);
	}
	
	/**
	 * <PRE>
	 * 数据库实体类缓存工具，【支持自动生成实体类对象】。
	 * 该工具会在指定的实体包下映射数据体，如果没有该实体java文件，则会自动生成对应的实体类文件 。
	 * 默认【不创建实体类java文件】。
	 * 
	 * 约定规范：
	 *  &#9; 每张表中【必须要有ID,或id】的唯一流水字段。
	 *  &#9; 用于主键，唯一标识，否则不会根据ID获取该数据项。
	 *  &#9; 如果不存在ID字段，则使用第一列为主键，且第一列必须为数字字段。
	 * </PRE>
	 * @param entityPackage 实体类包路径
	 * @param isCreateFile 是否自动创建实体类
	 * @param mysqlUrl mysql数据库地址
	 * @param username 用户名
	 * @param password 密码
	 */
	public CacheFromData(String entityPackage , boolean isCreateFile , String mysqlUrl , String username , String password) 
	{
		this.entityPackage = entityPackage;
		this.isCreateFile = isCreateFile;
		this.mysqlUrl = mysqlUrl;
		this.username = username;
		this.password = password;
		
		initConnection();
	}
	
	/**
	 * @param tableName 要设置成静态属性类的表。
	 */
	public void setAttributeTable(String ...tableNameArray)
	{
		if(attributeTableList == null)
		{
			synchronized (this)
			{
				if(attributeTableList == null)
					this.attributeTableList = new ArrayList<String>(tableNameArray.length + 5 );
			}
		}
		
		for (String tablleName : tableNameArray) 
			attributeTableList.add(tablleName);
	}
	
	
	private void initSrcBinPath()
	{
		this.srcEntityPath = getSrcEntityPath();
		this.binClassPath = getBinClassPath();
	}
	
	private void initConnection() 
	{
		connection = getConnection(this.mysqlUrl, this.username, this.password);
	}
	
//	public void openCreateEntityFile()
//	{
//		this.isCreateFile = true;
//	}
//	
//	public void closeCreateEntityFile()
//	{
//		this.isCreateFile = false;
//	}
	
	
	/**
	 * 创建TableNames类，主要存放哪些表名的java类
	 * @param className
	 * @param tableResult
	 * @return 
	 */
	private boolean createTableNameJavaFile(ArrayList<String> tableNameList)
	{
		if(!isCreateFile)
			return false;
		
		String srcEntityPath = this.srcEntityPath;
		String className = "TableName";
		
		StringBuilder sb = new StringBuilder();
		
		String javaFileName = className + ".java";
		try 
		{
			FileWriter fw = new FileWriter(new File(srcEntityPath + javaFileName));
			sb.append("package ").append(entityPackage).append(";\n\n");
			sb.append("public class ").append(className).append("\n");
			sb.append("{\n\n ");
			
			for (String tableName : tableNameList) 
			{
				sb.append("\t public static final String ").append(tableName).append(" = \"").append(tableName).append("\";\n");
			}
			sb.append("\n").append("}");
			
			fw.append(sb.toString());
			fw.flush();
			fw.close();
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
			return false;
		}
		
		System.out.println("create " + javaFileName + " ok !");
		return true;
	}
	
	
	/**
	 * 创建TableNames类，主要存放哪些表名的java类
	 * @param className
	 * @param tableResult
	 * @return 
	 */
	private boolean createTableNameEnumJavaFile(ArrayList<String> tableNameList)
	{
		String srcEntityPath = this.srcEntityPath;
		String className = "TableName";
		
		StringBuilder sb = new StringBuilder();
		
		String javaFileName = className + ".java";
		try 
		{
			FileWriter fw = new FileWriter(new File(srcEntityPath + javaFileName));
			sb.append("package ").append(entityPackage).append(";\n\n");
			sb.append("public enum ").append(className).append("\n");
			sb.append("{\n\n ");
			
			for (int i = 0; i < tableNameList.size(); i++) 
			{
				String tableName = tableNameList.get(i);
				if(i < tableNameList.size() - 1 )
					sb.append("\t").append(tableName).append("(\"").append(tableName).append("\"),\n");
				else	
					sb.append("\t").append(tableName).append("(\"").append(tableName).append("\");\n\n");
			}
			
			
			
			sb.append("\tprivate String name ;\n");
			sb.append("\tprivate ").append(className).append("( String name )\n");
			sb.append("\t{\n");
			sb.append("\t\tthis.name = name;\n");
			sb.append("\t}\n\n");
			sb.append("\tpublic String getName()	{	return name ; 	} \n");
			
			sb.append("\n").append("}");
			
			fw.append(sb.toString());
			fw.flush();
			fw.close();
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
			return false;
		}
		
		System.out.println("create " + javaFileName + " ok !");
		return true;
	}
	
	
	/**
	 * 创建属性类java文件
	 * @param className
	 * @param tableResult
	 * @return 返回是否创建成功
	 */
	private boolean createAttributeJavaFile(String className, ResultSet tableResult)
	{
		String srcEntityPath = this.srcEntityPath;
		
		StringBuilder sb = new StringBuilder();
		
		String javaFileName = className + ".java";
		try 
		{
			FileWriter fw = new FileWriter(new File(srcEntityPath + javaFileName));
			sb.append("package ").append(entityPackage).append(";\n\n");
			sb.append("public class ").append(className).append("\n");
			sb.append("{\n ");
			
			while(tableResult.next())
			{
				String attributeName = tableResult.getString(2);
				if(null != attributeName && !("".equals(attributeName)))
				{
					String columnNameType = tableResult.getMetaData().getColumnTypeName(3);
					String attributeType= getJavaTypeString(columnNameType);
					
					String attributeValue = tableResult.getString(3);
					if(attributeValue == null)
						continue;
					
					String attributeDesc = tableResult.getString(4);
					
					sb.append("\t /*** ").append(attributeDesc).append("*/ \n");
					
					if(attributeValue.indexOf("|") > 0  && attributeValue.indexOf(",") > 0 )
					{
						String [] iValueArray = attributeValue.split("\\|");
						
						StringBuffer valueJsonString = new StringBuffer();
						valueJsonString.append("{");
						
						for(int j = 0 ; j < iValueArray.length; j ++)
						{
							String ivalue = iValueArray[j];
							
							String [] sValueArray = ivalue.split(",");
							valueJsonString.append("{");
							
							
							for(int i = 0 ; i < sValueArray.length; i ++)
							{
								String svalue = sValueArray[i];
								if(i < sValueArray.length - 1)
									valueJsonString.append(svalue).append(",");
								else
									valueJsonString.append(svalue);
							}
							
							if(j < iValueArray.length - 1)
								valueJsonString.append("},");
							else
								valueJsonString.append("}");
						}
						
						valueJsonString.append("}; \n");
						
						sb.append("\t public static final float [][] ").append(attributeName.toUpperCase()).append(" = ").append(valueJsonString);
					}
					else if(attributeValue.indexOf(",") > 0)
					{
						StringBuffer valueJsonString = new StringBuffer();
						
						String [] sValueArray = attributeValue.split(",");
						valueJsonString.append("{");
						
						for(int i = 0 ; i < sValueArray.length; i ++)
						{
							String svalue = sValueArray[i];
							if(i < sValueArray.length - 1)
								valueJsonString.append(svalue).append(",");
							else
								valueJsonString.append(svalue);
						}

						valueJsonString.append("}; \n");

						sb.append("\t public static final float [] ").append(attributeName.toUpperCase()).append(" = ").append(valueJsonString);
					}
					else
						sb.append("\t public static final ").append(attributeType).append(" ").append(attributeName).append(" = \"").append(attributeValue).append("\"; \n");
				}
			}
				
			sb.append("\n\n").append("}");
			
			fw.append(sb.toString());
			fw.flush();
			fw.close();
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
			return false;
		}
		
		System.out.println("create " + javaFileName + " ok !");
		return true;
	}
	
	/**
	 * 创建实体类java代码文件
	 * @param tableResult 
	 * @return
	 */
	private boolean createEntityJavaFile(String className, ResultSet tableResult)
	{
		String srcEntityPath = this.srcEntityPath;
		
		StringBuilder sb = new StringBuilder();
		
		ArrayList<JSONObject> attributeNameList = new ArrayList<JSONObject>();
		
		String javaFileName = className + ".java";
		try 
		{
			FileWriter fw = new FileWriter(new File(srcEntityPath + javaFileName));
			sb.append("package ").append(entityPackage).append(";\n\n");
			sb.append("public class ").append(className).append("\n");
			sb.append("{\n ");
			
				
			int count = tableResult.getMetaData().getColumnCount();
			
			for (int i = 1; i <= count; i++) 
			{
				String columnName = tableResult.getMetaData().getColumnName(i);
				String columnNameType = tableResult.getMetaData().getColumnTypeName(i);
				
				String attributeType= getJavaTypeString(columnNameType);
				
				sb.append("\t private ").append(attributeType).append(" ").append(columnName).append("; \n");
				
				JSONObject o = new JSONObject();
				o.put("columnName", columnName);
				o.put("attributeType", attributeType);
				
				attributeNameList.add(o);
			}
			
			sb.append("\n\n");
			
//			System.out.println(sb.toString());
			
			for (JSONObject o : attributeNameList) 
			{
				String attributeType = o.getString("attributeType");
				String columnName = o.getString("columnName");
				
				String fristChar = columnName.substring(0,1);
				String attributeName = columnName.replaceFirst(fristChar, fristChar.toUpperCase());
				
				sb.append("\t private void set").append(attributeName).append("(").append(attributeType).append(" ").append(columnName).append(")\n");
				sb.append("\t { \n");
				sb.append("\t\t this.").append(columnName).append(" = ").append(columnName).append(";\n");
				sb.append("\t } \n\n");
				
				sb.append("\t public ").append(attributeType).append(" get").append(attributeName).append("() \n");
				sb.append("\t { \n");
				sb.append("\t\t return this.").append(columnName).append(";\n");
				sb.append("\t } \n\n");
			}
			
			sb.append("\n\n").append("}");
			
			fw.append(sb.toString());
			fw.flush();
			fw.close();
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
			return false;
		}
		
		System.out.println("create " + javaFileName + " ok !");
		return true;
	}
	
	/**
	 * @param columnNameType
	 * @return 返回数据库对应java类型的字符串
	 */
	private String getJavaTypeString(String columnNameType) 
	{
		switch (columnNameType.toLowerCase()) 
		{
			case "varchar":
			case "mediumtext":
			case "text":
			case "longtext":
				return "String";
				
			case "bigint":
				return "long";
			case "int":
				return "int";
			case "float":
				return "float";
			case "double":
				return "double";
			default:
				break;
		}
		return null;
	}

	/**
	 * @return 返回实体类源代码目录路径
	 */
	private String getSrcEntityPath()
	{
		String rootEntityPath = this.entityPackage.replace(".", "/");
		String path = new File("").getAbsolutePath();
//		String path = CacheFromData.class.getClassLoader().getResource("").getPath();
		path = path + "/src/" + rootEntityPath + "/";
		
		return path;
	}
	
	
	/**
	 * @return 返回存放的class目录，主要用于动态编译
	 */
	private String getBinClassPath()
	{
//		String path = "".getClass().getResource("/").getPath();
		String path = CacheFromData.class.getClassLoader().getResource("").getPath();
		return path.substring(1);
	}
	
	
	/**
	 * 从指定数据库中初始化所有配置表缓存
	 */
	public void initCacheFromData()
	{
		ArrayList<String> tableNameList = getTables(connection);
		
		isCreateFile = isCreate(tableNameList);
		
		if(isCreateFile)
			initSrcBinPath();
		
		createTableNameJavaFile(tableNameList);
		
		clear();
		
		fillJavaCacheMapObject(connection , tableNameList);
	}
	


	/**
	 * 按传入指定的表名称 初始化缓存
	 * @param tableNameArray 需要初始化加载的表名列表数组
	 */
	public void initCacheFromTableData(String ...tableNameArray)
	{
		if(tableNameArray == null || tableNameArray.length <= 0)
			return ;
		
		clear();
		
		ArrayList<String> tableNameList = new ArrayList<>(tableNameArray.length);
		for (String tableName : tableNameArray) 
			tableNameList.add(tableName);
		
		isCreateFile = isCreate(tableNameList);
		
		if(isCreateFile)
			initSrcBinPath();
		
		createTableNameJavaFile(tableNameList);
		
		fillJavaCacheMapObject(connection , tableNameList);
	}
	
	
	/**
	 * 是否发布执行
	 */
	private boolean isPublish()
	{
		boolean publish = false;
		
		URL url = CacheFromData.class.getClassLoader().getResource("");
		
		if(url == null)
			publish = true;
		else
		{
			String urlString = url.getPath();
			if(urlString.indexOf("bin") < 0)
				publish = true;
			else
				publish = false;
		}
		
//		System.out.println("publish = " +publish+ " absFile = " + CacheFromData.class.getClassLoader().getResource("") );
		
		return publish;
	}
	
	
	/**
	 * @return 是否需要创建
	 */ 
	private boolean isCreate(ArrayList<String> tableNameList) 
	{
		boolean publish = isPublish();
		
		int fileCount = 0;
		
		if(publish)			//生产环境,禁止创建实体类
		{
//			try 
//			{
//				String rootEntityPath = this.entityPackage.replace(".", "/");
//				String allPath = CacheFromData.class.getResource("/"+rootEntityPath+"/").getPath();			//allPath = file:/data/server/doraemon/gameLogic/gameLogic.jar!/game/data/conf/entity/
//				
//				String [] arrayString = allPath.split("!")[0].split(":");
//				String runProjectPath = arrayString[arrayString.length - 1];
//				ZipFile  zipFile = new ZipFile(runProjectPath);			///data/server/doraemon/gameLogic/gameLogic.jar
//				
//				Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
//				
//				for (String name: tableNameList) 
//				{
//					while (enu.hasMoreElements()) 
//					{
//						ZipEntry zipElement = (ZipEntry) enu.nextElement();  
//						
//						String fileName = zipElement.getName();
//						String className = rootEntityPath + name + ".class";
//						
//						if (fileName != null && fileName.indexOf(rootEntityPath) >= 0)			//指定rootMapper目录下
//						{
//							if(fileName.equalsIgnoreCase(className))
//							{
//								System.out.println("fileName = " + fileName + " fileCount = " + fileCount);
//								fileCount++;
//								break;
//							}
//						}
//					}
//				}
//			}
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//			}
			
			return false;
		}
		else
		{
			URL url = CacheFromData.class.getClassLoader().getResource("");
			
			String rootEntityPath = this.entityPackage.replace(".", "/");
			
			File [] files = new File(url.getPath() + "../src/"+rootEntityPath).listFiles();
			
			for (String name: tableNameList) 
			{
				for (File file : files) 
				{
					if(file.getName().equalsIgnoreCase(name+".java"))
					{
						fileCount++;
						break;
					}
				}
			}
		}
		
		if(fileCount == tableNameList.size())
			return false;
		else
			return true;
	}

	/**
	 * @param tableName 请使用TableName中的表名常量
	 * @return 返回对应数据表的map集合对象
	 */
	public <T> ConcurrentHashMap<Long, T> getMapByTable(String tableName)
	{
		return (ConcurrentHashMap<Long, T>)CACHE_TABLE_DATA.get(tableName);
	}
	
	
	
	/**
	 * @param tableName 请使用TableName中的表名常量
	 * @param id
	 * @param clzss
	 * @return 返回对应数据表下的id的java对象
	 */
	public <T> T getObjectById(String tableName , long id )
	{
		ConcurrentHashMap<Long, Object> map = CACHE_TABLE_DATA.get(tableName);
		if(map != null)
			return (T) map.get(id);
		return null;
	}
	
	
	/**
	 * 填充java 配置表对象
	 * @param connection
	 * @param tableNameList
	 */
	private void fillJavaCacheMapObject(Connection connection, ArrayList<String> tableNameList) 
	{
		connection = checkConnection(connection);
		
		int size = tableNameList.size();
		
		for (int i = 0; i < size ; i++) 
		{
			String tableName = tableNameList.get(i);
			
			ConcurrentHashMap<Long, Object> javaMap = getJavaMapObject(connection , tableName);
			if(javaMap != null)
				CACHE_TABLE_DATA.put(tableName, javaMap);
			
//			showProgress(i , size , 5);
		}
		
		System.out.println("Init Cache OK ! ");
	}
	
	
	/**
	 * 检查这个数据库连接
	 * @param connection
	 */
	private Connection checkConnection(Connection connection) 
	{
		try 
		{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select 1");
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			connection = reConnection();
		}
		return connection;
	}





	/**
	 * 显示进度
	 * @param index
	 * @param size
	 */
	private void showProgress(int index ,int size , int showCount)
	{
		int count = size / showCount;
		if(count == 0 || index == 0)
			return;
		
		if(index % count == 0)
		{
			float rate = (float)index / size * 100;
			if(rate != 0)
				System.out.println("Init Cache In At "+rate+"% ");
		}
				
	}
	
	/**
	 * @param connection 数据连接
	 * @param tableName 对应的数据表
	 * @return 返回对应数据表的java对象map集合
	 */
	private ConcurrentHashMap<Long, Object>  getJavaMapObject(Connection connection, String tableName) 
	{
		long id = -1;
		
		Statement stmt = null;
		ResultSet tableResult = null;
		
		String className = null;
		Class classObj = null;
		try 
		{
			ConcurrentHashMap<Long, Object> javaMap  = new ConcurrentHashMap<>();
			
			stmt = connection.createStatement();
			tableResult = stmt.executeQuery("select * from " + tableName);
			
			
			className = tableOrColumnToClassName(tableName);
			classObj =  classNameToClass(tableName , className , tableResult);
			
			if(classObj == null)
				return null;
			
			ArrayList<Method> methodList = getClassMethodList(classObj);
			
			while(tableResult.next())
			{
				Object obj = classObj.newInstance();
				
				int count = tableResult.getMetaData().getColumnCount();
				
				for (int i = 1; i <= count; i++) 
				{
					String column = tableResult.getMetaData().getColumnName(i);
					
					Object value = tableResult.getObject(column);
						
					if(column.equalsIgnoreCase("id"))					//如果存在约定的ID列，则以ID为主键
					{
//						id = Long.valueOf(value.toString());
						String oneColumnString = value.toString();
						if(Util.isNumeric(oneColumnString))
							id = Long.valueOf(oneColumnString);
					}
					else if(i == 1)				//如果不存在ID字段，则使用第一列为主键，且第一列必须为数字字段
					{
						String oneColumnString = value.toString();
						if(Util.isNumeric(oneColumnString))
							id = Long.valueOf(oneColumnString);
					}
					
					Method method = getMethodByField(column,methodList);
					if(method != null && value != null)
					{
						try 
						{
							value = getTypeVlaue(method , value);
							method.invoke(obj, value);
						} 
						catch (Exception e) 
						{
//							value = getTypeVlaue(method , value);			//转换对应的参数类型
							e.printStackTrace();
						}
					}
				}
				
				if(id <= 0)
				{
					Exception e = new Exception("Warning : Table "+ tableName+" at did not find the \"ID\"  field at primary key !");
					LogUtil.error(e);
				}
				
				javaMap.put((long)id, obj);
			}
			
			return javaMap;
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
		finally
		{
			close(stmt, tableResult, connection);
		}
		
		return null;
	}

	private ArrayList<Method> getClassMethodList(Class classObj)
	{
        ArrayList<Method> methodList = new ArrayList<>(10);
          
        for(Class<?> clazz = classObj ; clazz != Object.class ; clazz = clazz.getSuperclass())
        {  
        	Method [] methodArr = clazz.getDeclaredMethods();
        	for (Method method : methodArr) 
        		methodList.add(method);
        }  
          
        return methodList;  
	}

	/**
	 * @param method
	 * @param value
	 * @return 返回对应类型的值 
	 */
	private Object getTypeVlaue(Method method , Object value) 
	{
		String type = method.getParameterTypes()[0].getTypeName();
		if (type.equals("int"))
		{
			if(value instanceof Float)
				value = new Float((float)value).intValue();
			else if (value instanceof Double)
				value = new Double((double)value).intValue();
			else if (value instanceof Long)
				value = new Long((long)value).intValue();
			else if (value instanceof String)
				value = Integer.parseInt(value.toString());
			
			return value;
		}
		else if (type.equals("double"))
			return Double.parseDouble(value.toString());
		else if (type.equals("long"))
			return Long.parseLong(value.toString());
		else if (type.equals("float"))
			return Float.parseFloat(value.toString());
		else if (type.equals("java.lang.String"))
			return value.toString();
		else if (type.equals("boolean"))
			return Boolean.parseBoolean(value.toString());
		return null;
	}


	private Method getMethodByField(String name, ArrayList<Method> methodList) 
	{
		for (Method method : methodList) 
		{
			if(method.getName().equalsIgnoreCase("set"+name))
			{
				method.setAccessible(true);
				return method;
			}
		}
		return null;
	}

	/**
	 * @param connection
	 * @return 返回对应数据库下所有表名列表
	 */
	private ArrayList<String> getTables(Connection connection)
	{
		ArrayList<String> tableNameList = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		boolean exception = false;
		try 
		{
			stmt = connection.createStatement();
			rs = stmt.executeQuery("show tables; ");
			while(rs.next())
			{
				String tableName = rs.getString(1);
				tableNameList.add(tableName);
			}
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
			exception = true;
		}
		finally
		{
			close(stmt, rs, connection);
			if(exception)
				reConnection();
		}
		
		
		return tableNameList;
		
	}
	
	/**
	 * @param className
	 * @param className2 
	 * @param tableResult 
	 * @return 根据class返回一个class对象
	 * @throws Exception 
	 */
	private Class classNameToClass(String tableName, String className, ResultSet tableResult)
	{
		String allClassName = entityPackage + "."+className;
		
		try 
		{
			if(isCreateFile)				//是否对创建实体类
			{
				File file = new File(this.srcEntityPath + className+".java");
				
				if(!file.exists())				//如果不存这个实体类
				{
					boolean isCreateAttributeJavaFile = isCreateAttributeJavaFile(tableName);
					boolean isCreate = false;
					
					if(isCreateAttributeJavaFile)			//如果是创建的静态属性类的java文件
						isCreate = createAttributeJavaFile(className , tableResult);
					else
						isCreate = createEntityJavaFile(className , tableResult);		//创建对象属性的java文件
					
					if(isCreate)				//创建成功
					{
						compilerJava(file.getAbsolutePath());			//动态编译java源文件至class文件 
						
						return Class.forName(allClassName);
					}
					else
						return null;
				}
				else
				{
					return Class.forName(allClassName);
				}
			}
			else
			{
				return Class.forName(allClassName);
			}
		}
		catch (Exception e) 
		{
			if(e instanceof ClassNotFoundException)
				System.out.println("Class Not Found : " + allClassName);
			else
				e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * @param srcTableName
	 * @return 返回是否去创建属性类文件
	 */
	private boolean isCreateAttributeJavaFile(String srcTableName)
	{
		if(attributeTableList == null)
			return false;
		for (String tableName : attributeTableList) 
		{
			if(tableName.equals(srcTableName))
				return true;
		}
		return false;
	}
	
	/**
	 * @param fileName 编译 java 源文件 至class文件 
	 */
	private void compilerJava(String fileName)
	{
		try 
		{
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("javac -d "+ this.binClassPath + " " +fileName);
			
			Thread.sleep(600L);
			
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * @param tableName
	 * @return 数据表名，或数据字段名，转换成java类的字段或类名
	 */
	private String tableOrColumnToClassName(String tableName)
	{
//		StringBuffer classNameSb = new StringBuffer();
//		
//		String [] tName = tableName.split("_");
//		for (int i = 0; i < tName.length; i++) 
//		{
//			String wordStr = tName[i];
//			
//			String first = wordStr.substring(0, 1).toUpperCase();
//			String remain = wordStr.substring(1);
//			classNameSb.append(first).append(remain);
//		}
//		
//		return classNameSb.toString();
		
		return tableName;
		
	}
	
	private boolean isUpperCase(char k)
	{
		for (int i = 0; i < UPPER_CASE.length; i++) 
			if(k == UPPER_CASE[i])
				return true;
		
		return false;
	}
	
	
	/**
	 * @param url
	 * @param user
	 * @param password
	 * @return 返回对应数据库连接
	 */
	private Connection getConnection(String url ,  String user , String password )
	{
		Connection connection = null;
		
		try 
		{
			Class.forName(MYSQL_DRIVER); 	//加载mysq驱动
			connection = DriverManager.getConnection(url+"?autoReconnect=true&useUnicode=true&characterEncoding=utf-8", user, password);
			
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();// 打印出错详细信息
		}
		
		return connection;
	}
	
	private Connection reConnection()
	{
		try
		{
			if(this.connection != null)
			{
				if(!this.connection.isClosed())
					this.connection.close();
			}
		}
		catch (SQLException e)
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
		
		this.connection = getConnection(this.mysqlUrl , this.username , this.password);
		
		return this.connection;
	}
	
	private Connection getConnection(String ip , int port , String dataName , String user , String password )
	{
		Connection connection = null;
		
		try 
		{
			Class.forName("com.mysql.jdbc.Driver"); 	//加载mysq驱动
			String url = "jdbc:mysql://"+ip+":"+port+"/"+dataName+"?user="+user+"&password="+password+"&useUnicode=true&&characterEncoding=utf-8&autoReconnect=true";
			connection = DriverManager.getConnection(url, user, password);
			
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();// 打印出错详细信息
		}
		
		return connection;
	}
	
	
	private void close(Statement stmt , ResultSet rs , Connection connection)
	{
		try 
		{
			if (rs != null) 
			{
				rs.close();
				rs = null;
			}
			if (stmt != null) 
			{
				stmt.close();
				stmt = null;
			}
//			if (connection != null) 
//			{
//				connection.close();
//				connection = null;
//			}
		} 
		catch (Exception e) 
		{
			LogUtil.error(e);
			System.out.println("数据库关闭错误");
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql)
	{
		return executeSql("executeQuery", sql);
	}
	
	public ResultSet execute(String sql)
	{
		return executeSql("execute", sql);
	}

	private <T> T executeSql(String executeType , String sql)
	{
		Statement stmt = null;
		Object retVal = null;
		try 
		{
			stmt = connection.createStatement();
			if(executeType.equals("executeQuery"))
				retVal = stmt.executeQuery(sql);
			else if(executeType.equals("execute"))
				retVal = stmt.execute(sql);
			else if(executeType.equals("executeUpdate"))
				retVal = stmt.executeUpdate(sql);
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
		finally
		{
			if(retVal instanceof ResultSet)
				close(stmt, (ResultSet)retVal , connection);
			else
				close(stmt, null , connection);
		}
		
		return (T)retVal;
		
	}


	public Connection getConnection() {
		return connection;
	}


	public void clear() 
	{
//		CACHE_TABLE_DATA.clear();
	}
	
}
