package game.tools.xls;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import game.tools.utils.StringTools;

public class XlsToSql 
{
	private XlsToSql() {	}
	
	private static String xlsFolder = null;
	private static String saveFolder = null;
	private static boolean print = false;
	
	/**
	 * java -jar XlsToSql.jar d:/xls
	 * 
	 * Excel约定规范是：
	 * 第一行为字段注释描述
	 * 第二行为字段类型
	 * 第三行为字符名称 
	 * 第四行为数据行开始，在此行，在读取数据，如下：
	 * 		数字为int(11) [PRIMARY KEY]为主键
	 * 		字 符串为 varchar(255) 
	 * 
	 * |唯一标识				|城镇资源ID		|名称			|等级		|阶段		|消耗的物品		|英雄ID
	 * |int(11) PRIMARY KEY	|varchar(255)	|varchar(255)	|int(11)	|int(11)	|varchar(255)	|int(11)
	 * |id					|city_res		|name			|level		|stage		|item			|hero_id
	 * |1					|	1			|1				|			|			|1003,50|		|
	 * |2					|	1			|1				|			|1			|003,50|		|
	 *
	 *
	 */
	public static void main(String[] args) throws Exception
	{
		initPath(args);
		
		xlsTosql();
	}
	
	private static void initPath(String[] args) throws Exception
	{
		if(args == null || args.length == 0)
			throw new Exception("args is empty !!!");
		
		xlsFolder = args[0];
		if(args.length == 2)
			print = Boolean.parseBoolean(args[1]);
		
		saveFolder = xlsFolder + "/all_in_one.sql";
		
		if(StringTools.empty(xlsFolder) || StringTools.empty(saveFolder))
			throw new Exception("xlsFolder or saveFile is empty !!!");
		
		
		System.out.println("xlsFolder = " + xlsFolder);
		System.out.println("saveFolder = " + saveFolder);
		
		
		System.out.printf("\n"+
		"======================================================================================================\n"+
		"O       O    O         OOOO          OOOOOOOOO    OOO              OOOO          O O O         O           \n"+
		" O     O     O        O    OO            O      O      O          O    OO       O      O       O           \n"+
		"  O   O      O       O       OO          O      O      O         O       OO    O        O      O           \n"+
		"   O O       O        OO                 O      O      O          OO          O          O     O           \n"+
		"    O        O           OO              O      O      O             OO       O          O     O           \n"+
		"   O  O      O             OO            O      O      O               OO      O      O  O     O           \n"+
		"  O    O     O                O          O      O      O                  O     O       O      O           \n"+
		" O      O    O       O     OO            O      O      O         O     OO        O     O O     O           \n"+
		"O        O   OOOOOO   OOOOO              O        OOO             OOOOO           O O O    O   OOOOOO       \n"+
		"======================================================================================================\n"+
		"    \n"+                                                                                  
		"* Excel约定规范是：\r\n" + 
		"* 第一行为字段注释描述\r\n" + 
		"* 第二行为字段类型\r\n" + 
		"* 第三行为字符名称 \r\n" + 
		"* 第四行为数据行开始，在此行，在读取数据，\n"+
		"* 示例如下：\r\n" + 
		"* 	数字为int(11) [PRIMARY KEY] ，为主键。注：符合“ID”列名，则默认为主键。\r\n" + 
		"* 	字 符串为 varchar(255) \r\n" + 
		"* \r\n" + 
		"* |唯一标识            |城镇资源ID     |名称           |等级       |阶段       |消耗的物品     |英雄ID\r\n" + 
		"* |int(11) PRIMARY KEY |varchar(255)   |varchar(255)   |int(11)    |int(11)    |varchar(255)   |int(11)\r\n" + 
		"* |id                  |city_res       |name           |level      |stage      |item           |hero_id\r\n" + 
		"* |1                   |   1           |1              |           |           |1003,50|       |       \r\n" + 
		"* |2                   |   1           |1              |           |1          |003,50|        |       \r\n\n"+
		"==================================================================================================\n\n");
		
	}

	private static void xlsTosql() 
	{
		File [] fileArray = getXlsFile();
		
		String sqlString = "";
		for (File file : fileArray) 
		{
			sqlString += generateSql(file);
		}
		
		saveSql(sqlString);
		
		
	}

