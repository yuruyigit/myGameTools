package game.tools.http;
import javax.servlet.http.HttpServletRequest;

public class HttpTools
{
	private static final HttpClient HTTP = new HttpClient();
	
	/**
	 * @param httpUrl http地址
	 * @param paramArray key,val,key,val,key,val 对应的键-值数组
	 * @return 
	 */
	public static <T> T sendGet(String httpUrl , Object ...paramArray)
	{
		return (T)HTTP.sendGet(httpUrl, paramArray);
	}
	
	/**
	 * @param httpUrl http地址
	 * @param paramArray val,val,val 对应的值数组
	 * @return 
	 */
	public static <T> T sendPost(String urlString , Object ...paramArray)
	{
		return (T)HTTP.sendPost(urlString, paramArray);
	}
	
	public static void main(String[] args) 
	{
		HttpTools.sendGet("www.baidu.com", 
				"a","1",
				"b","2");
	}
	
	public static Object readHttpObject(HttpServletRequest req) throws Exception 
	{
		return HttpClient.readHttpObject(req);
	}
	
}
