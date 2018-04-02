package game.tools.db.mybatis.plush.sqlittable;
import java.util.ArrayList;
import java.util.HashMap;
import game.tools.utils.Util;

class Sqls 
{
	
	/**
	 * @param originTableName
	 * @param sql
	 * @param paramArray
	 * @return 返回根据指定参数的sql
	 */
	static String getSelectSql(Tables tables , String originTableName , String sql , Object [] paramArray)
	{
		HashMap<String, SqlCondition> condMap = getSqlCondition(sql);
		if(!condMap.containsKey(SqlKeyword.FROM))
			return null ;
		
		ArrayList<String> nameList = tables.getThisTableNameList(originTableName);
		int size = nameList.size();
		
		ParamIndex paramIndexObj = new ParamIndex();
		
		StringBuffer sb = new StringBuffer(sql.length() * size);
		
		SqlCondition cond = null;
		for (int i = 0; i < size; i++) 
		{
			String tabName = nameList.get(i);
			sb.append(" ");
			
			sb.append(SqlKeyword.SELECT).append(condMap.get(SqlKeyword.SELECT).getValue());
			sb.append(" ").append(SqlKeyword.FROM).append(" ").append(tabName);
			
			String value = null;
			 
			cond = condMap.get(SqlKeyword.WHERE);
			if(cond != null)
			{
				value = getParamsValue( i , cond , paramIndexObj , paramArray);
				
				sb.append(" ").append(SqlKeyword.WHERE).append(" ").append(value);
			}
			
			if(i < size - 1)
			{
				cond = condMap.get(SqlKeyword.LIMIT);
				if(cond != null)
				{
					value = getParamsValue( i , cond , paramIndexObj , paramArray);
					sb.append(" ").append(SqlKeyword.LIMIT).append(" ").append(value);
				}
				
				sb.append(" ").append(SqlKeyword.UNION_ALL);
			}
			else
			{
				cond = condMap.get(SqlKeyword.ORDER_BY);
				if(cond != null)
				{
					value = getParamsValue( i , cond , paramIndexObj , paramArray);
					sb.append(" ").append(SqlKeyword.ORDER_BY).append(" ").append(value);
				}
				
				cond = condMap.get(SqlKeyword.LIMIT);
				if(cond != null)
				{
					value = getParamsValue( i , cond , paramIndexObj , paramArray);
					sb.append(" ").append(SqlKeyword.LIMIT).append(" ").append(value);
				}
			}
			
			paramIndexObj.clear();
		}
		
		return sb.toString();
	}
	
	/**
	 * @param tableIndex
	 * @param cond
	 * @param paramIndexObj
	 * @param paramArray
	 * @return 返回传入设置好的参数语句
	 */
	private static String getParamsValue(int tableIndex ,SqlCondition cond, ParamIndex paramIndexObj,  Object[] paramArray) 
	{
		String value = cond.getValue(); 
		if(tableIndex == 0)
			return value;
		
		int paramIndex = paramIndexObj.getParamIndex();
		
		while(value.indexOf("?") >= 0 && paramIndex < paramArray.length)
		{
			Object paramObj = paramArray[paramIndex++];
			if(paramObj instanceof String)
				paramObj = "'" + paramObj + "'";
			
			value = value.replaceFirst("\\?", paramObj.toString());
		}
		
		paramIndexObj.setParamIndex(paramIndex);
			
		return value;
	}
	
	
	/**
	 * @param originTableName
	 * @param sql
	 * @return 返回分表查询的sql语句
	 */
	static String getSelectSql(Tables tables ,String originTableName , String sql)
	{
		HashMap<String, SqlCondition> condMap = getSqlCondition(sql);
		if(!condMap.containsKey(SqlKeyword.FROM))
			return null ;
		
		ArrayList<String> nameList = tables.getThisTableNameList(originTableName);
		int size = nameList.size();
		StringBuffer sb = new StringBuffer(sql.length() * size);
		
		SqlCondition whereCond = null;
		for (int i = 0; i < size; i++) 
		{
			String tabName = nameList.get(i);
			sb.append(" ");
			
			sb.append(SqlKeyword.SELECT).append(condMap.get(SqlKeyword.SELECT).getValue());
			sb.append(" ").append(SqlKeyword.FROM).append(" ").append(tabName);
			
			whereCond = condMap.get(SqlKeyword.WHERE);
			if(whereCond != null)
				sb.append(" ").append(SqlKeyword.WHERE).append(" ").append(whereCond.getValue());
			
			if(i < size - 1)
			{
				whereCond = condMap.get(SqlKeyword.LIMIT);
				if(whereCond != null)
					sb.append(" ").append(SqlKeyword.LIMIT).append(" ").append(whereCond.getValue());
				
				sb.append(" ").append(SqlKeyword.UNION_ALL);
			}
			else
			{
				whereCond = condMap.get(SqlKeyword.ORDER_BY);
				if(whereCond != null)
					sb.append(" ").append(SqlKeyword.ORDER_BY).append(" ").append(whereCond.getValue());
				
				whereCond = condMap.get(SqlKeyword.LIMIT);
				if(whereCond != null)
					sb.append(" ").append(SqlKeyword.LIMIT).append(" ").append(whereCond.getValue());
			}
		}
		
		return sb.toString();
	}
	
	
	/**
	 * @param sql
	 * @return 返回关键字条件的值map
	 */
	private static HashMap<String, SqlCondition> getSqlCondition(String sql)
	{
		if(Util.stringIsEmpty(sql))
			return null;
		
		HashMap<String ,SqlCondition> map = new HashMap<>();

		String scanStr = "" , oldKeyword = null ;
		int gap = 10;
		
		int sqlLength = sql.length();
		for (int i = 0 ; i < sqlLength; i++) 
		{
			scanStr += sql.charAt(i);
			
			if( i < sqlLength - 1)
			{
				if(i == 0 || i % gap != 0 && i < sqlLength)
					continue;
			}
			
			Object [] arr = SqlKeyword.getKeyword(scanStr);
			
			if(arr != null)
			{
				String keyword = (String)arr[0];
				int index = (int)arr[1];
				
				if(oldKeyword == null || !oldKeyword.trim().equals(keyword.trim()))
				{
					if(oldKeyword != null)
						oldKeyword = oldKeyword.trim();
					
					if(map.containsKey(oldKeyword))
					{
						SqlCondition o = map.get(oldKeyword);
						o.setValue(scanStr.substring(0 ,index));
					}
					
					SqlCondition o = new SqlCondition(keyword);
					map.put(keyword.trim(), o);
					
					oldKeyword = keyword;
					
					if(index == 0)
						scanStr = scanStr.substring(keyword.length());
					else
						scanStr = scanStr.substring(index + keyword.length());
				}
				
			}
			
			if( i == sqlLength - 1)			//如果到最后了
			{
				if(oldKeyword != null)
					oldKeyword = oldKeyword.trim();
				
				if(map.containsKey(oldKeyword))
				{
					SqlCondition o = map.get(oldKeyword);
					String value = scanStr;
					o.setValue(value);
				}
			}
		}
		
//		System.out.println(JSONObject.toJSONString(map.values()));
		return map;
	}
	
}
