package game.tools.http.protocol;
import game.tools.http.HttpServer;
import game.tools.http.HttpTools;

public class HttpCmdServletMain
{
	
	@HttpCmd(cmdNo = 123)
	public static Object httpCmd1(HttpPackage pkg)
	{
		System.out.println("pkg = " + pkg);
		return "httpCmd1ffd";
	}
	
	public static void main(String[] args) 
	{
		HttpServer httpServer = new HttpServer("game", 1234);
		try {
			HttpCmdServlet.setHttpCmdServletScanClassPackage("game.tools.http");
			httpServer.registerServlet(HttpCmdServlet.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = HttpTools.sendPost("http://localhost:1234/game/HttpCmdServlet", 123, "工工要在地" , "asdfasdf");
//		String result = HttpTools.sendGet("http://localhost:1234/game/HttpCmdServlet", 123, "工工要在地");
		
		System.out.println("result = " + result);
		
	}
}
