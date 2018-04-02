package game.tools.redis;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import game.tools.utils.StringTools;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

public class RedidLock 
{
	/**
	 * REDIS锁前缀名称
	 */
	private static final String LOCK_NAME = "REDIS_LOCK:";
	/**
	 * 锁的失效时长，这里为15秒钟,(单位:秒)
	 */
	private static final int LOCK_EXPIRE = 15;
    /**
     *  redis脚本的sha缓存
     */
    private static final ConcurrentMap<String, String> SHA_CACHE = new ConcurrentHashMap<>();
    
	/**
	 * 加锁的lua脚本 
	 */
	private static final String LOCK_SCRIPT = 
            "local key = KEYS[1]\n" +
            "local identifier = ARGV[1]\n" +
            "local lockTimeOut = ARGV[2]\n" +
            "\n" +
            "if redis.call(\"SETNX\", key, identifier) == 1 then\n" +
            "    redis.call(\"EXPIRE\", key, lockTimeOut)\n" +
            "    return 1\n" +
            "elseif redis.call(\"TTL\", key) == -1 then\n" +
            "    redis.call(\"EXPIRE\", key, lockTimeOut)\n" +
            "end\n" +
            "return 0"
            ;

	/**
	 * 解锁的lua脚本
	 */
	private static final String UN_LOCK_SCRIPT = 
			"local key = KEYS[1]\n" +
            "local identifier = ARGV[1]\n" +
            "\n" +
            "if redis.call(\"GET\", key) == identifier then\n" +
            "    redis.call(\"DEL\", key)\n" +
            "    return 1\n" +
            "end\n" +
            "return 0"
            ;

	

	/**
	 * 测试加锁，解锁。
	 */
	public void testLockUnlock()
	{
		Runnable r = new Runnable() {
			
			@Override
			public void run() 
			{
				String lockName = "testRedisLock";
//				RedisOper.unlock(lockName, "2018-01-02 10:48:07.698");
				
				String id = RedisOper.lock(lockName , 11 * 1000);
				
				if(!StringTools.empty(id))
				{
					System.out.println(Thread.currentThread().getId() + "已拿到redisLock");
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					RedisOper.unlock(lockName, id);
				}
				else
				{
					System.out.println(Thread.currentThread().getId() + "没拿到redisLock");
				}
			}
		};
		
		Thread [] tArray = new Thread[10];
		
		for (int i = 0; i < tArray.length; i++) 
			tArray[i] = new Thread(r);	
		
		for (int i = 0; i < tArray.length; i++) 
			tArray[i].start();
	}
	
	/**
	 * @param connection
	 * @param acquireTimeOut 请求时长，如果没有拿到锁，则等待的时长(单位毫秒)。
	 * @return 返回这个锁唯一字符串标识
	 */
	public String lock(Jedis connection, String lockName , long acquireTimeOut)
	{
		if(acquireTimeOut <= 0)
			acquireTimeOut = 10;
			
		lockName = LOCK_NAME + lockName;
		
		String identifier = UUID.randomUUID().toString();
		
        List<String> keys = Collections.singletonList(lockName);
        List<String> argv = Arrays.asList(identifier,String.valueOf(LOCK_EXPIRE));

        long acquireTimeEnd = System.currentTimeMillis() + acquireTimeOut;
        boolean acquired = false;
        
        while (!acquired && (System.currentTimeMillis() < acquireTimeEnd)) 			//在请求的时间内循环,是否能拿到锁
        {
        	String sha = SHA_CACHE.get(LOCK_SCRIPT);
            if (StringTools.empty(sha)) 
            {
                sha = connection.scriptLoad(LOCK_SCRIPT);			//load 脚本得到 sha1 缓存,把脚本缓存到redis里面,获取sha值(脚本的唯一标识）
                SHA_CACHE.put(LOCK_SCRIPT, sha);
            }
            
            long n = (long)connection.evalsha(sha, keys, argv);				//通过sha执行lua
            
            if (1 == n) 				//如果拿到锁,则标记返厍
            {
                acquired = true;
            }
            else 
            {
                try 
                {
                    Thread.sleep(20);
                }
                catch (InterruptedException ignored) 
                {
                	
                }
            }
        }
        
        if(acquired)
        	return identifier;
        else
        	return null;
	}
	
