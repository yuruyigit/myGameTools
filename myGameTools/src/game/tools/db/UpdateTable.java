package game.tools.db;
import game.tools.utils.Symbol;
import game.tools.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

public class UpdateTable
{
	private static final int INIT_SIZE = 20;
	
	private String tableName;
	
	private HashMap<String, ArrayList<Object[]>> calMap = new HashMap<String,ArrayList<Object[]>>(INIT_SIZE);
	
	/**
	 * 批量更新工具
	 * @param tableName 传入要批量更新的表名
	 */
	public UpdateTable(String tableName)
	{
		this.tableName = tableName;
	}
	
	/**
	 * @param callName 要更新的列名
	 * @param id 要更新数据行id
	 * @param value 要更新数据列的值
	 */
	public void add(String callName , long id , Object value)
	{
		ArrayList<Object []> list = calMap.get(callName);
		if(list == null)
		{
			list = new ArrayList<Object []>(INIT_SIZE);
			calMap.put(callName, list);
		}
		list.add(new Object [] {id, value});
	}

	public void clear() 
	{
		calMap.clear();
	}
	
	public String getTableName()	{		return this.tableName;	}
	private HashMap<String, ArrayList<Object[]>> getCalMap()	{		return calMap;	}
	
	
	/**
	 * 	批量更新数据示例
	 * 	update sys_role_accept_task 
		set factor_Count = case id  
		when 764 then 0 
		when 761 then 0 
		when 760 then 0 end , 
		status = case id  
		when 764 then 1 
		when 759 then 1 
		end where  id in (764,759,762,766,767,765,761,760 )
		update prf_ai_data 
		set id = case `name`
		when '吃葡萄' then 11
		when '不吐葡萄皮' then 22
		end where `name` in ('吃葡萄','不吐葡萄皮')
	 * @param ut
	 * @return
	 */
	private static String getUpdatesSqlStr(UpdateTable ut) 
	{
		StringBuffer whereStr = new StringBuffer(" where  id in (");
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("update ").append(ut.getTableName()).append(" set ");

		int size = ut.getCalMap().size(), p = 0 , count = 0 , n = 0 ;
		Object id = null, val = null;
		
		Set<String> setList = ut.getCalMap().keySet();
		
		for (String string : setList) 
		{
			strBuf.append(string).append(" = case id ");
			ArrayList<Object[]> ucList = ut.getCalMap().get(string);
			for (Object [] array : ucList) 
			{
				id = array[0];
				val = array[1];
				
				if(count == 0)
				{
					if(n < ucList.size() -1)
						whereStr.append(id).append(",");
					else
						whereStr.append(id);
					n ++ ;
				}
				
				strBuf.append(" when ").append(id).append(" then ");
				if(val != null)
				{
					if(isNumeric(val.toString()))
						strBuf.append(val);
					else if(val.toString().equals(Symbol.EMPTY))
						strBuf.append("''");
					else
						strBuf.append("'").append(val).append("'");
				}
				else
					strBuf.append(val);
			}
			p++; count ++;
			if (p < size)
				strBuf.append(" end , ");
			else
				strBuf.append(" end");
		}
		
		whereStr.append(" )");
		strBuf.append(whereStr);
		
		return strBuf.toString();
	}
	
	
	
	/**
	 * 是否是数字
	 * zzb 2015年4月15日上午10:15:06 
	 * @param str
	 * @return 
	 */
	private static boolean isNumeric(String str)
	{ 
		return Util.isNumeric(str);
	} 
	
	
	public String toSqlString()
	{
		return getUpdatesSqlStr(this);
	}
	
	public static void main(String[] args) 
	{

//		UpdateTable ut = new UpdateTable("sys_role_item");
//		ArrayList<UpdateCall> count = new ArrayList<UpdateCall>();
//		ArrayList<UpdateCall> args = new ArrayList<UpdateCall>();
//
//		for (RoleItemControl ric : roleItemMap.values()) {
//			count.add(new UpdateTable.UpdateCall(ric.getId(), ric.getCount()));
//			args.add(new UpdateTable.UpdateCall(ric.getId(), ric.getArgs()));
//		}
//		ut.add("count", count);
//		ut.add("args", args);
//		
//		String sql = ut.toSqlString();
//		Tools.executeSqlStr(sql);
		
//		UpdateTable table = new UpdateTable("sys_role_build_queue");
//		for (SysRoleBuildQueue queue : roleAllProductQueueList) 
//		{
//			table.add("city_build_id",  queue.getId() , queue.getCityBuildId());
//			table.add("product_formula_id", queue.getId(), queue.getProductFormulaId());
//			table.add("product_count", queue.getId(), queue.getProductCount());
//			table.add("start_time", queue.getId(), queue.getStartTime());
//			table.add("end_time", queue.getId(), queue.getEndTime());
//			table.add("state", queue.getId(), queue.getState());
//		}
	}


}
