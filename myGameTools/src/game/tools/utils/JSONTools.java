package game.tools.utils;

import com.alibaba.fastjson.JSONObject;

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
	
	public static void main(String[] args) 
	{
		JSONObject o = JSONTools.getJsonObject("{'a':1}");
		System.out.println(o);
	}
}
