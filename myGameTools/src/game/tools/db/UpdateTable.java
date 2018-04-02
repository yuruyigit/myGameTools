package game.tools.db;
import game.tools.utils.Symbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

public class UpdateTable
{
	private String tableName;
	
	private HashMap<String, ArrayList<UpdateCall>> calMap = new HashMap<String,ArrayList<UpdateCall>>();
	
	/**
	 * 批量更新工具
	 * @param tableName 传入要批量更新的表名
	 */
	public UpdateTable(String tableName)
	{
		this.tableName = tableName;
	}
	
	public void add(String callName , ArrayList<UpdateCall> list)
	{
		calMap.put(callName, list);
	}
	
	
	/**
	 * @param callName 要更新的列名
	 * @param id 要更新数据行id
	 * @param value 要更新数据列的值
	 */
	public void add(String callName , long id , Object value)
	{
		ArrayList<UpdateCall> list = calMap.get(callName);
		if(list == null)
		{
			list = new ArrayList<UpdateCall>();
			calMap.put(callName, list);
		}
		list.add(new UpdateCall(id, value));
	}
	
	public static class UpdateCall
	{
		public UpdateCall(long id , Object val) 
		{
			this.id = id;
			this.val = val;
		}
		
		private long id ;
		private Object val;
		
		public long getId() {
			return id;
		}
		public Object getVal() {
			return val;
		}
		
		
	}

	public String getTableName() 
	{
		return this.tableName;
	}


	public HashMap<String, ArrayList<UpdateCall>> getCalMap() {
		return calMap;
	}
	
	
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
		Set<String> setList = ut.getCalMap().keySet();
		
		for (String string : setList) 
		{
			strBuf.append(string).append(" = case id ");
			ArrayList<UpdateCall> ucList = ut.getCalMap().get(string);
			for (UpdateCall uc : ucList) 
			{
				if(count == 0)
				{
					if(n < ucList.size() -1)
						whereStr.append(uc.getId()).append(",");
					else
						whereStr.append(uc.getId());
					n ++ ;
				}
				
				strBuf.append(" when ").append(uc.getId()).append(" then ");
				Object val = uc.getVal(); 
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
		if(str.equals(Symbol.EMPTY))
			return false;
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
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
