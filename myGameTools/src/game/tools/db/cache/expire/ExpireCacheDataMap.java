package game.tools.db.cache.expire;

import java.util.concurrent.ConcurrentHashMap;



public class ExpireCacheDataMap
{
	/** 2016年9月21日下午4:19:12 key过期时间，单位毫秒间隔 , 这里是1个小时*/
	private int keyExpireTime = 1 * 60 * 60 * 1000;
	/** 2016年9月21日下午4:21:19 key缓存大小，大于这个数的话，去检查过期 */
	private int keyCheckSize = 10000 ;
	
	/**
	 *  过期时间，单位小时 （默认一个小时）
	 *  检查数量（默认10000）
	 */
	public ExpireCacheDataMap() {	}
	/**
	 * @param keyExpireTime 过期时间，单位分钟 （默认一个小时）
	 * @param keyCheckSize	大于这个数的话，去检查过期（默认10000）。
	 */
	public ExpireCacheDataMap(int keyExpireTime , int keyCheckSize) 
	{
		this.keyExpireTime = keyExpireTime * 60 *  1000;
		this.keyCheckSize = keyCheckSize;
	}
	
	/** 2016年9月20日下午9:50:37 key连接的映射 */
	private final ConcurrentHashMap<Object, KeyValue> keyIndexMap = new ConcurrentHashMap<Object, KeyValue>();
	
	/**
	 * 检查过期key
	 */
	private void checkExpireKey()
	{
		if(keyIndexMap.size() < keyCheckSize)			//如果还没达检查的长度的话就直接返回
			return ;
		
		for (KeyValue idKey : keyIndexMap.values()) 
		{
			long gapTime = System.currentTimeMillis() - idKey.getUpdateTime();
			
			if(gapTime >= keyExpireTime)
				keyIndexMap.remove(idKey.getKey());
		}
	}
	
	public void put(Object key , Object value)
	{
		KeyValue keyValue = new KeyValue(key, value);
		keyIndexMap.put(keyValue.getKey() , keyValue);
	}
	
	
	/**
	 * @param key
	 * @return 获取一个还未过期的key
	 */
	public KeyValue getKeyObject(Object key)
	{
		checkExpireKey();
		
		KeyValue keyVal = keyIndexMap.get(key);			
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
	public <E> E getValue(Object key)
	{
		checkExpireKey();
		
		KeyValue keyVal = getKeyObject(key);
		
		if(keyVal != null)
			return (E)keyVal.getValue();
		return null;
	}
	
	
	public static void main(String[] args)  throws Exception
	{
		ExpireCacheDataMap d = new ExpireCacheDataMap(1,1);
		d.put(100, "dddd");
		Object val  = d.getValue(100);
	}
	
	public<E> E get(String key) 
	{
		return getValue(key);
	}

}
