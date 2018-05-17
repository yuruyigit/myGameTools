package game.tools.http.protocol;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import game.tools.http.HttpClient;
import game.tools.log.LogUtil;
import game.tools.method.IMethodNo;
import game.tools.method.MethodInvokeTools;
import game.tools.method.MethodObject;

/**
 * @author zhibing.zhou
 * http的post请求servlet<br/>
 * 该servlet使用@HttpCmd(cmdNo = 123)注解来绑定对应的httpcmd处理函数，<br/>
 * cmdNo则为对应的处理编号，函数传入的参数为httpPkage对象。<br/>
 * <b>默认使用第一个参数为处理编号</b>
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
	
	private static final long serialVersionUID = 1L;
	/** 协议处理函数集合 */
	private static HashMap<Integer , MethodObject> HTTP_CMD_METHOD_MAP;
	/** 扫描函数包的路径 */
	private static String SCAN_CLASS_PACKAGE;
	
	private static Object lock = new Object();
	
	/**
	 * 设置扫描函数包的路径，<b>注意：只有初次才有效，如二次设置，则直接丢弃。</b>
	 * @param scanClassPackage 要扫描的执行的函数包路径
	 */
	public static void setHttpCmdServletScanClassPackage(String scanClassPackage)
	{
		if(HttpCmdServlet.SCAN_CLASS_PACKAGE == null)
		{
			synchronized (lock) 
			{
				if(HttpCmdServlet.SCAN_CLASS_PACKAGE == null)
				{
					HttpCmdServlet.SCAN_CLASS_PACKAGE = scanClassPackage;
					
					initInvokeMethod();
				}
			}
		}
	}
	
	/**
	 * 初始化调用的方法
	 */
	private static void initInvokeMethod()
	{
		HTTP_CMD_METHOD_MAP  = MethodInvokeTools.getInvokeMethod(HttpCmdServlet.SCAN_CLASS_PACKAGE, HttpCmd.class, new IMethodNo() 
		{
			public int getMethodNo(Annotation annot) 
			{
				HttpCmd cmd = ((HttpCmd)annot);
				return cmd.cmdNo();
			}
		});
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
			if(HTTP_CMD_METHOD_MAP.isEmpty())
				throw new Exception("HTTP_CMD_METHOD_MAP isEmpty !");
			
			Object []  paramArray = (Object [])HttpClient.readHttpObject(req);
			
			if(paramArray == null)
			{
				throw new Exception("post paramArray null , maybe not post request ! !  !");
			}
			
			HttpPackage httpPkg = new HttpPackage(req , resp , paramArray);
			
			int httpCmd = httpPkg.get();
			
			MethodObject methodObject = HTTP_CMD_METHOD_MAP.get(httpCmd);
			
			if(methodObject == null)
				throw new Exception("httpCmd :" + httpCmd + " not Method.");
			
			Method method = methodObject.getMethod();
			method.setAccessible(true);
			
			Object result = method.invoke(methodObject.getObject(), httpPkg);
			
			HttpClient.writeHttpObject(resp, result);
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
}
