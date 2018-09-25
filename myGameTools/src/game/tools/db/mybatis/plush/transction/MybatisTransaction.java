package game.tools.db.mybatis.plush.transction;
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
			RedisOper.execute(RedisCmd.del, MybatisTransactionHandler.getTransctioningKey(transctionId));
	}
	
	public static boolean rollback()
	{
		String transctionId = MybatisTransactionHandler.getTransctionId();
		
		return rollback(transctionId);
	}
	
	/**  事务回滚	 */
	public static boolean rollback(String transctionId) 
	{
		return MybatisTransactionHandler.rollback(transctionId);
	}
	

}