	private static void saveSql(String sqlString) 
	{
		try
		{
//			FileWriter fw = new FileWriter(new File(saveFolder));
			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(saveFolder),"UTF-8"); 
			fw.write(sqlString);
			fw.flush();
			fw.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		

		System.out.println("generateSql ok by : " + saveFolder);
	}

	private static String generateSql(File file) 
	{
		Xls xls = new Xls(file.getAbsolutePath());
		
		String fileName = file.getName();
		
		System.out.println("generateSql by " + fileName);
		
		String tableName = fileName.substring(0,fileName.indexOf("."));
		
		StringBuffer sqlStringBuffer = new StringBuffer();
		
		sqlStringBuffer.append("\nDROP TABLE IF EXISTS ").append(tableName).append(";").append("\n");
		sqlStringBuffer.append("CREATE TABLE ").append(tableName).append(" ( \n");
	
		
		int rowCount = xls.getRowCount();
		int columnCount = xls.getColumnCount();
		
		int rowCursor = 0;
		
		for (int i = 0; i <= rowCount; i++) 
		{
			if(i == 0)			//获取创建表的注解与字段名
			{
				StringBuffer columnNameString = new StringBuffer();
				
				for (int j = 0; j < columnCount; j++) 
				{
					rowCursor = i;
					
					String columnDesc = xls.getCell(rowCursor, j);
					String columnType = xls.getCell(++rowCursor, j);
					String columnName = xls.getCell(++rowCursor, j);
					
					if(columnName.equalsIgnoreCase("id"))			//如果是ID这一列,检测是否为主键，如果不过主键，则添加主键
					{
						String upperColumnType = columnType.toUpperCase();
						if(upperColumnType.indexOf("PRIMARY KEY") < 0)		//如果ID这列没人主键，则默认去设置ID为主键		
							columnType += " PRIMARY KEY";
					}
					
					if(j == columnCount - 1)		//如果是最后一列
						sqlStringBuffer.append("`").append(columnName).append("` ").append(columnType).append(" COMMENT '").append(columnDesc).append("' \n");
					else
						sqlStringBuffer.append("`").append(columnName).append("` ").append(columnType).append(" COMMENT '").append(columnDesc).append("', \n");
					
					if(j < columnCount - 1)
						columnNameString.append("`").append(columnName).append("`").append(",");
					else
						columnNameString.append("`").append(columnName).append("`");
				}

				sqlStringBuffer.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n\n");
				
				sqlStringBuffer.append("INSERT INTO ").append(tableName).append(" (").append(columnNameString).append(") VALUES \n");
				
				i = 2;		//标记到数据行开始
			}
			else			//开始提取数值内容
			{
				for (int j = 0; j < columnCount; j++) 
				{
					String value = xls.getCell(i, j);
					String type = xls.getCell(1, j);
					
					if(j == 0)
						sqlStringBuffer.append("(").append(getTypeValue(type , value )).append(",");
					else
					{
						if(j < columnCount - 1)
							sqlStringBuffer.append(getTypeValue(type , value )).append(",");
						else
							sqlStringBuffer.append(getTypeValue(type , value )).append("),\n");
					}
				}
			}
		}
		
		String sqlString = null;
		
		if(sqlStringBuffer.length() > 0)
			sqlString = sqlStringBuffer.substring(0, sqlStringBuffer.length() - 2) + ";\n";
		
		if(print)
			System.out.println(sqlString);
		
		return sqlString;
	}
	
	private static String getTypeValue(String type , String value)
	{
		if(value == null)
			return	"Null";
		
		if(type.indexOf("int") >= 0)
			return value;
		else if(type.indexOf("varchar") >= 0)
			return "'"+value+"'";
			
		return	"Null";
	}

	private static File[] getXlsFile() 
	{
		File rootFile = new File(XlsToSql.xlsFolder);
		File [] xlsFileArray = rootFile.listFiles(new FileFilter() 
		{
			@Override
			public boolean accept(File pathname) 
			{
				String name = pathname.getName();
				if(name.lastIndexOf(".xlsx") >= 0)
					return true;
				return false;
			}
		});
		return xlsFileArray;
	}
}
