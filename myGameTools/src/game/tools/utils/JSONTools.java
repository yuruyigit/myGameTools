package game.tools.utils;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class JSONTools 
{
	public static JSONObject getJsonObject(String value)
	{
		JSONObject jsonObject = null;
		try
		{
			char jsonStart = value.charAt(0);
			char jsonEnd = value.charAt(value.length() - 1);
			
			if(jsonStart == '{' && jsonEnd == '}') 
				jsonObject = JSONObject.parseObject(value);
			
//			if(value.indexOf("{") >= 0 && value.lastIndexOf("}") > 0)
//				jsonObject = JSONObject.parseObject(value);
		} 
		catch (Exception e) 
		{
			jsonObject = null;
		}
		
		return jsonObject;
	}
	
	public static <T> ArrayList<T> jsonToList(String jsonString , Class<T> clzss)
	{
		return JSON.parseObject(jsonString, new TypeReference<ArrayList<T>>(){});
	}
	
	public static <K,V> ConcurrentHashMap<K, V> jsonToMap(String jsonString , Class<V> clzss)
	{
		return JSON.parseObject(jsonString, new TypeReference<ConcurrentHashMap<K,V>>(){});
	}
	
	public static <T> T jsonToObject(String jsonString , Class<T> clzss)
	{
		return JSON.parseObject(jsonString, new TypeReference<T>(){});
	}
	
	public static void main(String[] args) 
	{
		JSONObject o = JSONTools.getJsonObject("{'a':1");
		System.out.println(o);
	}
}
