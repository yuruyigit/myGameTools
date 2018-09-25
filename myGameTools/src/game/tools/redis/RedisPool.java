package game.tools.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import game.tools.db.cache.expire.ExpireCacheDataMap;
import game.tools.utils.StringTools;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanResult;

class MyJedisPool
{
	private JedisConnnection conn;
	private JedisPool jedisPool;
	private int redisIndex ;
	
	public MyJedisPool(JedisPool jedisPool , int redisIndex , JedisConnnection conn) 
	{
		this.jedisPool = jedisPool;
		this.redisIndex = redisIndex;
		this.conn = conn;
	}
	
	public JedisPool getJedisPool() {		return jedisPool;	}
	public int getRedisIndex() {		return redisIndex;	}	
	public Jedis getResource()	{		return jedisPool.getResource();		}

	public void returnResource(Jedis jedis)	{		jedisPool.returnResource(jedis);	}
	
	/**
	 * @param connection
	 * @return 返回这个连接池是否相同
	 */
	public boolean same(JedisConnnection connection)
	{
		if(connection.getHost().equals(conn.getHost()) &&
				connection.getPort() == conn.getPort())
			return true;
		return false;
	}
	
	@Override
	public String toString() 
	{
		return conn.toString();
	}
}


class RedisPool 
{
	// Redis服务器IP
	private static String ADDR = "localhost";
	// Redis的端口号
	private static int PORT = 6379;
	// 访问密码
	private static String AUTH = "";

	/** 可用连接实例的最大数目，默认值为8, 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。*/
	private static int MAX_ACTIVE = 1024;
	/** 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。*/
	private static int MAX_IDLE = 50 , MAX_TOTAL = 9999;
	/** 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException */
	private static int MAX_WAIT = 10000;
	/**  */
	private static int TIMEOUT = 10000;
	/** 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；*/
	private static boolean TEST_ON_BORROW = true;
	/** 2016年9月20日下午8:46:34  注册的多个redis连接池列表*/
	private static final ArrayList<MyJedisPool> JEDIS_POOL_LIST = new ArrayList<>();

	/** 2016年9月20日下午8:43:21 连接池配置 */
	private static final JedisPoolConfig JEDIS_POOL_CONFIG;
	
	static
	{
		JEDIS_POOL_CONFIG = new JedisPoolConfig();
		JEDIS_POOL_CONFIG.setMaxIdle(MAX_IDLE);
		JEDIS_POOL_CONFIG.setMaxTotal(MAX_TOTAL);
		JEDIS_POOL_CONFIG.setMaxWaitMillis(MAX_WAIT);
		JEDIS_POOL_CONFIG.setTestOnBorrow(TEST_ON_BORROW);
	}
	
	static MyJedisPool getJedisPoolByIndex(int index)
	{
		return JEDIS_POOL_LIST.get(index);
	}
	
	static MyJedisPool getJedisPool(String key)
	{
		if(JEDIS_POOL_LIST.size() == 1)
			return getJedisPoolByIndex(0);
		
		return getMyJedisPool(key);
	}
	
	private static MyJedisPool getMyJedisPool(String key)
	{
		for (MyJedisPool myJedisPool : JEDIS_POOL_LIST) 
		{
			Jedis jedis = myJedisPool.getResource();
			try 
			{
				if(jedis.exists(key))
					return myJedisPool;
			}
			finally
			{
				myJedisPool.returnResource(jedis);
			}
		}
		
		int minJedisPool = getMinJedisPool();
		
		return getJedisPoolByIndex(minJedisPool);
	}
	
	private static int getMinJedisPool()
	{
		long curDbsize = 0;
		int redisIndex = -1;
		
		int size = JEDIS_POOL_LIST.size();
		
		for (int i = 0; i < size; i++)
		{
			MyJedisPool myJedisPool = JEDIS_POOL_LIST.get(i);
			Jedis jedis = myJedisPool.getResource();
			
			try 
			{
				long dbsize = jedis.dbSize();
				if(curDbsize == 0 || curDbsize > dbsize)
				{
					curDbsize = dbsize;
					redisIndex = i;
					
					if(curDbsize == 0 )
						break;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally 
			{
				myJedisPool.returnResource(jedis);
			}
		}
		return redisIndex;
	}
	
	
	public static boolean connection(String addr, int port, String passWord) {
		return connection(new JedisConnnection(addr, port, passWord));

	}
	
	public static boolean connection(JedisConnnection ...infoArr) 
	{
		if(infoArr.length == 0)
			return false;
		
		JedisPoolConfig config = JEDIS_POOL_CONFIG;
		
		try 
		{
			boolean isAdd = false;
			
			for (int i = 0; i < infoArr.length; i++ ) 
			{
				JedisConnnection conn = infoArr[i];
				JedisPool jedisPool = null;
				
				if(exist(conn))					//不去重复添加redis连接
					continue;
				
				if(StringTools.empty(conn.getPass()))
					jedisPool = new JedisPool(config , conn.getHost(), conn.getPort());
				else
					jedisPool = new JedisPool(config , conn.getHost(), conn.getPort() , TIMEOUT , conn.getPass());
				
				JEDIS_POOL_LIST.add(new MyJedisPool(jedisPool, i , conn));
				
				isAdd = true;
			}
			
			if(isAdd)
				System.out.println("------------>Redis Start On = " + Arrays.toString(JEDIS_POOL_LIST.toArray()));
			
			return true;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Redis Not Connection Ok !");
			System.exit(-1);

			return false;
		}
	}

	private static boolean exist(JedisConnnection conn) 
	{
		for (MyJedisPool myJedisPool : JEDIS_POOL_LIST) 
		{
			if(myJedisPool.same(conn))
				return true;
		}
		return false;
	}

	public static void closeRedis() 
	{
		
	}
	
	public static int getConneciontCount() {
		return JEDIS_POOL_LIST.size();
	}
	
	public static ArrayList<MyJedisPool> getJedisPoolList()
	{
		return JEDIS_POOL_LIST;
//		return (ArrayList<MyJedisPool>)Collections.unmodifiableList(JEDIS_POOL_LIST);
	}
}
