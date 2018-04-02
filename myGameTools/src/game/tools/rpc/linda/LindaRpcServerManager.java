package game.tools.rpc.linda;

import game.tools.http.HttpServer;

public class LindaRpcServerManager 
{
	private static final LindaRpcServerManager lindaRpcServerManager = new LindaRpcServerManager();
	
	private LindaRpcServerManager()	{}
	
	public static final LindaRpcServerManager getInstances()	{		return lindaRpcServerManager;	}
	
	private static final String name = "LindaRpcServerManager";
	
	private String registerRedis;
	
	private int port;
	
	private HttpServer httpServer;
	
	public void start(String registerRedis , int port)
	{
		this.registerRedis = registerRedis;
		this.port = port;
		
		init();
	}

	private void init() 
	{
		initRedis();
		initHttpServer();
	}
	
	private void initRedis()
	{
		LindaRpcServer.connectionRedist(registerRedis);
	}

	private void initHttpServer() 
	{
		this.httpServer = new HttpServer(name, port);
		this.httpServer.registerServlets(LindaRpcServerManagerServlet.class);
	}
	
	
	public static void main(String[] args) 
	{
		LindaRpcServerManager.getInstances().start("127.0.0.1:6379", 11111);
	}

	public int getPort() {		return port;	}
}
