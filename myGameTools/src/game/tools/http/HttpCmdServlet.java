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
		
		HttpPackage httpPkg = null;
		try 
		{
			httpPkg = (HttpPackage)HttpClient.readHttpObject(req);
			if(httpPkg == null)
			{
				resp.getOutputStream().write("Params Empty".getBytes());
			}
			else
			{
				httpCmd.doCmd(req , resp , httpPkg);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(JSONObject.toJSONString(httpPkg),e);
			resp.getOutputStream().write(("EXCEPTION" + e.getMessage()).getBytes());
		}
	}
	

}
