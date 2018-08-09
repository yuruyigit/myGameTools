package game.tools.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import game.tools.utils.StringTools;
import game.tools.utils.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis 操作类 
 * @author zhibing.zhou
 */
public class RedisOper
{
	
	/**
	 * redis分布锁对象
	 */
	private static final RedidLock REDIS_LOCK = new RedidLock();
	
	private static boolean PRINT_INFO = true;
	
	public static void main(String[] args)
	{
		RedisPool.connection("127.0.0.1", 6379,"");
//		User u = new User();
//		u.setId(11123123);
//		u.setName("zzb");
//		u.setAge(12312);
//		
////	RedisUtil.getJedis().
//		System.out.println(RedisUtil.getJedis().select(10));
//		RedisUtil.getJedis().select(10);
//		setJSONArray("test", JSONObject.toJSON(u));
//		RedisOper.setObjectByHash("objMap", u.getId()+1, u);
//		User u1 = RedisOper.getObjectByHash("objMap", u.getId(), User.class);
		
//		RedisUtil.getResource().hset("testMap", "name", "zzb");
//		String val = RedisUtil.getResource().hget("testMap", "name");
//		System.out.println("val = " + u1.getId());
		
//		long roleId = 10010000019010L;
//		
//		for (int i = 0; i < 555; i++) 
//		{
//			String key = "warZone-" + i;
//			for (int j = 0; j < 5000; j++) 
//			{
//				double score = Util.getRandomInt(1, 10000);
//				RedisOper.execute(RedisCmd.zadd, key, score , String.valueOf(roleId ++));
//			}
//		}
		
//		String key = "warZone-2";
//		for (int j = 0; j < 20; j++) 
//		{
//			double score = Util.getRandomInt(100, 1000);
//			RedisOper.execute(RedisCmd.zadd, key, score , String.valueOf(roleId ++));
//		}
		
//		
//		System.out.println("添加测试数据OK");
		
//		TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>();
//		treeMap.put("a", 1);
//		treeMap.put("c", 3);
//		treeMap.put("d", 4);
//		treeMap.put("b", 2);
//		
//		
//		Iterator<String> iterator = treeMap.keySet().iterator();
//		while(iterator.hasNext())
//		{
//			String key = iterator.next();
//			
//			int val = treeMap.get(key);
//			
//			System.out.println("key=" + key + " val=" + val);
//		}
		
		REDIS_LOCK.testLockUnlock();
	}
	
	public static boolean connection(String addr , int port , String passWord)
	{
		return RedisPool.connection(addr, port, passWord);
	}
	
	public static boolean connection(JedisConnnection ...infoArr) 
	{
		return RedisPool.connection(infoArr);
	}
	
