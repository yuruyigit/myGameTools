package game.tools.rpc.flow;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import game.tools.db.cache.expire.ExpireCacheDataMap;
import game.tools.http.HttpClient;
import game.tools.log.LogUtil;
import game.tools.utils.StringTools;

public class FlowRpcClient 
{
	private String address;
	
	private static final int timeout = 15 * 1000;
	
	private HttpURLConnection urlConnection;
	
	private static ExpireCacheDataMap<String,URL> EXPIRE_URL_MAP = new ExpireCacheDataMap<String,URL>();
	
	public FlowRpcClient(String address) 
	{
		this.address = "http://" + address + "/" + FlowRpcServer.ROOT_PATH + "/" + FlowServlet.class.getSimpleName();
		
		init();
	}

	private void init() 
	{
		
	}
	
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
	 * @param httpUrl
	 * @return 返回一个设置好属性的httpConnecion对象
	 * @throws Exception
	 */
	private HttpURLConnection getHttpURLConnection(String httpUrl , String method) throws Exception
	{
		URL url = getUrl(httpUrl);
		
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod(method);
		urlConnection.setUseCaches(false);
		urlConnection.setInstanceFollowRedirects(true);
		urlConnection.setConnectTimeout(timeout);  
		urlConnection.setReadTimeout(timeout);
//		urlConnection.setRequestProperty("Host","$domain");
//		urlConnection.setRequestProperty("Content-Type", "application/x-java-serialized-object");
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConnection.setRequestProperty("Connection", "Keep-Alive");
//		urlConnection.setRequestProperty("Content-Length", (param.toString().length()) + "");
//		urlConnection.connect();  
//		System.out.println("urlConnection = " + urlConnection.hashCode());
			
		return urlConnection;
	}
	
	/**
	 * @param urlConnection
	 * @return 返回从http读取出来的数据对象
	 */
	private Object readHttpObject(HttpURLConnection urlConnection) 
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
	
	
	/**
	 * 通过post把所请求的参数写入内容，不放在地址中。
	 * @param httpUrl 请求的url地址
	 * @param params 传入请求所需要的内容
	 * @return 返请求后的结果 
	 * @throws Exception
	 */
	public Object sendPost(String httpUrl, Object content) 
	{
		try
		{
//			System.out.println("request address = " + httpUrl);
			
			if(StringTools.empty(httpUrl))
				throw new NullPointerException("HttpUrl Not Empty");
			if(content == null)
				throw new NullPointerException("Content Not Empty");
		
			HttpURLConnection urlConnection = getHttpURLConnection(httpUrl , "POST");
			
			writeHttpObject(urlConnection , content);			//把对象写到url里面

			int responseCode = urlConnection.getResponseCode();
			
			if (responseCode != HttpURLConnection.HTTP_OK)
				throw new NullPointerException("Http State Code :" + responseCode);
			
			Object result = readHttpObject(urlConnection);
			
			return result;
			
		}
		catch (Exception e)
		{
			String msg = JSON.toJSONString(content);
			LogUtil.error( msg, e);
			
 			e.printStackTrace();
		}
		return null;
	}
	
	
	public Object call(Object ...params)
	{
		return sendPost(address, params);
	}
	
	public static void main(String[] args) 
	{
		FlowRpcClient flowClient = new FlowRpcClient("192.168.1.55:12345");
		
		Object [] array = {1,12,3,"张三",1.2f,"第二步，是对全国18家铁路局和3家专业运输公司（中铁集装箱运输有限责任公司、中铁特货运输有限责任公司、中铁快运股份有限公司）的公司制改革，即对运输主业的改革。对于这部分企业，中铁总正在加紧做好相关准备工作，并形成指导意见，改制方案和公司章程、议事规则范本。其中，完善企业化、市场化运行机制是这部分企业改革的指导方针，中铁总计划在2017年年底前启动直属运输企业改革。　　最后一步是改革最难的部分，即对中铁总公司的公司制改革。上述人士称，对总公司的改革原则就是要实现绝对的政企分开，目前方案正在国务院和财政部审核，待上述部门作出批复意见后方可实施推进。　　财新记者获悉，在近期中铁总举行的推进铁路公司制改革会议上，中铁总总经理陆东福表示，铁路企业公司制改革今年是起步和探索阶段，2018年要全面深化开展，目的是建立收、支、利刚性"};
		int sumCount = 0 , maxCount = 10;
		
		for (int i = 0; i < maxCount; i++) 
		{
			long startTime = System.currentTimeMillis();
			int index = 0;
			while(true)
			{
				Object o = flowClient.call(array);
				index ++;
				
				long endTime = System.currentTimeMillis();
				
				if(endTime - startTime >= 1000)
				{
					System.out.println( "method " + index + " o = " + o +  " gap = " + (endTime - startTime) );
					sumCount += index;
					break;
				}
			}
		}
		
		System.out.println("QPS : " + sumCount / maxCount);
		
//		for (int i = 0; i < 100; i++) 
//		{
//			long startTime = System.currentTimeMillis();
//			
//			Object o = flowClient.method(array);
//
//			long endTime = System.currentTimeMillis();
//			
//			System.out.println( "method " + i + " o = " + o +  " gap = " + (endTime - startTime));
//		}
	}
	
}
