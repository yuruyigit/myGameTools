package game.tools.utils;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import game.tools.log.LogUtil;


public class Sorts
{
	
	/**
	 * @param list
	 * @param sortFiled 排序字段
	 */
	public static void sort(List list, final String sortFiled)
	{
		sort(list, sortFiled , "asc");
	}
	/**
	 * 
	 * @param list   要排序的list列表
	 * @param method	排序的对象名称  例：getId
	 * @param sort   正序 还是倒序  desc   asc
	 */
	@SuppressWarnings("all")
	public static void sort(List list, final String sortFiled, final String sort)
	{
		if (list != null)
		{
			Collections.sort(list, new Comparator()
			{
				public int compare(Object a, Object b)
				{
					int ret = 0;
					boolean asc = true;
					try
					{
						if(StringTools.empty(sort) || sort.equals("asc"))
							asc = true;
						else
							asc = false;
						
						Field field1 = null , field2 = null;
						
						Class aClzss = a.getClass();
						Class bClzss = b.getClass();
						
						for (int i = 0; i < 100 && aClzss != Object.class; i++) 			//检测父类最大深度为100
						{
							try
							{
								field1 = aClzss.getDeclaredField(sortFiled);
								field2 = bClzss.getDeclaredField(sortFiled);
							}
							catch(NoSuchFieldException e){		}
							
							aClzss = aClzss.getSuperclass();
							bClzss = bClzss.getSuperclass();
						}
					
						field1.setAccessible(true);
						field2.setAccessible(true);
						
						if(field1.getType() == String.class)
						{
							if(asc)
								ret = field2.get(a).toString().compareTo(field1.get(b).toString());
							else
								ret = field2.get(b).toString().compareTo(field1.get(a).toString());
						}
						else if(field1.getType() == int.class || field1.getType() == Integer.class)
						{
							if(asc)
								ret = (int)field2.get(a) > (int)field1.get(b) ? 1 : -1;
							else
								ret = (int)field2.get(a) > (int)field1.get(b) ? -1 : 1;
						}
						else if(field1.getType() == long.class || field1.getType() == Long.class)
						{
							if(asc)
								ret = (long)field2.get(a) > (long)field1.get(b) ?  1:  -1;
							else
								ret = (long)field2.get(a) > (long)field1.get(b) ?  -1:  1;
						}
						else 
						{
							throw new Exception("Not Type Compare ");
						}
						
						
						
//						Method m1 = a.getClass().getMethod(method, null);
//						Method m2 = b.getClass().getMethod(method, null);
//						
//						if(StringTools.empty(sort) || sort.equals("asc"))
//							ret = m2.invoke(a, null).toString().compareTo(m1.invoke(b, null).toString());
//						else
//							ret = m2.invoke(b, null).toString().compareTo(m1.invoke(a, null).toString());
					}
					catch (Exception e)
					{
						LogUtil.error(e);
						e.printStackTrace();
					}
					return ret;
				}
			});
		}
	}
	
	public static void main(String[] args) 
	{
//		List<SysRoleBuildQueue> list =new ArrayList<SysRoleBuildQueue>();
//		
//		SysRoleBuildQueue s1=new SysRoleBuildQueue();
//		s1.setId(1);
//		s1.setState(1);
//		
//		SysRoleBuildQueue s2=new SysRoleBuildQueue();
//		s2.setId(2);
//		s2.setState(2);
//		
//		SysRoleBuildQueue s3=new SysRoleBuildQueue();
//		s3.setId(3);
//		s3.setState(0);
//		
//		SysRoleBuildQueue s4=new SysRoleBuildQueue();
//		s4.setId(4);
//		s4.setState(1);
//		list.add(s1);
//		list.add(s2);
//		list.add(s3);
//		list.add(s4);
//		
//		Sorts.sort(list, "state", "desc");
//		System.out.println(JSONArray.toJSONString(list));
		
//		List<city_item> list =new ArrayList<>();
//		city_item item = new city_item();
//		item.setItem_id(1);
//		city_item item2 = new city_item();
//		item2.setItem_id(2);
//		city_item item3 = new city_item();
//		item3.setItem_id(3);
//		
//		list.add(item3);
//		list.add(item);
//		list.add(item2);
//		
//		System.out.println(JSONArray.toJSONString(list));
//		
//		Sorts.sort(list, "getItem_id", "desc");
//		
//		System.out.println(JSONArray.toJSONString(list));
	}

}
