package game.tools.rpc.linda;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import game.tools.utils.StringTools;

public class  LindaRpcServerManagerServlet extends HttpServlet
{
	/** 2017年8月25日 上午10:41:46 */
	private static final long serialVersionUID = 1L;
	
	private static final String LINDA_RPC_KEY_NAME = "lindaRpcKeyName";
	
	public LindaRpcServerManagerServlet() 
	{
		System.out.println("LindaRpcServerManagerServlet");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		doHandler(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		doHandler(req, resp);
	}

	private void doHandler(HttpServletRequest req, HttpServletResponse resp) 
	{
		Map<String, String[]> paramMap = req.getParameterMap();
		
		boolean update = false;
		
		if(paramMap.containsKey("update"))
			update = update(req , resp , paramMap);
		
		pageContent(req, resp , paramMap , update);
	}
	
	 
	private boolean update(HttpServletRequest req, HttpServletResponse resp, Map<String, String[]> paramMap) 
	{
		return LindaRpcServer.updateWeight(paramMap  , getLindaRpcKeyName(req));
	}

	private void pageContent(HttpServletRequest req, HttpServletResponse resp, Map<String, String[]> paramMap, boolean update)
	{
		try 
		{
//			printParams(paramMap);
			
			req.setCharacterEncoding("utf-8");
			resp.setCharacterEncoding("utf-8");
			resp.setHeader("content-type", "text/html;charset=UTF-8");
			
			StringBuilder pageContent = new StringBuilder();
			
			String rpcKeyName = checkRpcKeyName(req, pageContent , paramMap);
			
			HashMap<String, Linda>lindaMap = LindaRpcServer.getLindaServerHashMap(rpcKeyName);
			
			StringBuffer tableContent = new StringBuffer();
			tableContent.append("<tr><td width='25%' >名称</td>"
					+ "<td width='25%' >ip地址</td>"
					+ "<td width='10%'>端口</td>"
					+ "<td width='40%'>分配权重</td>"
					+ "<td width='10%'><div style=\"width:120px\">每3秒命中</div></td></tr>");
			
			for(Linda linda : lindaMap.values())
			{
				tableContent.append("<tr>")
						.append("<td width='25%' name=''>").append(linda.getName()).append("</td>")
						.append("<td width='25%' >").append(linda.getIp()).append("</td>")
						.append("<td width='10%' >").append(linda.getPort()).append("</span></td>")
						.append("<td width='40%' > <input type=\"text\"  name='"+linda.getName()+"' style=\"width:120px\" value=\'").append(linda.getWeight()).append("'/></td>")
						.append("<td width='10%' >").append("<div>   " + linda.getHit() + "   </div>").append("</td>")
						.append("</tr>");
			}
			
			StringBuffer requestAddress = req.getRequestURL();
			String localIp = requestAddress.substring("http://".length(), requestAddress.lastIndexOf(":"));
			int localPort = LindaRpcServerManager.getInstances().getPort();
			String localUrl = "http://"+localIp+":"+localPort+"/LindaRpcServerManager/LindaRpcServerManagerServlet";
			
			
			pageContent.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")
			.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" /><title>Linda 服务管理</title>")
			.append("<style>table{border-top:1px solid; border-left:1px solid; padding:0px; margin:0px;} td{width:33%;  border-bottom:1px solid ;border-right:1px solid;   }</style> </head>")
			.append("<body><br/><br/>");
			
			if(update)
				pageContent.append(alert("更新成功！"));
			else 	
			{
				if(paramMap.containsKey("update"))
					pageContent.append(alert("参数无更改！"));
			}
			
			pageContent.append("<div align=\"center\" > <form action='"+localUrl+"' method='post'><table width=\"1000px\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" >")
			.append("<tr height=\"50px\">  <td colspan=\"5\" align=\"center\"><img src=\"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503652789614&di=1a5f7630ef133c257f3187d26da81d7d&imgtype=0&src=http%3A%2F%2Fthemagichappensnow.com%2Fwp-content%2Fuploads%2F2015%2F03%2Fwipe-97583_640.png\" weight=\"50px\" height=\"50px\"  valign=\"middle\"/>Linda 服务管理</td></tr>");
			
			if(rpcKeyName != null)
				pageContent.append("<tr height=\"50px\">  <td colspan=\"5\" align=\"center\"> <input id='rpcKey' type=\"text\" placeholder='请输入查询RPC的KEY' value='"+rpcKeyName+"' style='height:30px;' /> </td></tr>");
			else
				pageContent.append("<tr height=\"50px\">  <td colspan=\"5\" align=\"center\"> <input id='rpcKey' type=\"text\" placeholder='请输入查询RPC的KEY' style='height:30px;' /> </td></tr>");
					
			pageContent.append(tableContent)
			.append("<tr height=\"50px\"><td colspan=\"5\" align=\"center\">")
				.append("<input type=\"button\" style=\"width:120px\" value=\"查        询\" onclick=\" javascript:window.location.href='"+localUrl+"?doCmd=search&rpcKey='+document.getElementById('rpcKey').value\" />  ")
				.append("<input type=\"hidden\" name='update' value='true'/>  ");
				
			if(rpcKeyName != null)
				pageContent.append("<input type=\"submit\" style=\"width:120px\" value=\"更新权重\"/>  ");
			else
				pageContent.append("<input type=\"submit\" disabled=\"disabled\" style=\"width:120px\" value=\"更新权重\"/>  ");
			
			
//				.append("<input type=\"button\" style=\"width:120px\" value=\"删除服务\"/>")
			
			pageContent.append("</td></tr></table></div></body>")
			.append("</html>");
			
			resp.getOutputStream().write(pageContent.toString().getBytes());
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String getLindaRpcKeyName(HttpServletRequest req )
	{
		Object lindaRpcKeyName = req.getSession().getAttribute(LINDA_RPC_KEY_NAME);
		
		if(lindaRpcKeyName != null)
		{
			if(!StringTools.empty(lindaRpcKeyName.toString()))
				return lindaRpcKeyName.toString();
			else
				return null;
		}
		return null;
	}
	
	private String checkRpcKeyName(HttpServletRequest req , StringBuilder pageContent ,  Map<String, String[]> paramMap)
	{
		Object rpcKeyObject= paramMap.get("rpcKey");
		
		if(rpcKeyObject == null)
		{
			return getLindaRpcKeyName(req);
		}
		else
		{
			String rpcKeyName = ((String[])rpcKeyObject)[0];
			if(!StringTools.empty(rpcKeyName))
			{
				req.getSession().setAttribute(LINDA_RPC_KEY_NAME, rpcKeyName);
				return rpcKeyName;
			}
			else
				return null;
			
		}
	}
	
	private String alert(String msg)
	{
		String script = "<script language='javascript'>alert('"+msg+"');</script>";
		return script;
	}
	
	private void printParams(Map<String, String[]> paramMap)
	{
		Iterator<String> keyIterator = paramMap.keySet().iterator();
		while(keyIterator.hasNext())
		{
			String key = keyIterator.next();
			System.out.println(key +" = " + Arrays.toString(paramMap.get(key)));
		}
	}
}