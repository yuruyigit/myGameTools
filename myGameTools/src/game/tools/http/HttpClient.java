package game.tools.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import game.tools.db.cache.expire.ExpireCacheDataMap;
import game.tools.log.LogUtil;
import game.tools.utils.StringTools;

public class HttpClient 
{
	private static final String MSG_ENCODE = "utf-8";
	
	private static final int timeout = 30 * 1000;
	
	private static final ExpireCacheDataMap<String,URL> EXPIRE_URL_MAP = new ExpireCacheDataMap<String,URL>();
	
	
	/**
	 * @param urlString
	 * @return 返回一个url对象
	 * @throws Exception
	 */
	private URL getUrl(String urlString) throws Exception
	{
		URL url = EXPIRE_URL_MAP.get(urlString);
		
		if(url == null)
		{
			url = new URL(urlString);
			EXPIRE_URL_MAP.put(urlString, url);
		}
		
		return url;
	}
	
	/**
	 * @param params
	 * @return 返回拼接好的参数字符串
	 * @throws Exception
	 */
	private String getUrlParamString(Object ... params) throws Exception
	{
		if (params != null) 
		{
			if(params.length % 2 != 0)
			{
				System.err.println("ParamArray Key-Val Not Align");
				return null;
			}
			
			StringBuffer webAddr = new StringBuffer();
			
			for (int i = 0; i < params.length; i++) 
			{
				if(i == 0)
					webAddr.append("?");
				else
					webAddr.append("&");
				
				String key = params[i].toString();
				String val = params[++i].toString();
				
				webAddr.append(key);
				webAddr.append("=");
				
				webAddr.append(URLEncoder.encode(val, "UTF-8"));
			}
			
			return webAddr.toString();
		}
		return null;
	}
	
	
	private String getContent(HttpURLConnection urlConnection) throws Exception
	{
		StringBuffer content = new StringBuffer();
		InputStream	in = urlConnection.getInputStream();
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, MSG_ENCODE));
		while (bufReader.ready())
			content.append(bufReader.readLine());
		bufReader.close();

		return content.toString();
	}
	
	/**
	 * @param httpUrl
	 * @return 返回一个设置好属性的httpConnecion对象
	 * @throws Exception
	 */
	private HttpURLConnection getHttpURLConnection(String httpUrl , String method) throws Exception
	{
		URL url = getUrl(httpUrl);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod(method);
		urlConnection.setUseCaches(false);
		urlConnection.setInstanceFollowRedirects(true);
		urlConnection.setConnectTimeout(timeout);  
		urlConnection.setReadTimeout(timeout);
		// urlConnection.setRequestProperty("Host","$domain");
//		urlConnection.setRequestProperty("Content-Type", "application/x-java-serialized-object");
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConnection.setRequestProperty("Connection", "Keep-Alive");
		//urlConnection.setRequestProperty("Content-Length", (param.toString().length()) + "");
		
//		urlConnection.connect();  
		 
//		System.out.println("urlConnection = " + urlConnection.hashCode());
		return urlConnection;
	}
	
	/**
	 * @param urlConnection
	 * @return 返回从http读取出来的数据对象
	 */
	public static Object readHttpObject(HttpURLConnection urlConnection) 
	{
		InputStream in = null;
		ObjectInputStream ois = null;
		Object obj = null;
		try 
		{
			in = urlConnection.getInputStream();
			ois = new ObjectInputStream(in);
			obj = ois.readObject();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (ois != null) 
				{
					ois.close();
					ois = null;
				}
				if (in != null) 
				{
					in.close();
					in = null;
				}
			}
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		}

		return obj;
	}
	
	/**
	 * @param urlConnection
	 * @param content
	 * @return 返回是否成功的写入http中
	 * @throws Exception
	 */
	private boolean writeHttpObject(HttpURLConnection urlConnection , Object content) throws Exception
	{
		if (content != null)
		{
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(urlConnection.getOutputStream()));  
			out.writeObject(content);
			out.flush();
			out.close();
			return true;
		}
		
		return false;
	}
	
	public static void writeHttpObject(HttpServletResponse resp , Object result) throws Exception
	{
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(resp.getOutputStream()));  
		out.writeObject(result);
		out.flush();
		out.close();
	}
	
	
	public static Object readHttpObject(HttpServletRequest req) throws Exception 
	{
		ObjectInputStream in = new ObjectInputStream( new BufferedInputStream(req.getInputStream()));  
		Object o = in.readObject();
		in.close();
		
		return o;
	}
	
	
	
	/**
	 * @param httpUrl http地址
	 * @param paramArray key,val,key,val,key,val 对应的键-值数组
	 * @return 
	 */
	public String sendGet(String httpUrl, Object ...paramArray) 
	{
		try 
		{
			String paramUrl = getUrlParamString(paramArray);
			
			httpUrl = httpUrl + paramUrl;

			HttpURLConnection urlConnection = getHttpURLConnection(httpUrl , "GET");

			return getContent(urlConnection);
		}
		catch (Exception e) 
		{
			String msg = JSON.toJSONString(paramArray);
			LogUtil.error(msg , e);
			
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 通过post把所请求的参数写入内容，不放在地址中。
	 * @param httpUrl 请求的url地址
	 * @param params 传入请求所需要的内容
	 * @return 返请求后的结果 
	 * @throws Exception
	 */
	public Object sendPost(String httpUrl, Object ...paramArray) 
	{
		HttpPackage httpPackage = new HttpPackage(paramArray);
		
		Object params = httpPackage;
		
		try
		{
			if(StringTools.empty(httpUrl))
				throw new NullPointerException("HttpUrl Not Empty");
			if(params == null)
				throw new NullPointerException("Content Not Empty");
			
			HttpURLConnection urlConnection = getHttpURLConnection(httpUrl , "POST");
			
			writeHttpObject(urlConnection , params);			//把对象写到url里面
			
			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				throw new NullPointerException("Http State Code :" + urlConnection.getResponseCode());

			return readHttpObject(urlConnection);
			
		}
		catch (Exception e)
		{
			String msg = JSON.toJSONString(params);
			LogUtil.error( msg, e);
			
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) 
	{
//		HttpHashMap<String, Object> params = new HttpHashMap<String, Object>(1);
//		
//		params.put("a", "vala");
//		params.put("b", "valb");
//		params.put("c", "valc");
//		
//		String msg = JSON.toJSONString(params);
//		
//		HttpClient hc = new HttpClient();
//		Object o = hc.sendGet("http://www.baidu.com/s", "wd" , "zzb");
//		Object o1 = hc.sendGet("http://www.baidu.com/s", "wd" , "zzb");
//		System.out.println();
		
		HashMap<String, String> stringMap = new HashMap<>();
		
		for (int i = 0; i < 10111110; i++) 
		{
			String string = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 11);
			if(stringMap.containsKey(string))
				System.out.println("string  = " + string + " exist ");
//			System.out.println(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 11));
			stringMap.put(string, string);
		}
		
		System.out.println("OK" + stringMap.size());
	}
}
