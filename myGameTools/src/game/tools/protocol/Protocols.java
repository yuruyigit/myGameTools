package game.tools.protocol;


import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

class PlayControl
{
	
	private String name;
	
	public PlayControl(String name) 
	{
		this.name = name;
	}
	
	public void m111(JSONObject o )
	{
		System.out.println(this.name + " m111 " + o);
	}
	
	
	public void m222(JSONObject o )
	{
		System.out.println(this.name + " m222 " + o);
	}
}

/**
 * @author zzb
 */
public class Protocols 
{
	
	private static final HashMap<Integer, IProtocol<PlayControl , JSONObject>> MSG_MAP = new HashMap<Integer, IProtocol<PlayControl , JSONObject>>();
	
	public static void registerProtocol(int protocolNo , IProtocol<PlayControl , JSONObject> protocol)
	{
		MSG_MAP.put(protocolNo, protocol);
	}
	
	
	public static IProtocol<PlayControl , JSONObject> getProtocol(int protocolNo)
	{
		return MSG_MAP.get(protocolNo);
	}
	
	
	public static void main(String[] args) 
	{
		PlayControl playControlObject = new PlayControl("张三1");
		JSONObject  json = new JSONObject();
		json.put("name", "zzb");
		
		registerProtocol(111, (playControl , o) -> { playControl.m111(o);});
		registerProtocol(222, (playControl , o) -> { playControl.m222(o);});
		
		IProtocol p = getProtocol(111);
		p.doProtocol(playControlObject, json);
	}
}
