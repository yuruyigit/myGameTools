package game.tools.http;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.alibaba.fastjson.JSONObject;

public class HttpsClient 
{
	private static final X509TrustManager DEFAULT_TRUST_MANAGER = new X509TrustManager() 
	{
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {        }
        @Override
        public X509Certificate[] getAcceptedIssuers() {            return null;        }
    };
    
	
	public static HttpsURLConnection getHttpsURLConnection(String uri , String method) throws IOException 
	{
        SSLContext ctx = null;
        
        try 
        {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new TrustManager[] {DEFAULT_TRUST_MANAGER}, new SecureRandom());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        SSLSocketFactory ssf = ctx.getSocketFactory();
        
        URL url = new URL(uri);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        httpsConn.setHostnameVerifier(new HostnameVerifier() 
        {
            @Override
            public boolean verify(String arg0, SSLSession arg1) 
            {
                return true;
            }
        });
        
        httpsConn.setRequestMethod(method);
        
        httpsConn.setDoInput(true);
        httpsConn.setDoOutput(true);
        httpsConn.setUseCaches(false);
        httpsConn.setConnectTimeout(10000);
        
        return httpsConn;
    }
	
	
	public static String readHttpsURLConnectionContent(HttpsURLConnection conn)
	{
		StringBuffer buffer = null;
		try 
		{
			//读取服务器端返回的内容
			InputStream is = null;
			
			if(conn.getResponseCode() >= 400)
				is = conn.getErrorStream();
			else
				is = conn.getInputStream();
			
			InputStreamReader isr = new InputStreamReader(is,"utf-8");
			BufferedReader br = new BufferedReader(isr);
			buffer = new StringBuffer();
			String line=null;
			while((line=br.readLine())!=null)
			{
				buffer.append(line);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		if(buffer != null)
			return buffer.toString();
		return null;
	}
	
	
	
	public static void writerHttpsURLConnectionContent(HttpsURLConnection conn , String writerContent)
	{
		try 
		{
			if(null != writerContent)
			{
				OutputStream os=conn.getOutputStream();
				os.write(writerContent.getBytes("utf-8"));
				os.flush();
				os.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param requestUrl 请求地址、
	 * @param method  GET/POST请求
	 * @param writeContent 要写入的内容
	 * @param headerProperty 协议头的属性
	 * @return [请求返的状态码, 返回的内容]
	 */
	public static Object[] httpsRequest(String requestUrl, String method , String writeContent , String ... headerProperty)
	{
		String content = null;
		
		HttpsURLConnection conn = null;
		
		try 
		{
			conn = HttpsClient.getHttpsURLConnection(requestUrl , method);
			
			for (int i = 0; i < headerProperty.length; i++) 
				conn.setRequestProperty(headerProperty[i],headerProperty[++i]);
			
			HttpsClient.writerHttpsURLConnectionContent(conn, writeContent);
			
			content = HttpsClient.readHttpsURLConnectionContent(conn);
			
			return new Object[] { conn.getResponseCode() , content};
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static String httpsRequestContent(String requestUrl, String method , String writeContent , String ... headerProperty)
	{
		return (String)httpsRequest(requestUrl , method , writeContent , headerProperty)[1];
	}
	
	
	private static final String ONESTORE_DEV_URL = "https://sbpp.onestore.co.kr";
	private static final String ONESTORE_PUBLISH_URL = "https://apis.onestore.co.kr";
	
	
	public static void main(String[] args)  throws Exception
	{
		String host = ONESTORE_PUBLISH_URL;
		if(args.length >= 1)
			if("dev".equals(args[0]))
				host = ONESTORE_DEV_URL;
				
		String result = HttpsClient.httpsRequestContent(host+"/v2/oauth/token", 
				"POST" , 
				"grant_type=client_credentials&client_id=com.monawa.hyunwkum&client_secret=Z0PWRVoXafxfYUdo5YkkYkL/qAGT456wlKY90LkXLwA=",  
				"Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		System.out.println("result1 = " + result);
		
		if(result != null)
		{
			JSONObject o = JSONObject.parseObject(result);
			
			if(o.getString("status").equals("SUCCESS"))
			{
				String accessToken = o.getString("access_token");
				
				result = (String)HttpsClient.httpsRequest(host+"/v2/purchase/details/SANDBOX3000000035082/com.monawa.hyunwkum",
						"GET" , 
						null,
						"Authorization", accessToken,
						"Content-Type", "application/json")[1];
				
			}
		}
	}
}
