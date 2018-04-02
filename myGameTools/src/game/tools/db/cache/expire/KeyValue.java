package game.tools.db.cache.expire;

public class KeyValue
{
	private long updateTime;
	private Object key ;
	private Object value;
	
	public KeyValue(Object key, Object value) 
	{
		this.key = key;
		this.value = value;
		
		updateTime();
	}
	
	void updateTime()	{		this.updateTime = System.currentTimeMillis();	}
	public long getUpdateTime() {		return updateTime;	}
	public Object getKey() {		return key;	}
	public Object getValue() {		return value;	}
}