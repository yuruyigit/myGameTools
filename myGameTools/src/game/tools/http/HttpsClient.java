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
    
	
	public static HttpsURLConnection getHttpsURLConnection(String uri) throws IOException 
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
        
//        uri = URLEncoder.encode(uri,"utf-8");
        
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
        
        httpsConn.setRequestMethod("POST");
        httpsConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
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
			InputStream is = conn.getInputStream();
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
	
	
	/*
	 * 处理https GET/POST请求
	 * 请求地址、请求方法、参数
	 * */
	public static String httpsRequest(String requestUrl,String outputStr)
	{
		String content = null;
		try 
		{
			HttpsURLConnection conn = HttpsClient.getHttpsURLConnection(requestUrl);
			
			HttpsClient.writerHttpsURLConnectionContent(conn, outputStr);
			
			content = HttpsClient.readHttpsURLConnectionContent(conn);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return content;
	}
	
	
	public static void main(String[] args)  throws Exception
	{
		HttpsURLConnection urlConnection = HttpsClient.getHttpsURLConnection("https://apis.onestore.co.kr/api/oauth/token");
		urlConnection.setRequestProperty("Authorization","Basic[Base64(com.monawa.hyunwkum:Z0PWRVoXafxfYUdo5YkkYkL/qAGT456wlKY90LkXLwA=)]");
		urlConnection.setRequestMethod("POST");
		HttpsClient.writerHttpsURLConnectionContent(urlConnection, "{\"grant_type\":\"client_credentials\"}");
		
		String content = HttpsClient.readHttpsURLConnectionContent(urlConnection);
        System.out.println("content = " + content );
	}
}
