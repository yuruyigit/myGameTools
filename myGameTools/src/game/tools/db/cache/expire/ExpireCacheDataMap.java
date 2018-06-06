package game.tools.db.cache.expire;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import game.tools.utils.DateTools;

class KeyValue<K,V>
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
	
	@Override
	public String toString() 
	{
		return "[" + key + "->" + value + "->" + DateTools.getCurrentTimeString(updateTime) + "]";
	}
}

public class ExpireCacheDataMap<K,V>
{
	/** 2016年9月21日下午4:19:12 key过期时间，单位毫秒间隔 , 这里是1个小时*/
	private int keyExpireTime = 1 * 60 * 60 * 1000;
	/** 2016年9月21日下午4:21:19 key缓存大小，大于这个数的话，去检查过期 */
	private int keyCheckSize = 0 ;
	/** 2016年9月20日下午9:50:37 key连接的映射 */
	private final ConcurrentHashMap<K, KeyValue<K,V>> keyIndexMap = new ConcurrentHashMap<K, KeyValue<K,V>>();
	
	
	/**
	 *  过期时间，单位小时 （默认一个小时）
	 *  检查数量（默认0）。
	 */
	public ExpireCacheDataMap() {}
	
	/**
	 * @param keyExpireTime 过期时间，单位分钟 （默认一个小时）
	 * @param keyCheckSize	大于这个数的话，去检查过期（默认10000）。
	 */
	public ExpireCacheDataMap(int keyExpireTime , int keyCheckSize) 
	{
		this.keyExpireTime = keyExpireTime * 60 *  1000;
		this.keyCheckSize = keyCheckSize;
	}
	
	/**
	 * 检查过期key
	 */
	private void checkExpireKey()
	{
		if(keyIndexMap.size() < keyCheckSize)			//如果还没达检查的长度的话就直接返回
			return ;
		
		for (KeyValue<K , V> idKey : keyIndexMap.values()) 
		{
			long gapTime = System.currentTimeMillis() - idKey.getUpdateTime();
			
			if(gapTime >= keyExpireTime)
				keyIndexMap.remove(idKey.getKey());
		}
	}
	
	public void put(K key , V value)
	{
		KeyValue<K,V> keyValue = new KeyValue<K,V>(key, value);
		keyIndexMap.put(keyValue.getKey() , keyValue);
	}
	
	
	/**
	 * @param key
	 * @return 获取一个还未过期的key
	 */
	private KeyValue<K,V> getKeyObject(K key)
	{
		checkExpireKey();
		
		KeyValue<K,V> keyVal = keyIndexMap.get(key);			
		if( keyVal != null)
		{
			keyVal.updateTime();			//更新时间
			return keyVal;
		}
		
		return null;
	}
	
	/**
	 * @param key
	 * @return 获取一个还未过期的key
	 */
	private V getValue(K key)
	{
		KeyValue<K,V> keyVal = getKeyObject(key);
		
		if(keyVal != null)
			return keyVal.getValue();
		return null;
	}
	
	public V get(K key) 
	{
		return getValue(key);
	}
	
	public V remove(K key) 
	{
		KeyValue<K,V> value = this.keyIndexMap.remove(key);
		
		return value.getValue();
	}
	
	public ArrayList<V> toArrayList()
	{
		checkExpireKey();
		
		ArrayList<V> list = new ArrayList<V>(keyIndexMap.size());
		
		for (KeyValue<K,V> keyVal: keyIndexMap.values()) 
		{
			list.add(keyVal.getValue());
		}
		return list;
	}
	
	@Override
	public String toString() {
		return this.keyIndexMap.values().toString();
	}
	
	public static void main(String[] args)  throws Exception
	{
		ExpireCacheDataMap<String , Object> d = new ExpireCacheDataMap<String , Object>(1,1);
		d.put("1", "dddd");
		d.put("2", "ddddsdfa");
		
		Object val  = d.getValue("1");
		System.out.println(val);
		
		Thread.sleep(10* 1000);
		
		val  = d.getValue("1");
		System.out.println(val);
		
		Thread.sleep(60* 1000);
		
		val  = d.getValue("1");
		System.out.println(val);
	}
}
