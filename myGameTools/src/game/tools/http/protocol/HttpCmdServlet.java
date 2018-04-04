package game.tools.http.protocol;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;
import game.tools.http.HttpClient;
import game.tools.http.HttpPackage;
import game.tools.http.HttpServer;
import game.tools.log.LogUtil;
import game.tools.net.netty4.protocol.Netty4Protocol;
import game.tools.utils.ClassUtils;

/**
 * @author zhibing.zhou
 * http的post请求servlet<br/>
 * 该servlet使用@HttpCmd(cmdNo = 123)注解来绑定对应的httpcmd处理函数，<br/>
 * cmdNo则为对应的处理编号，函数传入的参数为httpPkage对象。<br/>
 * 如：
 * <pre>
 * HttpCmd(cmdNo = 123)
 * public static Object httpCmd1(HttpPackage pkg)
 * {
 * 	 System.out.println("pkg = " + pkg);
 *	 return "httpCmd1";
 * }
 * </pre>
 */
public class HttpCmdServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 协议处理函数集合 */
	private static final HashMap<Integer , Method> protocolHandlerMap = new HashMap<Integer , Method>();
	
	public HttpCmdServlet() 
	{
		initMethod();
	}
	
	private void initMethod()
	{
		try 
		{
			Set<String> setString = ClassUtils.getClassName("game.tools.http", true);
			
			for (String string : setString) 
			{
				Class clzss = ClassLoader.getSystemClassLoader().loadClass(string);
				Method[] methodArray = clzss.getDeclaredMethods();
				
				for (Method method : methodArray) 
				{
					Annotation annot = method.getAnnotation(HttpCmd.class);

					if(annot != null)
					{
						HttpCmd cmd = ((HttpCmd)annot);
						
						if(protocolHandlerMap.containsKey(cmd.cmdNo()))
						{
							throw new Exception("HttpCmdServlet : duplicate httpCmd key !! at " + cmd.cmdNo());
						}
						
						protocolHandlerMap.put(cmd.cmdNo(), method);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		doCmd(req, resp);
	}

	
	private void doCmd(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		try 
		{
			HttpPackage httpPkg = (HttpPackage)HttpClient.readHttpObject(req);
			
			if(httpPkg == null)
			{
				throw new Exception("post httpPkg null , maybe not post request ! !  !");
			}
			
			int httpCmd = httpPkg.get();
			
			Method httpCmdMethod = protocolHandlerMap.get(httpCmd);
			
			if(httpCmdMethod == null)
				throw new Exception("httpCmd :" + httpCmd + " not Method.");
			
			Object result = httpCmdMethod.invoke(null, httpPkg);
			
			HttpClient.writeHttpObject(resp, result);
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
}
