package game.tools.http;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import game.tools.log.LogUtil;

public abstract class HttpCmd 
{
	protected static final String DEFAULT_DO_CMD = "default doCmd";
	protected static final ExecutorService CMD_THREAD_POOL = Executors.newFixedThreadPool(100);
	
	public void doCmd(HttpServletRequest req, HttpServletResponse resp , HttpPackage httpPkg) throws Exception 
	{
		try 
		{
			Object result = executeCmd(httpPkg);
			
			HttpClient.writeHttpObject(resp, result);
			
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	};
	
	public abstract Object executeCmd(HttpPackage httpPkg) throws Exception;
}
