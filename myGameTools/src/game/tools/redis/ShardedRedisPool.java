package game.tools.redis;

import java.util.ArrayList;
import java.util.Arrays;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

 final class ShardedRedisPool {

	// Redis服务器IP
	private static String ADDR = "localhost";
	// Redis的端口号
	private static int PORT = 6379;
	// 访问密码
	private static String AUTH = "";

	private static String passWord = "";

	// 可用连接实例的最大数目，默认值为8；
	// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 1024;
	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 50 , MAX_TOTAL = 9999;
	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;

	private static int TIMEOUT = 10000;
	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;

	/** 2016年4月21日上午12:32:28  单个reids池*/
	private static JedisPool JEDIS_POOL = null;
	/** 2016年4月21日上午12:32:09  redis集群池*/
	private static ShardedJedisPool SHARD_JEDIS_POOL = null; 

	
	/**
	 * @return 返回一个redis池 配置 
	 */
	private static JedisPoolConfig createJedisPoolConfig()
	{
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(MAX_IDLE);
		poolConfig.setMaxTotal(MAX_TOTAL);
		poolConfig.setMaxWaitMillis(MAX_WAIT);
		poolConfig.setTestOnBorrow(TEST_ON_BORROW);
		
		return poolConfig;
	}
//	public static boolean connection(String addr, int port, String passWord) {
//		try 
//		{
//			if ("".equals(passWord))
//				JEDIS_POOL = new JedisPool(POOL_CONFIG, addr, port, TIMEOUT);
//			else
//				JEDIS_POOL = new JedisPool(POOL_CONFIG, addr, port, TIMEOUT, passWord);
//			JEDIS_POOL.getResource();
//
//			System.out.println("------------>Redis Start On Port=" + port);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Redis Not Connection Ok !");
//			System.exit(-1);
//
//			return false;
//		} finally {
//		}
//
//		return true;
//
//	}
	
	
	public static boolean connection(String addr, int port, String passWord) {
		return connection(new JedisConnnection(addr, port, passWord));

	}
	
	/**
	 * <pre>
	 *注：如果要注册多个连接的话，请注意【注册顺序】，
	 *则请数组列表【有序向后添加】连接对象，否则获取不到之前老的键值。
	 *
	 *如：[127.0.0.1:6379],目前只有一个连接对象，
	 *如果在添加新的节点的话，请从后面添加开始 ， [127.0.0.1:6379, 127.0.0.1:7001]。
	 *
	 *初始化ShardedJedisPool的时候，会将上面程序中的jdsInfoList数据进行一个算法技术，主要计算依据为list中的index位置来计算。
	 *第一次set值的时候，是按list下标来hash计算出一个服务器的，所以取值的时候，list顺序不能变动，否则获取不到之前老的键值。
	 *
	 *提示：ShardedJedisPool，则是使用向前注册 ，也有就是，新添加节点的话， 则【有序向前添加】，否则获取不到老值，
	 *这里以使用习惯，做了一个封装，使用【有序向后添加】。
	 * </pre>
	 * @param infoArr 传入一个或多个有序的redis连接对象列表, 
	 * @return 返回是否连接创建成功
	 */
	public static boolean connection(JedisConnnection ...infoArr) 
	{
		ShardedJedis jedis = null;
		
		try 
		{
			ArrayList<JedisShardInfo> infoList = new ArrayList<>();
			
			for (int i = infoArr.length - 1 ; i >= 0; i-- ) 
			{
				JedisConnnection conn = infoArr[i];
				
				JedisShardInfo info = new JedisShardInfo(conn.getHost(), conn.getPort() , 3000, conn.getTimeout() ,conn.getWeight());
				if(conn.getPass() != null && !conn.getPass().equals(""))
					info.setPassword(conn.getPass());
				
				infoList.add(info);
			}
			
			SHARD_JEDIS_POOL = new ShardedJedisPool(createJedisPoolConfig(), infoList);
			
			jedis = SHARD_JEDIS_POOL.getResource();
			
			System.out.println("------------>Redis Start On Port=" + Arrays.toString(infoArr));
			
			return true;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Redis Not Connection Ok !");
			System.exit(-1);

			return false;
		}
		finally 
		{
			SHARD_JEDIS_POOL.returnResource(jedis);
		}
	}
	
	
	public static void main(String[] args) 
	{
		ShardedRedisPool.connection(new JedisConnnection("192.168.1.131", 9720, "test131") , 
				new JedisConnnection("192.168.1.55", 6379));
		
		
//		RedisPool.connection("192.168.1.55", 6379 , "");
//		
//		for (int i = 0; i < 1000; i++) 
//		{
//			sjp.getResource().set("test-info-"+i, String.valueOf(Math.random()));
//			
//			System.out.println(" i = " + i);
//		}
//		
		String key = "playInfo-10010000000001";
		key="gsHash";
//		getShardedJedis.del(key);
	
//		System.out.println(JSONObject.toJSONString(getJedis().hgetAll(key)));
//		System.out.println(getJedis().exists("warZone-5"));
	}

//	/**
//	 * 获取Jedis实例
//	 * @return
//	 */
//	public static Jedis getJedis()
//	{
//		Jedis jedis = null;
//		try {
//			if (JEDIS_POOL != null) {
//				jedis = JEDIS_POOL.getResource();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return jedis;
//	}
	
	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public static void closeRedis(Jedis jedis) 
	{
		if (jedis != null) 
		{
			JEDIS_POOL.returnResource(jedis);
		}
	}
	
	/**
	 * @param jedis 
	 * 释放jedis 资源
	 */
	public static void closeRedis(ShardedJedis jedis)
	{
		if (jedis != null) 
		{
//			SHARD_JEDIS_POOL.returnResourceObject(jedis);
			SHARD_JEDIS_POOL.returnResource(jedis);
		}
	}
	
	
	
	/**
	 * @return 返回一个集群中的连接
	 */
	public static ShardedJedis getJedis()
	{
		ShardedJedis jedis = null;
		try 
		{
			if (SHARD_JEDIS_POOL != null) 
			{
				jedis = SHARD_JEDIS_POOL.getResource();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return jedis;
	}
	
}