	/**
	 * @param connection
	 * @param lockName 要释放锁的key
	 * @param identifier 要释放锁的标识，要释锁时，校验下是否属于这个锁的。
	 * @return
	 */
	public boolean unlock(Jedis connection, String lockName, String identifier)
	{
		lockName = LOCK_NAME + lockName;
		
		List<String> keys = Collections.singletonList(lockName);
        List<String> argv = Collections.singletonList(identifier);
        return 1 == (long) connection.eval(UN_LOCK_SCRIPT, keys, argv);
	}
	
	
	/**
	 * 这个锁没有lua脚本
	 * @param connection
	 * @param acquireTimeOut 请求时长，如果没有拿到锁，则等待的时长(单位毫秒)。
	 * @return 返回这个锁唯一字符串标识
	 */
	public String lock1(Jedis jedis , String lockName , long acquireTimeOut) 
	{
		if(acquireTimeOut <= 0)
			acquireTimeOut = 10;
		
		lockName = LOCK_NAME + lockName;
		
	    boolean success = false;			// 1. 通过SETNX试图获取一个lock
	    
	    String identifier = UUID.randomUUID().toString();
	    long acquireTimeEnd = System.currentTimeMillis() + acquireTimeOut;
	    
	    while (!success && (System.currentTimeMillis() < acquireTimeEnd)) 			//在请求的时间内循环,是否能拿到锁
        {
	    	String ques = jedis.set(lockName, identifier, "NX", "EX", LOCK_EXPIRE);			//设置锁key，NX只在键不存在时，才对键进行设置操作 , EX 设置键的过期时间为 second 秒
	    	
	    	if("OK".equals(ques))
	 	    {
	 	    	success = true;
	 	    }
	 	    else						//SETNX失败，说明锁仍然被其他对象保持，检查其是否已经超时 
	 	    {
	 	    	try 
                {
                    Thread.sleep(20);
//                    System.out.print(Thread.currentThread().getId() + ":等待拿锁 |");
                }
                catch (InterruptedException ignored) 
                {
                	
                }
	 	    }     
        }
	   
	    
	    if(success)
	    	return identifier;
	    else
	    	return null;
	}
	 
	/**
	 * @param jedis
	 * @param lockName 要释放锁的key
	 * @param identifier 要释放锁的标识，要释锁时，校验下是否属于这个锁的。
	 * @return
	 */
	public boolean unlock1(Jedis jedis , String lockName , String identifier) 
	{
		lockName = LOCK_NAME + lockName;
		
		String value = jedis.get(lockName);
		if(StringTools.empty(value))
			return false;
		
		if(identifier.equals(value))
		{
			jedis.del(lockName);   
	    	return true;
	    }
	    
	    return false;
	}
	
	
	/**
	 * @param locaName 锁的key 
	 * @param acquireTimeout 获取超时时间 
	 * @param timeout 锁的超时时间 
	 * @return 锁标识 
	 */
	private String lockWithTimeout(String lockName, long acquireTimeout, long timeout) 
	{ 
		Jedis conn = null; String retIdentifier = null; 
		try 
		{ 
			
			MyJedisPool myJedisPool = RedisPool.getJedisPool(lockName);
			Jedis jedis = myJedisPool.getResource();
			
			// 随机生成一个value 
			String identifier = UUID.randomUUID().toString(); 
			// 锁名，即key值 
			String lockKey = "lock:" + lockName; 
			// 超时时间，上锁后超过此时间则自动释放锁
			int lockExpire = (int)(timeout / 1000); 
			// 获取锁的超时时间，超过这个时间则放弃获取锁 
			long end = System.currentTimeMillis() + acquireTimeout; 
			
			while (System.currentTimeMillis() < end) 
			{ 
				if (conn.setnx(lockKey, identifier) == 1) 
				{ 
					conn.expire(lockKey, lockExpire); 
					// 返回value值，用于释放锁时间确认
					retIdentifier = identifier; 
					return retIdentifier; 
				} 
				// 返回-1代表key没有设置超时时间，为key设置一个超时时间 
				if (conn.ttl(lockKey) == -1) 
				{ 
					conn.expire(lockKey, lockExpire); 
				} 
				try 
				{ 
					Thread.sleep(10); 
				} 
				catch(InterruptedException e) 
				{ 
					Thread.currentThread().interrupt(); 
				}
			}
		}
		catch (JedisException e) 
		{ 
			e.printStackTrace(); 
		} 
		finally 
		{ 
			if (conn != null) 
			{ 
				conn.close(); 
			} 
		} 
		return retIdentifier; 
	} 
	
	/**
	 *	释放锁 
	 * @param lockName 锁的key 
	 * @param identifier 释放锁的标识
	 * @return 
	 */
	private boolean releaseLock(String lockName,String identifier)
	{ 
		Jedis conn = null; String lockKey = "lock:" + lockName;
		boolean retFlag = false; 
		try 
		{ 
			MyJedisPool myJedisPool = RedisPool.getJedisPool(lockName);
			Jedis jedis = myJedisPool.getResource();
			while (true) 
			{ 
				// 监视lock，准备开始事务 
				conn.watch(lockKey); 
				// 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁 
				if (identifier.equals(conn.get(lockKey))) 
				{ 
					Transaction transaction = conn.multi(); 
					transaction.del(lockKey); 
					List<Object> results = transaction.exec(); 
					if (results == null) 
					{ 
						continue; 
					} 
					retFlag = true; 
				} 
				conn.unwatch(); break; 
			} 
		}
		catch (JedisException e) 
		{ 
			e.printStackTrace(); 
		} 
		finally
		{ 
			if (conn != null) 
			{ 
				conn.close(); 
			} 
		} 
		return retFlag; 
	}
	
	
}
