package game.tools.db.cache.expire;

public class KeyValue<K,V>
{
	private long updateTime;
	private K key ;
	private V value;
	
	public KeyValue(K key, V value) 
	{
		this.key = key;
		this.value = value;
		
		updateTime();
	}
	
	void updateTime()	{		this.updateTime = System.currentTimeMillis();	}
	public long getUpdateTime() {		return updateTime;	}
	public K getKey() {		return key;	}
	public V getValue() {		return value;	}
}