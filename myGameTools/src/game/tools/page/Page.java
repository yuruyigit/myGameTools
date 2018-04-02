package game.tools.page;
import java.util.List;
import java.util.Map;

public class Page 
{
	public static final String PAGE_NO = "Page-pageNo";
	public static final String PAGE_NO_RESULT = "Page-pageNoResult";
	
	/**
	 * @param session	 传入的session
	 * @param pageNo 要查询的页码(注意，该页码从1开始）
	 * @param pageSize 页大小
	 * @param pageWork 当前页内容的数据来源
	 * @return
	 */
	public static Object selectByPageNo(Map<String, Object> session, int pageNo, int pageSize , PageWork pageWork)
	{
		if(pageNo >= 1)
			pageNo --;
		
		session.put(PAGE_NO, pageNo);
		session.put(PAGE_NO_RESULT, null);
		
		return selectByPageNo(session, true, pageSize, pageWork);
	}
	
	/**
	 * @param session 传入的session
	 * @param next 是否是下一页
	 * @param pageSize 页大小
	 * @param pageWork 当前页内容的数据来源
	 * @return
	 */
	public static Object selectByPageNo(Map<String, Object> session, boolean next , int pageSize , PageWork pageWork)
	{
		Object pageNoObject = null;
		
		if(session != null)
			pageNoObject = session.get(PAGE_NO);
		
		//获取当前页码
		int pageNo = 0 ;
		if(pageNoObject != null)
		{
			if(pageNoObject instanceof String)
				pageNo = Integer.parseInt(pageNoObject.toString());
			else
				pageNo = (int)pageNoObject;
		}
		
		//计算上一页，下一页
		int beforePageNo = pageNo;
		if(next)			
		{
			if(session != null)
			{
				List list = (List)session.get(PAGE_NO_RESULT);
				
				if(list != null)
				{
					if(list.size() >= pageSize)
						pageNo ++;
				}
				else
					pageNo ++;
			}
			else
				pageNo ++;
		}
		else
		{
			if(pageNo > 1)
				pageNo --;
		}
		
		
		if(beforePageNo == pageNo)		//如果页面不变,则不进行数据查询
			return null;
			
		List<Object> retList = (List)pageWork.selectPageNo((pageNo - 1) * pageSize, pageSize);
		
		if(session != null)
		{
			session.put(PAGE_NO, pageNo);
			session.put(PAGE_NO_RESULT, retList);
		}
		
		return retList;
	}
	
	public static void clear(Map<String, Object> session)
	{
		session.put(PAGE_NO, 0);
		session.put(PAGE_NO_RESULT, null);
	}
}
