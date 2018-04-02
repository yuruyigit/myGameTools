package game.tools.println;
import com.alibaba.fastjson.JSONObject;
import game.tools.log.LogUtil;
import game.tools.utils.DateTools;

/**
 * 工具类，一般通用的工具都在这里
 * @author zzb 2014-4-9 上午11:47:35
 * 
 */
public class ProtocolPrintln 
{

	
	public static final String PROTOCOL_KEY_THREAD_ID = "#threadId";
	public static final String PROTOCOL_KEY_REV_TIME= "#revTime";
	public static final String PROTOCOL_KEY_CONTENT = "#protocolContent";
	
	
	/**
	 * 打印通道信息(不打印心跳)
	 * zzb 
	/**
	 * <pre>
	 * @param paramArray 
	 * [
	 * 		boolean:是否是发送消息,
	 * 		long:用户ID,
	 * 		long:角色ID,
	 * 		int:通道ID，
	 * 		String:发送的字符串内容,
	 * 		int:数据体的byte数据长度
	 * 		long:数据执行使用时间
	 * ]
	 * </pre>
	 */
	public static String protocolPrintln(Object ...paramArray)
	{
		int index = 0;
		
		boolean send = (boolean) paramArray[index++];
		String userId = (String)paramArray[index++];
		long roleId = (long)paramArray[index++];
		int channeId = (int)paramArray[index++];
		JSONObject o = (JSONObject)paramArray[index++];
		long useTime = -1;
		
		Long revTime = (Long)o.remove(ProtocolPrintln.PROTOCOL_KEY_REV_TIME);
		if(revTime != null)
			useTime = (long)DateTools.getCurrentTimeLong() - (long)revTime;
		
		Long threadId = (Long)o.remove(ProtocolPrintln.PROTOCOL_KEY_THREAD_ID);
		if(threadId == null)
			threadId = Thread.currentThread().getId();
				
		String msgContent = (String)o.remove(ProtocolPrintln.PROTOCOL_KEY_CONTENT);
		if(msgContent == null)
			msgContent = o.toJSONString();
		
		
		String size = getByteSizeString(msgContent.getBytes().length);
		
		String currentTime = DateTools.getCurrentTimeMSString();
		
		String content = null;
		
		if(send)		//发送时打印
		{
			content = currentTime +" ========>>|"+userId+"|"+roleId+"|"+channeId+"|"+threadId+"|"+useTime+"|"+size+"| Send Content " + msgContent;
			System.err.println(content);
		}
		else		//接收时打印
		{
			content = currentTime +" <<========|"+userId+"|"+roleId+"|"+channeId+"|"+threadId+"|"+useTime+"|"+size+"| Revc Content " + msgContent;
			System.out.println(content);
			
			o.put(ProtocolPrintln.PROTOCOL_KEY_THREAD_ID, threadId);							//如何是接收消息的话，则记录当前执行的线程ID
			o.put(ProtocolPrintln.PROTOCOL_KEY_REV_TIME, System.currentTimeMillis());			//记录接收协议时间戳
		}
		
		LogUtil.protocol(content);
		
		return content;
	}
	
	
	private static String getByteSizeString(long byteSize)
	{
		String size = null;
		
		if(byteSize < 1000)
			size = byteSize + "b";
		else if(byteSize >= 1000 && byteSize < 1000000)
			size = (byteSize / 1000) + "kb";
		else if(byteSize >= 1000000)
			size = (byteSize / 1000) + "mb";
		
		return size;
	}

}

