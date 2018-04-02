package game.tools.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class HttpHashMap<K , V>  implements Serializable
{
	/** 2017年4月14日 下午4:22:23			 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, Object> map = new HashMap<String, Object>();
	
	private int getIndex = 0;
	
	public HttpHashMap(HashMap<String, Object> map) 
	{
		this.map = map;
	}
	
	public HttpHashMap() 
	{
		this(10);
	}
	
	public HttpHashMap(int size) 
	{
		this.map = new HashMap<>(size);
	}
	
	public void put(String key , V value)
	{
		this.map.put(key, value);
	}
	
	public <V extends Object> V getValue(String key)
	{
		return (V)map.get(key);
	}
	
	public <V extends Object> V getValue()
	{
		Object o = map.get(""+getIndex);
		
		getIndex++;
		
		return (V)o;
	}

	public Set<String>  keySet() 
	{
		return this.map.keySet();
	}
	@SuppressWarnings("all")
	public <V extends Object> V get(String key) 
	{
		return getValue(key);
	}
}
