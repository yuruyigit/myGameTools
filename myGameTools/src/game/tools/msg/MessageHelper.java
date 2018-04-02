package game.tools.msg;

import com.alibaba.fastjson.JSONObject;

import game.tools.error.ErrMessage;

public class MessageHelper 
{

	public static JSONObject getErrPkg(int errNo )
	{
		JSONObject o  = getPkg();
		o.put(ErrMessage.EC, errNo);
		return o;
	}	
	
	private static JSONObject getPkg()
	{
		JSONObject o = new JSONObject();
		return o;
	}
}
