package game.tools.rpc.flow;
import java.io.IOException;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

import game.tools.rpc.linda.ILindaRpcNo;
import game.tools.rpc.linda.LindaRpcPackage;


/**
 * @author zhibing.zhou<br/>
 *	基于http通信，与服务器客户端建立连接，进行远程调用。
<pre>
	FlowServer flowServer = new FlowServer(12345, new IRpc()
	{
		public Object execute(RpcPackage rpcPackage) 
		{
			//在此处理相应的逻辑
		}
	}
</pre>
 */
public class FlowRpcServer 
{
	public static final String ROOT_PATH = "Flow";
	
	private int port;
	
	private int threadCount = 10;
	
	private Server server = null;
	
	private ServletContextHandler context = null;
	
	private String rootPath;
	
	private ILindaRpcNo rpc;
			
	/**
	 * @param port 所监听的端口
	 * @param threadCount 指定的线程数量，默认为10
	 * @param rpc 对应所实现的rpc远程函数	
<pre>
基于http通信，与服务器客户端建立连接，进行远程调用。
FlowServer flowServer = new FlowServer(12345, new IRpc()
{
	public Object execute(RpcPackage rpcPackage) 
	{
		//在此处理相应的逻辑
	}
}
</pre>
	 */
	public FlowRpcServer(int port , int threadCount , ILindaRpcNo rpc) 
	{
		this.port = port;
		this.threadCount = threadCount;
		this.rpc = rpc;
		
		init();
	}
	
	public FlowRpcServer(int port , ILindaRpcNo flow) 
	{
		this.port = port;
		this.rpc = flow;
		
		init();
	}
	
	private void init() 
	{
		initHttpServer();
		
		registerServlet();
	}
	
	private void registerServlet() 
	{
		FlowServlet flowServlet = new FlowServlet(rpc);
		
		registerServlet(flowServlet);
	}

	private void registerServlet(HttpServlet servlet)  
	{
		if (servlet == null ) 
		{
			System.err.println("FlowServer registerServlet at is not HttpServlet!");
			return ;
		}
		
		String pathString = "/" +servlet.getClass().getSimpleName();
		
		context.addServlet(new ServletHolder(servlet), pathString);
		
		System.out.println("Register Servlet By : " + rootPath +pathString);
	}
	
	private void initHttpServer()
	{
		this.context = new ServletContextHandler(ServletContextHandler.SESSIONS);  
    	this.context.setContextPath("/" + FlowRpcServer.ROOT_PATH);
    	
		server = new Server(new ExecutorThreadPool(Executors.newFixedThreadPool(threadCount)));
		
	    ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        
        server.addConnector(connector);
        
        server.setHandler(context);
        
        rootPath = server.getURI().getHost() + ":"+port+"/" + FlowRpcServer.ROOT_PATH;
        
        try
        {
			server.start();
		}
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        
        System.out.println("FlowServer Startup : " + rootPath);
	}
	
	public static void main(String[] args) 
	{
//		FlowRpcServer flowServer = new FlowRpcServer(12345, new IRpcNo()
//		{
//			long startTime = 0;
//			
//			int index = 0;
//			
//			@Override
//			public Object execute(RpcPackage rpcPackage) 
//			{
//				if(startTime == 0)
//					startTime = System.currentTimeMillis();
//				
//				Object method = rpcPackage.get(5);
//				
//				index ++ ;
//				
//				long endTime = System.currentTimeMillis();
//				
//				if(endTime - startTime >= 1000)
//				{
//					System.out.println("req  index = " + index);
//					startTime = 0;
//					index = 0;
//				}
//				
//				return System.currentTimeMillis();
//			}
//		});
//		
//		
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
