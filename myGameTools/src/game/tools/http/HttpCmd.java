package game.tools.http;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import game.tools.log.LogUtil;

public abstract class HttpCmd 
{
	protected static final String DEFAULT_DO_CMD = "default doCmd";
	protected static final ExecutorService CMD_THREAD_POOL = Executors.newFixedThreadPool(100);
	
	public void doCmd(HttpServletRequest req, HttpServletResponse resp , HttpHashMap<String, Object> params ,  int protocolNo) throws Exception 
	{
		try 
		{
			Object result = executeCmd(params , protocolNo);
			
			doWrite(resp, result);
			
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	};
	
	public abstract Object executeCmd(HttpHashMap<String, Object> params ,  int protocolNo) throws Exception;
	
	public void doWrite( HttpServletResponse resp , Object result) throws Exception
	{
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(resp.getOutputStream()));  
		out.writeObject(result);
		out.flush();
		out.close();
	}
}
