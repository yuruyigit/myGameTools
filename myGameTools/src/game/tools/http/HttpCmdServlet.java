package game.tools.http;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import game.tools.http.HttpTools;
import game.tools.keys.Keys;
import game.tools.log.LogUtil;

/**
 * @author zhibing.zhou
 */
public abstract class HttpCmdServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected HttpCmd httpCmd ;
	
	/**
	 * 使用这个httpCmdServer中必须要传入一个cmd接口实现内容
	 * @param httpCmd 传入要执行IHttpCmd的接口实现
	 */
	protected HttpCmdServlet(HttpCmd httpCmd) 
	{
		this.httpCmd = httpCmd;
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doCmd(req, resp);
	}

	private void doCmd(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("content-type", "text/html;charset=UTF-8");

		if(httpCmd == null)
		{
			resp.getOutputStream().write("HttpCmd Obj Is Null".getBytes());
			return ;
		}
		
		Object result = null;
		HttpHashMap<String, Object> params = null;
		try 
		{
			params = (HttpHashMap<String, Object>)readHttpObject(req);
			if(params == null)
			{
				resp.getOutputStream().write("Params Empty".getBytes());
			}
			else
			{
				Object protocolObject = params.getValue(Keys.PROTOCOL_NO);
				if (protocolObject == null)
				{
					resp.getOutputStream().write("Not Cmd".getBytes());
					return ;
				}
				
				int protocolNo = (int)protocolObject;
				
				httpCmd.doCmd(req , resp , params ,protocolNo );
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(JSONObject.toJSONString(params),e);
			resp.getOutputStream().write(("EXCEPTION" + e.getMessage()).getBytes());
		}
	}
	
	private Object readHttpObject(HttpServletRequest req) throws Exception 
	{
		ObjectInputStream in = new ObjectInputStream( new BufferedInputStream(req.getInputStream()));  
		Object o = in.readObject();
		in.close();
		
		return o;
	}

}
