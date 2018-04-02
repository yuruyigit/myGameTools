package game.tools.json;

import com.alibaba.fastjson.JSONObject;

public class MyJSONObject extends JSONObject 
{
	public <T> T getValue(String key)
	{
		return (T)this.remove(key);
	}
}
