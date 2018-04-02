package game.tools.rpc.flow;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import game.tools.log.LogUtil;
import game.tools.rpc.linda.ILindaRpcNo;
import game.tools.rpc.linda.LindaRpcPackage;

public class FlowServlet extends HttpServlet
{
	/** 2017年8月22日 下午6:24:46 */
	private static final long serialVersionUID = 1L;
	
	ILindaRpcNo rpc;
	
	public FlowServlet(ILindaRpcNo flow) 
	{
		this.rpc = flow;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		doCmd(req , resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		doCmd(req , resp);
	}
	
	private void doCmd(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
//		req.setCharacterEncoding("UTF-8");
//		resp.setCharacterEncoding("UTF-8");
//		resp.setHeader("content-type", "text/html;charset=UTF-8");
//
//		try 
//		{
//			Object [] paramArray = (Object[])readHttpObject(req);
//
//			RpcPackage flowPackage = new RpcPackage(paramArray);
//			
//			Object o = rpc.execute(flowPackage);
//			
//			writeHttpObject(resp,o);
//		}
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//			LogUtil.error(JSONObject.toJSONString(req.getParameterMap()),e);
//			resp.getOutputStream().write(("Exception:" + e.getMessage()).getBytes());
//		}
	}
	
	public void writeHttpObject( HttpServletResponse resp , Object result) throws Exception
	{
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(resp.getOutputStream()));  
		out.writeObject(result);
		out.flush();
		out.close();
	}
	
	private Object readHttpObject(HttpServletRequest req) throws Exception 
	{
		ObjectInputStream in = new ObjectInputStream( new BufferedInputStream(req.getInputStream()));  
		Object o = in.readObject();
		in.close();
		
		return o;
	}
}