	/**
	 * <pre>
	 * 	客户端jedis的一致性哈稀进行分片原理：初始化ShardedJedisPool的时候，
	 * 	会将上面程序中的jdsInfoList数据进行一个算法技术，主要计算依据为list中的index位置来计算.
	 * </pre>
	 * @param infoArr 传入一个或多个的redis连接对象
	 * @return 返回一个redis 集群连接池
	 */
	public static <T> T getObjectByHash(String hashName , Object key , Class<T> clzss , int dbNo)
	{
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(hashName);
			jedis = myJedisPool.getResource();
			
			String value = jedis.hget(hashName , String.valueOf(key));
			if(StringTools.empty(value))
				return null;
			
			return (T)JSONObject.parseObject(value , clzss);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			myJedisPool.returnResource(jedis);
		}
		return null;
	}
	
	
	public static <T> T getObjectByHash(String hashName , Object key , Class<T> clzss)
	{
		return getObjectByHash(hashName, key, clzss , 0);
	}
	
	
	public static long removeByHash(String hashName , Object key , int dbNo)
	{
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(hashName);
			jedis = myJedisPool.getResource();
//			jedis.select(dbNo);
			long value = jedis.hdel(hashName , String.valueOf(key));
			return value;	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			myJedisPool.returnResource(jedis);
		}
		return -1;
	}
	
	
	public static long removeByHash(String hashName , Object key)
	{
		return removeByHash(hashName, key, 0);
	}
	
	public static <T> HashMap<String, T> getAllByHash(String hashName , Class<T> clzss , int dbNo)
	{
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(hashName);
			if(myJedisPool == null)
				return null;
			
			jedis = myJedisPool.getResource();
//			jedis.select(dbNo);
			Map<String,String> map = jedis.hgetAll(hashName);
			
			HashMap<String , T> retMap = new HashMap<String, T>(map.size());
			
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext())
			{
				String key = it.next();
				String val = map.get(key);
				
				retMap.put(key, (T)JSONObject.parseObject(val , clzss));
			}
			
			return retMap;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			myJedisPool.returnResource(jedis);
		}
		return null;
	}
	
	public static <T> HashMap<String, T> getAllByHash(String hashName , Class<T> clzss)
	{
		return getAllByHash(hashName , clzss , 0);
	}
	
	public static long setObjectByHash(String hashName , Object key , Object val , int dbNo)
	{
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(hashName);
			jedis = myJedisPool.getResource();
//			jedis.select(dbNo);
			String content = JSONObject.toJSONString(val);
			return jedis.hset(hashName , String.valueOf(key) , content);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			myJedisPool.returnResource(jedis);
		}
		
		return 0;
	}
	
	public static long setObjectByHash(String hashName , Object key , Object val)
	{
		return setObjectByHash(hashName, key, val, 0);
	}
	
	public static <T> T execute(RedisCmd cmd, String key , Object ...params)
	{
		return (T)execute(cmd, 0,key,  params);
	}
	
	public static Object execute(RedisCmd cmd, int dbNo ,String key ,  Object ...params)
	{
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(key);
			jedis = myJedisPool.getResource();
//			jedis.select(dbNo);
			switch (cmd) 
			{
				case del:
					return jedis.del(key);
				case zrangeWithScores:
					return zrangeWithScores(jedis , key, "asc", params );
				case zrevrangeWithScores:
					return zrangeWithScores(jedis , key, "desc", params );
				case zremArr:
					return jedis.zrem(key, (String[])params);
				case zrem:
					return jedis.zrem(key, (String)params[0]);
				case zremrangebyrank:
					return jedis.zremrangeByRank(key, (long)params[0] , (long)params[1]);
				case zrevrange:
					return jedis.zrevrange(key, (long)params[0] , (long)params[1]);
				case zsize:
				case zcard:
					return jedis.zcard(key);
				case zcount://有序列表对应score区间内，有多少个元素  zhu
					return jedis.zcount(key, (double)params[0], (double)params[1]);
				case zrevrank:
					return jedis.zrevrank(key, (String)params[0]);
				case zrank:
					return jedis.zrank(key, (String)params[0]);
				case zadd:
					return jedis.zadd(key, (double)params[0] , (String)params[1]);
				case zadds:
					return jedis.zadd(key, (Map<String , Double>)params[0]);
				case hset:
					return jedis.hset(key , String.valueOf(params[0]) , String.valueOf(params[1]));
				case hget:
					return jedis.hget(key , (String)params[0]);
				case hmget:
					return jedis.hmget(key, (String[])params);
				case hmset:
					return jedis.hmset(key, (Map<String,String>)params[0]);
				case hmsetJavaObj:
					return jedis.hmset(key, Util.toMapKeyValueString((Map<String, String>)JSONObject.toJSON(params[0])));
				case hvals:
					return jedis.hvals(key);
				case hkeys:
					return jedis.hkeys(key);
				case hgetAll:
					return jedis.hgetAll(key);
				case hgetAllJavaObj:
					return Util.parseMaptoJavaObject((Class)params[0], jedis.hgetAll(key));
				case hdel:
					return jedis.hdel(key,(String)params[0]);
				case exists:
					return jedis.exists(key);
				case lpush:
					return jedis.lpush(key, (String)params[0]);
				case lsize:
				case lget:
					return jedis.lrange(key, 0, jedis.llen(key));
				case lrem:
					return jedis.lrem(key, 0L, (String)params[0]);
				case lset:
					return jedis.lset(key, (long)params[0], (String)params[1]);
				case sadd:
					return jedis.sadd(key, (String)params[0]);
				case smimber:
					return jedis.sismember(key, (String)params[0]);
				case smembers:
					return jedis.smembers(key);
				case srem:
					return jedis.srem(key,(String)params[0]);
				case set:
					return jedis.set(key, (String)params[0]);
				case get:
					return jedis.get(key);
				case incr:
					return jedis.incr(key);
				case keys:
					return keys(key);
				case expire:
					return jedis.expire(key, (int)params[0]);
				default:
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
		
		return null;
	}
	
	private static Object keys(String key)
	{
		String cmd = "*"+key+"*";
		Set<String> keySet = null;
		ArrayList<MyJedisPool> jedisPoolList = RedisPool.getJedisPoolList();
		for (MyJedisPool myJedisPool : jedisPoolList)
		{
			Jedis jedis = myJedisPool.getResource();
			if(keySet == null)
				keySet = jedis.keys(cmd);
			else
				keySet.addAll(jedis.keys(cmd));
		}
		return keySet;
	}



	private static Object zrangeWithScores(Jedis jedis , String key, String sort, Object... params ) 
	{
		ArrayList<Object[]> list = null;
		Set<Tuple> tuples = null;
		if(sort.equalsIgnoreCase("asc"))
			tuples = jedis.zrangeWithScores(key, (long)params[0], (long)params[1]);
		else if(sort.equalsIgnoreCase("desc"))
			tuples = jedis.zrevrangeWithScores(key, (long)params[0], (long)params[1]);
		if(tuples.size() > 0)
			list = new ArrayList<Object[]>();
			
		for (Tuple tuple : tuples) 
			list.add(new Object [] {tuple.getElement() , tuple.getScore()});
		return list;
	}

	
	private static Object zrangeWithScores(ShardedJedis jedis , String key, String sort, Object... params ) 
	{
		ArrayList<Object[]> list = null;
		Set<Tuple> tuples = null;
		if(sort.equalsIgnoreCase("asc"))
			tuples = jedis.zrangeWithScores(key, (long)params[0], (long)params[1]);
		else if(sort.equalsIgnoreCase("desc"))
			tuples = jedis.zrevrangeWithScores(key, (long)params[0], (long)params[1]);
		if(tuples.size() > 0)
			list = new ArrayList<Object[]>();
			
		for (Tuple tuple : tuples) 
			list.add(new Object [] {tuple.getElement() , tuple.getScore()});
		return list;
	}

	public static int getConneciontCount() 
	{
		return RedisPool.getConneciontCount();
	}
	
	public static boolean isConneciont() 
	{
		if(RedisPool.getConneciontCount() > 0)
			return true;
		return false;
	}
	
	
	//////////////////////////////////////////redis锁/////////////////////////////////////////////////
	public static String lock(String lockName)
	{
		return lock(lockName, 0);
	}
	
	public static String lock(String lockName , long acquireTimeOut)
	{
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(lockName);
			jedis = myJedisPool.getResource();
			
			return REDIS_LOCK.lock1(jedis, lockName, acquireTimeOut);
		}
		finally
		{
			myJedisPool.returnResource(jedis);
		}
		
	}
	
	public static boolean unlock(String lockName , String identifier)
	{
		
		MyJedisPool myJedisPool = null;
		Jedis jedis = null;
		try
		{
			myJedisPool = RedisPool.getJedisPool(lockName);
			jedis = myJedisPool.getResource();
			
			return REDIS_LOCK.unlock1(jedis, lockName, identifier);
		}
		finally
		{
			myJedisPool.returnResource(jedis);
		}
	}
}