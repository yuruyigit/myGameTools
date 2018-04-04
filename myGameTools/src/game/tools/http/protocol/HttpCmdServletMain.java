package game.tools.http.protocol;
import game.tools.http.HttpPackage;
import game.tools.http.HttpServer;
import game.tools.http.HttpTools;

public class HttpCmdServletMain
{
	
	@HttpCmd(cmdNo = 123)
	public static Object httpCmd1(HttpPackage pkg)
	{
		System.out.println("pkg = " + pkg);
		return "httpCmd1";
	}
	
	public static void main(String[] args) 
	{
		HttpServer httpServer = new HttpServer("game", 1234);
		try {
			httpServer.registerServlet(HttpCmdServlet.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = HttpTools.sendPost("http://localhost:1234/game/HttpCmdServlet", 123, "工工要在地");
//		String result = HttpTools.sendGet("http://localhost:1234/game/HttpCmdServlet", 123, "工工要在地");
		
		System.out.println("result = " + result);
		
	}
}
