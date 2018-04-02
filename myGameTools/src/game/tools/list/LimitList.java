package game.tools.list;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.alibaba.fastjson.JSONObject;

public class LimitList<E> implements List<E> , java.io.Serializable
{
	/** 2017年5月4日 下午4:43:38			 */
	private static final long serialVersionUID = 1L;

	/** 
	 * 限制长度
     */  
    private int limit;    
       
    private int index;
    
    private Object [] element ;
    
//    public LimitList() 
//    {
//    	this(10);
//    }

	public LimitList(int size) 
	{
		this.limit = size;
		element = new Object[limit];
	}
	
	public boolean add(List<E> c) 
	{
		for (E e : c) 
			add(e);
		return false;	
	}
	
	public boolean add(E e)
	{ 
		 if(index >= limit)				            //如果超出长度,添加时,移除第一个
			 reform(0);
		 
		 element[index ++] = e;
		 
		 return true;
	}
	
	private void reform(int cursor)
	{
		int next = 0;
		for (int i = cursor ; i < index; i++)
		{
			next = i + 1;
			if(next < index)
				element[i] = element[next];
			else
				element[index - 1] = null;
		}
		index --;
	}
	
	public E get(int index)
	{
		if(index > this.index || index > limit || index >= element.length)
			return null;
		
		return (E)element[index];
	}
	
	public int limit() 
	{
		return limit;
	}
	
	public int size()
	{
		return index;
	}

	public Object[] getArray() 
	{
		return element;	
	}

	public boolean isEmpty() 
	{
		if(index == 0)
			return true;
		return false;
	}
	
	@Override
	public String toString() 
	{
		return Arrays.toString(element);
	}

	@Override
	public Object[] toArray() 
	{
		Object [] elementClone = new Object [index];
		
		for (int i = 0; i < index; i++) 
			elementClone[i] = element[i];
		
		return elementClone;	
	}

	
	private class Itr implements Iterator<E>
	{
		private int cursor;
		@Override
		public boolean hasNext() 
		{
			if(cursor < index)
				return true;
			return false;
		}

		@Override
		public E next() 
		{
			return (E)element[cursor ++];
		}
		
		@Override
		public void remove() 
		{
			reform(cursor - 1);
		}
	}
	
	@Override
	public Iterator<E> iterator() 
	{
		return new Itr();
	}
	
	@Override
	public void clear() 
	{
		for (int i = 0; i < element.length; i++) 
			element[i] = null;
		
		index = 0;
	}

	@Override
	public E set(int index, E element) 
	{		
		this.element[index] = element;
		
		return (E)this.element[index];	
	}
	
	@Override
	public void add(int index, E element)
	{
		this.element[index] = element;
	}

	@Override
	public E remove(int index)
	{		
		E e = (E)element[index];
		
		reform(index);
		
		return e;	
	}

	@Override
	public int indexOf(Object o) 
	{
		for (int i = 0; i < index; i++) 
		{
			Object e = element[i];
			if(e == o && e.equals(o))
				return i;
		}
		return 0;	
	}

	@Override
	public boolean contains(Object o) 
	{
		for (Object object : element) 
		{
			if(object.equals(o) && object == o)
				return true;
		}
		return false;	
	}
	
	@Override
	public boolean remove(Object o) 
	{
		for (int i = 0; i < index; i++) 
		{
			Object e = element[i];
			if(e == o && e.equals(o))
			{
				remove(i);
				return true;
			}
		}
		return false;	
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) 
	{
		for (E e : c) 
			add(e);
		return false;	
	}
	
	@Override
	public <T> T[] toArray(T[] a) {		return null;	}
	@Override
	public boolean containsAll(Collection<?> c) {		return false;	}
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {		return false;	}
	@Override
	public boolean removeAll(Collection<?> c) {		return false;	}
	@Override
	public boolean retainAll(Collection<?> c) {		return false;	}
	@Override
	public int lastIndexOf(Object o) {		return 0;	}
	@Override
	public ListIterator<E> listIterator() {		System.out.println("listIterator1"); return null;	}
	@Override
	public ListIterator<E> listIterator(int index) {		System.out.println("listIterator2"); return null;	}
	@Override
	public List<E> subList(int fromIndex, int toIndex) {		return null;	}
	
	

	public static void main(String[] args) 
	{
		LimitList<Integer> list = new LimitList<>(5);
		
		for (int i = 0; i < 20; i++) 
		{
			list.add(i);
		}
//		list.contains(15);
		
		System.out.println("0 = " + JSONObject.toJSONString(list));
//		
//		Iterator<Integer> eleIterator = list.iterator();
//		while(eleIterator.hasNext())
//		{
//			int i = eleIterator.next();
//			if( i == 5)
//				eleIterator.remove();
//			
////			System.out.println(" i = " + i );
//		}
//		
//		System.out.println("1 = " + JSONObject.toJSONString(list));
//		
//		for(Integer i : list)
//		{
//			if( i == 8)
//				list.remove(i);
//		}
//		
//		list.remove(0);
//		
//		System.out.println("2 = " + JSONObject.toJSONString(list));
		
		
		
		
//		String arrayString = "[[1,2,3,4,5],[1,2,3,4,5],[1,2,3,4,5],[1,2,3,4,5]]";
//		JSONArray array = JSONArray.parseArray(arrayString);
//		
//		int [][] stringArray = JSONArray.parseObject(arrayString, int[][].class);
//		
//		
//		System.out.println(stringArray[0][0]);
	}
}
