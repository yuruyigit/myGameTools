package game.tools.db.diff;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author zhouzhibin
 * mysql 数据库中所有表差异
 */
public class MysqlDatabaseTableDiff 
{
	/**
	 * @param server1  localhost:3306:root:root:database 格式:ip地址:端口:用户名:密码:数据库
	 * @param server2	localhost:3306:root:root:database 格式:ip地址:端口:用户名:密码:数据库
	 * @param writerPath	生成的sql保存路径
	 */
	public void doDiff(String server1, String server2 , String writerPath)
	{
		String []serverArray = server1.split(":");
				
		Connection connection1 = getConnection(serverArray[0], serverArray[1], serverArray[2] , serverArray[3] , serverArray[4]);
		
		try {
			connection1.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		serverArray = server2.split(":");
		Connection connection2 = getConnection(serverArray[0], serverArray[1], serverArray[2] , serverArray[3] , serverArray[4]);
		

		HashMap<String, String> tableDllMap1 = getTableDllsMap(connection1);
		HashMap<String, String> tableDllMap2 = getTableDllsMap(connection2);
		
		ArrayList<String> diffList = diffDll(tableDllMap1 , tableDllMap2);
		
		writeFile(diffList , writerPath);
		
		System.out.println();
	}
	
	private void writeFile(ArrayList<String> diffList, String writerPath) 
	{
		String filePath = writerPath + "struct_diff.sql";
		try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
			
			for (String string : diffList) 
				writer.write(string + "\n");
			
			writer.flush();
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}  //文件写入流
		
		System.out.println("general struct_diff.sql by : " + filePath);
	}

	private ArrayList<String> diffDll(HashMap<String, String> tableDllMap1, HashMap<String, String> tableDllMap2) 
	{
		Iterator<String> tableNameIter = tableDllMap1.keySet().iterator();
		
		ArrayList<String> diffList = new ArrayList<>(10);
		
		diffList.add("SET FOREIGN_KEY_CHECKS=0;");
		
		while(tableNameIter.hasNext())
		{
			String tableName = tableNameIter.next();
			
			String ddlString1 = tableDllMap1.get(tableName);
			String ddlString2 = tableDllMap2.get(tableName);
			
			if(ddlString2 == null)
			{
				ddlString1 = ddlString1.replaceAll("\n", " ");
				diffList.add(ddlString1 + " ;");
			}
			else
			{
				String [] dllArray1 = ddlString1.split("\n");
				String [] dllArray2 = ddlString2.split("\n");
				
				boolean exist = false;
				//正向A->B比较，添加字段。
				for (String ddlItem1 : dllArray1) 
				{
					exist = false;
					
					for (String ddlItem2 : dllArray2) 
					{
						if(ddlItem1.equals(ddlItem2))
						{
							exist = true;
							break;
						}
							
					}
					if(!exist)
					{
						ddlItem1 = ddlItem1.trim();
						
						if(ddlItem1.lastIndexOf(",") > 0)
							ddlItem1 = ddlItem1.substring(0 , ddlItem1.length() - 1);
						
						if(ddlItem1.indexOf("`") == 0)
							diffList.add("ALTER TABLE `" + tableName + "` ADD COLUMN" + ddlItem1 + " ;");
					}
				}
				
				
				//反向B->A比较，删除字段。
				for (String ddlItem2 : dllArray2) 
				{
					exist = false;
					
					for (String ddlItem1 : dllArray1)  
					{
						if(ddlItem1.equals(ddlItem2))
						{
							exist = true;
							break;
						}
					}
					
					if(!exist)
					{
						ddlItem2 = ddlItem2.trim();
						
						if(ddlItem2.indexOf("`") == 0)
						{
							String [] array = ddlItem2.split("`");
							
							if(array.length >= 2)
								diffList.add("ALTER TABLE `" + tableName + "` DROP COLUMN " + array[1] + " ;");
						}
							
					}
				}
			}
		}
		diffList.add("SET FOREIGN_KEY_CHECKS=1;");
		
//		for (String string : diffList) 
//		{
//			System.out.println(string);
//		}
//		System.out.println(JSONObject.toJSONString(diffList , true));
		
		return diffList;
	}

	private Connection getConnection(String ip , String port ,  String user , String password , String database)
	{
		Connection connection = null;
		
		try 
		{
			Class.forName("com.mysql.jdbc.Driver"); 	//加载mysq驱动
			
			connection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+database+"?autoReconnect=true&useUnicode=true&characterEncoding=utf-8", user, password);
			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();// 打印出错详细信息
		}
		
		return connection;
	}
	
	
	/**
	 * @param connection
	 * @return 返回对应数据库下所有表名列表
	 */
	private HashMap<String, String> getTableDllsMap(Connection connection)
	{
		ArrayList<String> tableNameList = new ArrayList<>();
		
		HashMap<String, String> tableDllMap = new HashMap<>();
		
		Statement stmt = null;
		
		ResultSet rs = null;
		
		String tableName = null , ddlString = null;
		
		try 
		{
			stmt = connection.createStatement();
			
			rs = stmt.executeQuery("show tables; ");
			
			while(rs.next())
			{
				tableName = rs.getString(1);
				
				tableNameList.add(tableName);
			}
			
			for (String name : tableNameList) 
			{
				rs = stmt.executeQuery("show create table "+ name);
				
				while(rs.next())
				{
					ddlString = rs.getString(2);
					
					tableDllMap.put(name, ddlString);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			close(stmt, rs, connection);
//			if(exception)
//				reConnection();
		}
		return tableDllMap;
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
			System.out.println("数据库关闭错误");
			e.printStackTrace();
		}
	}
	
	private static void printDemo()
	{
		System.out.println("java -jar (server1)ip地址:端口:用户名:密码:数据库  (server2)ip地址:端口:用户名:密码:数据库  生成的sql目录地址\n"+
				"以server1为准进行比较差异\n"+
				"例如：java -jar mysqlStructsync.jar 192.168.56.31:3306:root:123456:game_dev 192.168.56.31:3306:root:123456:game_test d:");
		
	}
	
	public static void main(String[] args) 
	{
		MysqlDatabaseTableDiff diff = new MysqlDatabaseTableDiff();
		if(args == null || args.length == 0)
		{
			printDemo();
			System.out.println("Rec Param Is Empty");
			System.exit(-1);
		}
		
		String server1 = args[0];
		String server2 = args[1];
		String writerPath = args[2];
		
		if(server1 == null || "".equals(server1))
		{
			System.out.println("Rec Param server1 Is Empty");
			printDemo();
			System.exit(-1);
		}
		if(server2 == null || "".equals(server2))
		{
			System.out.println("Rec Param server2 Is Empty");
			printDemo();
			System.exit(-1);
		}
		if(writerPath == null || "".equals(writerPath))
		{
			System.out.println("Rec Param writerPathIs Empty");
			printDemo();
			System.exit(-1);
		}
			
//		String server1 = "192.168.56.31:3306:root:123456:game_dev";
//		String server2 = "192.168.56.31:3306:root:123456:game_test";
//		String writerPath = "d:/";
		 
		diff.doDiff(server1 , server2 , writerPath);
	}
}
