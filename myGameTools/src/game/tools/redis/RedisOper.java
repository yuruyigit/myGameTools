package game.tools.redis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson.JSONObject;
import game.tools.threadpool.ThreadLocal;
import game.tools.utils.StringTools;
import game.tools.utils.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

/**
 * Redis 操作类 
 * @author zhibing.zhou
 */
public class RedisOper
{
	
	/** redis分布锁对象 */
	private static final RedidLock REDIS_LOCK = new RedidLock();
	
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
	
	public static ArrayList<String> scan()
	{
		return scan("*");
	}
	
	public static ArrayList<String> scan(int count)
	{
		return scan(count , "*");
	}
	
	public static ArrayList<String> scan(String match)
	{
		return scan(10 , match);
	}
	
	public static ArrayList<String> scan(int count , String match)
	{
		int [][] scanCursor = ThreadLocal.remove("scan");
		
		Jedis jedis = null;
		
		int cursor = 0 , size = RedisPool.getJedisPoolList().size();
		
		boolean isCreate = false;
		
		if(scanCursor == null)
		{
			scanCursor = new int[size][2];
			isCreate = true;
		}
		
		ScanParams s = new ScanParams();
		s.count(count);
		s.match(match);
		
		ArrayList<String> list = new ArrayList<>(count * size);
		
		for (int i = 0 ; i < size; i++) 
		{
			if(!isCreate)
			{
				if(scanCursor[i][1] <= 0)
					continue;
			}
			
			cursor = scanCursor[i][1];
			
			MyJedisPool myJedisPool = RedisPool.getJedisPoolByIndex(i);
			try 
			{
				jedis = myJedisPool.getResource();
				ScanResult<String> scanResult = jedis.scan(cursor, s);
				
				scanCursor[i][0] = i;
				scanCursor[i][1] = scanResult.getCursor();
				
				list.addAll(scanResult.getResult());
			}
			finally
			{
				myJedisPool.returnResource(jedis);
			}

			ThreadLocal.setLocal("scan", scanCursor);
		}
		
		return list;
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
	
	public static boolean isConnecion() 
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