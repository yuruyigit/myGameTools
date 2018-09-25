package game.tools.redis;

import java.util.Map;

/**
 * <pre>
 * 参数名类型
 * 
 * String key , value , id
 * long start ,end 
 * double score , min , max
 * </pre>
 *  
 * @author zhibing.zhou
 *
 */
public enum RedisCmd 
{
	
	
	/** 获取所在key的jedis对象 (key)*/
	shard,   
	
	keys,
	
	/** 设置对应key的过期时间 */
	expire,
	
//	////////////////////////////////////////对key字符串操作//////////////////////////////////////////
	/** 是否存在这个key , (key)  */
	exists,
	/** 删除这个key (key) */
	del, 
	/**返回key的自增，该函数为安全型函数 (key)*/
	incr,				//返回对键的自增
	
	/**设置字符串 (key , value)*/
	set,
	/**获取字符串  (key)*/
	get,			//设置 , 获取 字符串
	/**redis的所有键迭代器 */
	scan,
	
	//////////////////////////////////////////有序集合操作//////////////////////////////////////////
	/** 获取一个正序的ArrayList<Object[]>SortedSet（有序集合） (key , start , end )*/
	zrangeWithScores, 
	/** 获取一个倒序的ArrayList<Object[]>SortedSet（有序集合） (key , start , end )*/
	zrevrangeWithScores , 
	/** 向有序集合添加数据，(key , score , id)*/
	zadd,			//有序列表添加元素 
	/** 向有序集合添加数据，(key ,Map<String , Double>)*/
	zadds,			//有序列表一并添加多个元素， 
	/** 有序集合删除数据  ， (key , id)*/
	zrem,
	/** 有序集合删除数据  ， (key , (String[])id)*/
	zremArr,  
	/** 获取有序集合中的数据长度， (key) */
	zsize , 		
	/** 获取有序集合正序排行号, (key , id) */
	zrank , 		//有序列表获取正序排行号
	/** 获取有序集合倒序排行号, (key , id)  */
	zrevrank, 		//有序列表获取倒序排行号
	/** 移除有序集 key 中，指定排名(rank)区间内的所有成员。   (key , start , end)*/
	zremrangebyrank,
	/** 返回有序集 key 中，指定区间内的成员。(key , member)  */
	zrevrange,
	/** 获取有序集合中的数据长度， (key)  */
	zcard,
	/** 有序列表对应score区间内，有多少个元素， (key , min , max ) */
	zcount,
	
	zscore,
	
	//////////////////////////////////////////Hash操作//////////////////////////////////////////
	hset, 
	hmset, 
	hmget,
	hvals, 
	hkeys ,
	/** 返回key中的map集合中指定value属性的值 			 */
	hget, 
	hgetAll ,
	hgetAllJavaObj , 
	hmsetJavaObj,
	hdel,
	
	//////////////////////////////////////////List操作//////////////////////////////////////////
	lpush,
	lsize,		//获取list所有数据
	lget,		//获取list所有数据,同lsize一样功能
	lrem,
	lset,		//将列表 key 下标为 index 的元素的值设置为 value 。
	
	
	sadd,
	smimber,  
	smembers,
	srem, 
	
	;
	
	
}
