package game.tools.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/**
 * @author zhibing.zhou
 * 多个redis库合并
 */
public class RedisMerge 
{
	
	
	private static long index ;
	
	/**
	 * formJedisConnnection 合并到-> toJedisConnnection
	 * 
	 * @param formJedisConnnection 要合并的库
	 * @param toJedisConnnection 合并到这个库
	 */
	public static void merge(JedisConnnection formJedisConnnection , JedisConnnection toJedisConnnection)
	{
		RedisOper.connection(formJedisConnnection , toJedisConnnection);
		
		MyJedisPool formJedisPool = RedisPool.getJedisPoolByIndex(0);
		Jedis formJedis = formJedisPool.getResource();
		
		MyJedisPool toJedisPool = RedisPool.getJedisPoolByIndex(1);
		Jedis toJedis = toJedisPool.getResource();
		
		Set<String> keySet = formJedis.keys("*");
		
		int needSize = keySet.size();
		
		String handlerKey = null;
		
		try 
		{
			
			for (String key : keySet) 
			{
				handlerKey = key;
				
				String type = formJedis.type(key);
				
				switch(type)
				{
					case RedisType.string:
						string(formJedis , toJedis , key);
						break;
					case RedisType.list:
						list(formJedis , toJedis , key);
						break;
					case RedisType.set:
						set(formJedis , toJedis , key);
						break;
					case RedisType.zset:
						zset(formJedis , toJedis , key);
						break;
					case RedisType.hash:
						hash(formJedis , toJedis , key);
						break;
					default:
						
						break;
				}
			}
		}
		catch (Exception e) 
		{
			System.err.println("Error Key [" + handlerKey + "]");
			e.printStackTrace();
		}
		finally
		{
			formJedisPool.returnResource(formJedis);
			toJedisPool.returnResource(toJedis);
			
			System.out.println("Merge Result : Need " + needSize +" Success " + index + " Fail " + (needSize - index));
		}
		
		
	}
	
	private static void hash(Jedis formJedis, Jedis toJedis, String key) 
	{
		Map<String , String> valueMap = formJedis.hgetAll(key);
		
		toJedis.hmset(key, valueMap);
		
		index++;
	}

	private static void zset(Jedis formJedis, Jedis toJedis, String key) 
	{
		long length = formJedis.zcard(key);
		Set<Tuple> valueSet = formJedis.zrangeWithScores(key, 0, length);
		
		HashMap<String, Double> map = new HashMap<>(valueSet.size());
		for (Tuple tuple : valueSet) 
		{
			String keyName = tuple.getElement();
			double score = tuple.getScore();
			
			
			map.put(keyName, score);
			
		}
		
		toJedis.zadd(key, map);
		
		index++;
	}

	private static void set(Jedis formJedis, Jedis toJedis, String key) 
	{
		Set<String> valueSet = formJedis.smembers(key);
		
		toJedis.sadd(key, valueSet.toArray(new String[0]));
		
		index++;
	}

	private static void list(Jedis formJedis , Jedis toJedis , String key)
	{
		long length = formJedis.llen(key);
		List<String> valueList = formJedis.lrange(key, 0, length);
		
		toJedis.lpush(key, valueList.toArray(new String[0]));
		
		index++;
	}
	
	private static void string(Jedis formJedis , Jedis toJedis , String key)
	{
		String value = formJedis.get(key);
		
		toJedis.set(key, value);
		
		index++;
	}
	
	public static void main(String[] args) 
	{
		RedisMerge.merge(new JedisConnnection("127.0.0.1", 7001), new JedisConnnection("127.0.0.1", 6379));
	}
}
