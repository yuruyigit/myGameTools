package game.tools.db.mybatis.plush.transction;
import java.util.List;
import com.alibaba.fastjson.JSONObject;

import game.tools.log.LogUtil;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;
import game.tools.utils.UUIDTools;

public class MybatisTransaction 
{
	public static String start()
	{
		String uuid  = UUIDTools.getUUid();

		return start(uuid);
	}
	
	public static String start(String transctionId)
	{
		return MybatisTransactionHandler.transction(transctionId);
	}
	
 
	/**  事务结束	 */
	public static void end()
	{
		String transctionId = MybatisTransactionHandler.getTransctionId();											//获取当前线程事务ID
		
		if(transctionId != null)
			RedisOper.execute(RedisCmd.del, MybatisTransactionHandler.getTransctionKey(transctionId));
	}
	
	public static boolean rollback()
	{
		String transctionId = MybatisTransactionHandler.getTransctionId();
		
		return rollback(transctionId);
	}
	
	/**  事务回滚	 */
	public static boolean rollback(String transctionId) 
	{
		try 
		{
			List<String> jsonList = RedisOper.execute(RedisCmd.lget, MybatisTransactionHandler.getTransctionKey(transctionId));
			
			String rollbackKey = null;
			
			JSONObject o = null;
			
			for (String jsonString : jsonList) 
			{
				o = JSONObject.parseObject(jsonString);
				
				rollbackKey = MybatisTransactionHandler.getTransactionRollBackKey(o.getString("serverSign"));
				
				JSONObject json = new JSONObject();
				json.put("mysqlUrl",o.getString("mysqlUrl"));
				json.put("rollbackSql",o.getString("rollbackSql"));
				
				RedisOper.execute(RedisCmd.lpush, rollbackKey, json.toJSONString());
			}
			
			RedisOper.execute(RedisCmd.del, MybatisTransactionHandler.getTransctionKey(transctionId));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
			return false;
		}
		finally 
		{
			MybatisTransactionRollback.start();
		}
		
		return true;
	}
	

}
