package game.tools.http;

import java.util.HashMap;
import java.util.Map;

import game.tools.keys.Keys;

public class HttpTools
{
	private static final HttpClient HTTP = new HttpClient();
	
	
	public static <T> T sendGet(String httpUrl , String ...paramArray)
	{
		return (T)HTTP.sendGet(httpUrl, paramArray);
	}
	
	
	public static <T> T sendPost(String httpUrl,HttpHashMap<String , Object> params)
	{
		return (T)HTTP.sendPost(httpUrl, params);
	}
	
	public static <T> T sendPost(String urlString , Object ...paramArray)
	{
		if(paramArray != null)
		{
			if(paramArray[1] instanceof Object [])
			{
				Object [] valueParamArray = (Object [])paramArray [1];
				
				HttpHashMap<String, Object> paramMap = new HttpHashMap<>();
				for (int i = 0; i < valueParamArray.length;) 
				{
					if(i == 0 )
						paramMap.put(paramArray[0].toString(), valueParamArray[i++]);
					else
						paramMap.put(valueParamArray[i++].toString(), valueParamArray[i++]);
				}
				return (T)HTTP.sendPost(urlString, paramMap);
			}
			else
			{
				if(paramArray.length % 2 != 0)
				{
					System.err.println("paramArray key-val not align");
					return null;
				}
				
				int size = paramArray.length /2;
				
				HttpHashMap<String, Object> params = new HttpHashMap<>(size);
				for (int i = 0; i < paramArray.length;) 
				{
					params.put(paramArray[i++].toString(), paramArray[i++]);
				}
				return (T)HTTP.sendPost(urlString, params);
			}
			
		}
		else
		{
			System.err.println("paramArray is null");
			return null;
		}
	}
	
	

	/**
	 * @param paramArray [[key],[val],[key],[val]]
	 * @return 返回对应的key-val的map
	 */
	public final static Map<String, Object> getArrayToMap(Object ...paramArray)
	{
		Map<String, Object> paramMap = null;
		if(paramArray == null)
			return paramMap;
		
		paramMap = new HashMap<>(5);
		paramMap.put("protocolNo", paramArray[0]);
		
		if(paramArray.length > 1)
		{
			String key = null;
			Object value = null;
			for (int i = 1; i < paramArray.length;i++) 
			{
				key = paramArray[i].toString();
				i ++ ;
				value = paramArray[i]; 
				
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}


	public static <T> T sendPostNotKey(String httpUrl, int protocolNo, Object[] params) 
	{
		HttpHashMap<String, Object> paramMap = new HttpHashMap<String, Object>();
		paramMap.put(Keys.PROTOCOL_NO, protocolNo);
		for (int i = 0; i < params.length; i++) 
		{
			paramMap.put(""+i, params[i]);
		}
		return (T)HTTP.sendPost(httpUrl, paramMap);
	}
	
}
