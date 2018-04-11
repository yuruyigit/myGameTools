package game.tools.http;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsClient 
{
	private static class DefaultTrustManager implements X509TrustManager 
	{
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
	
	private static HttpsURLConnection getHttpsURLConnection(String uri) throws IOException 
	{
        SSLContext ctx = null;
        
        try 
        {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
        } 
        catch (KeyManagementException e) 
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) 
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
        
        httpsConn.setDoInput(true);
        httpsConn.setDoOutput(true);
        
        return httpsConn;
    }
	
	public static void main(String[] args) 
	{
//		HttpsURLConnection conn = HttpsClient.getHttpsURLConnection("https://www.baidu.com");
//		String content = HttpsClient.httpsRequest("http://www.baidu.com/s?wd=1,","get","fsssss");
		
//		System.out.println("content = " + content);
	}
}
