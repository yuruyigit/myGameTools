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

public class  LindaRpcServerManagerServlet extends HttpServlet
{
	/** 2017年8月25日 上午10:41:46 */
	private static final long serialVersionUID = 1L;

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
		if(paramMap.size() != 0)
			update = update(req , resp , paramMap);
		
		pageContent(req, resp , paramMap , update);
	}
	
	 
	private boolean update(HttpServletRequest req, HttpServletResponse resp, Map<String, String[]> paramMap) 
	{
		return LindaRpcServer.updateWeight(paramMap);
	}

	private void pageContent(HttpServletRequest req, HttpServletResponse resp, Map<String, String[]> paramMap, boolean update)
	{
		try 
		{
//			printParams(paramMap);
			
			req.setCharacterEncoding("utf-8");
			resp.setCharacterEncoding("utf-8");
			resp.setHeader("content-type", "text/html;charset=UTF-8");
			
			HashMap<String, Linda>lindaMap = LindaRpcServer.getLindaServerHashMap();
			
			StringBuffer tableContent = new StringBuffer();
			tableContent.append("<tr><td>名称</td><td>ip地址</td><td>端口</td><td>分配权重</td></tr>");
			
			for(Linda linda : lindaMap.values())
			{
				tableContent.append("<tr>")
						.append("<td width='25%' name=''>").append(linda.getName()).append("</td>")
						.append("<td width='25%' >").append(linda.getIp()).append("</td>")
						.append("<td width='10%' >").append(linda.getPort()).append("</span></td>")
						.append("<td width='40%' > <input type=\"text\"  name='"+linda.getName()+"' style=\"width:120px\" value=\'").append(linda.getWeight()).append("'/></td>")
						.append("</tr>");
			}
			
			StringBuffer requestAddress = req.getRequestURL();
			String localIp = requestAddress.substring("http://".length(), requestAddress.lastIndexOf(":"));
			int localPort = LindaRpcServerManager.getInstances().getPort();
			String localUrl = "http://"+localIp+":"+localPort+"/LindaRpcServerManager/LindaRpcServerManagerServlet";
			
			StringBuffer pageContent = new StringBuffer();
			pageContent.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")
			.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" /><title>Linda 服务管理</title>")
			.append("<style>table{border-top:1px solid; border-left:1px solid; padding:0px; margin:0px;} td{width:33%;  border-bottom:1px solid ;border-right:1px solid;   }</style> </head>")
			.append("<body><br/><br/>");
			if(update)
				pageContent.append(alert("更新成功！"));
			else 
			{
				if(paramMap.size() > 0)
					pageContent.append(alert("参数无更改！"));
			}
			
			pageContent.append("<div align=\"center\" > <form action='"+localUrl+"' method='post'><table width=\"1000px\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" >")
			.append("<tr height=\"50px\">  <td colspan=\"4\" align=\"center\"><img src=\"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503652789614&di=1a5f7630ef133c257f3187d26da81d7d&imgtype=0&src=http%3A%2F%2Fthemagichappensnow.com%2Fwp-content%2Fuploads%2F2015%2F03%2Fwipe-97583_640.png\" weight=\"50px\" height=\"50px\"  valign=\"middle\"/>Linda 服务管理</td></tr>")
//			.append("<tr height=\"50px\">  <td colspan=\"4\" align=\"center\">Linda 服务管理</td></tr>")
			.append(tableContent)
			.append("<tr height=\"50px\"><td colspan=\"4\" align=\"center\">")
				.append("<input type=\"submit\" style=\"width:120px\" value=\"更新权重\"/>  ")
//				.append("<input type=\"button\" style=\"width:120px\" value=\"删除服务\"/>")
			.append("</td></tr></table></div></body>")
			.append("</html>");
			
			resp.getOutputStream().write(pageContent.toString().getBytes());
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
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