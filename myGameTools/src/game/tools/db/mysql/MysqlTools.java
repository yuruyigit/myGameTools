package game.tools.db.mysql;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class MysqlTools
{
	private TomcatJdbc tomcatJdbc; 
	private static final String MYSQL_URL_CONF = "?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8";
	/*** 可以转译的字符	 */
	private static final String DEC_FIELD="{field}";
	
	public MysqlTools(Properties p)
	{
		tomcatJdbc = new TomcatJdbc(p);
	}
	
	public MysqlTools(String db_url , String db_user , String db_pwd)
	{
		tomcatJdbc = new TomcatJdbc(db_url + MYSQL_URL_CONF , db_user , db_pwd);
	}
	
	
	public <T> Class<T> insert(String sql , Object o)
	{
		sql = getSqlStr(sql , o.getClass());
		
		Connection conn = null;
		Statement st = null;
		try
		{
			conn = tomcatJdbc.getConnection();
			st = conn.createStatement();
			st.execute(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				st.close();
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * @param sql  {field} 可使用对应的类中的属性字段当可查询的列标题 <br/>
	 * @param clzss 传入映射的类对象<br/>
	 * @return 返回对应的查询列表
	 */
	public <T> ArrayList<T> query(String sql , Class<T> clzss)
	{
		sql = getSqlStr(sql , clzss);
		Connection conn = null;
		Statement st = null;
		ResultSet result = null;
		try
		{
			conn = tomcatJdbc.getConnection();
			st = conn.createStatement();
			result = st.executeQuery(sql);
			if(result != null)
				return getArryLitByResultSet(result , clzss);
			else 
				return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				result.close();
				st.close();
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * @param sql 待转译解析的sql字符串
	 * @param clzss 要转译解析使用的字段类对象
	 * @return 返回一个可以使用的完整sql语句 
	 */
	private String getSqlStr(String sql, Class clzss)
	{
		String result = null;
		
		if(sql.indexOf(DEC_FIELD) >= 0)
		{
			StringBuffer newStr = new StringBuffer();
			Field[] fields = clzss.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				if(i < fields.length - 1)
					newStr.append("`").append(fields[i].getName()).append("`,");
				else
					newStr.append("`").append(fields[i].getName()).append("`");
			}
			
			result = sql.replace(DEC_FIELD, newStr.toString());
		}
		return result;
	}

	
	public static void main(String[] args)
	{
//		String db_url = "jdbc:mysql://192.168.1.131:3306/jjdtk_login";
//		String db_user = "root";
//		String db_pwd = "root";
//		
//		MysqlTools mysqlTools = new MysqlTools(db_url, db_user, db_pwd);
//		long startTime = System.currentTimeMillis();
//		ArrayList<SysUser> list = mysqlTools.query("select {field} from sys_user where id = 1", SysUser.class);
//		long endTime = System.currentTimeMillis();
//		System.out.println(list.size() + " gap =  " + (endTime - startTime));
		
//		System.out.println(Integer.parseInt("2582180926"));
	}
	
	private <T> ArrayList<T> getArryLitByResultSet(ResultSet result ,Class<T> clzss ) throws Exception
	{
		ArrayList<T> list = null ;
		result.last();
		if(result.getRow() <= 0)
			return list;
		
		result.first();
		list = new ArrayList<T>(result.getRow());
        Field[] fields = clzss.getDeclaredFields();
        
        while (result.next()) 
        {
            T object = clzss.newInstance();
            for (int i = 0; i < fields.length; i++) 
            {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = result.getObject(field.getName());
                field.set(object, value);
            }
            list.add(object);
        }
        return list;
	}
}
