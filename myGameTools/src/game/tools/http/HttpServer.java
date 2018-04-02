package game.tools.http;


import java.util.concurrent.Executors;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import game.tools.threadpool.ThreadGroupFactory;

public class HttpServer {

	private Server server = null;
	private ServletContextHandler context = null;
	
	private String rootPath= "";
	private int port ;
	private int threadCount = 50;
	
	public HttpServer(String rootPath , int port , int threadCount) 
	{
		this.rootPath = rootPath;
		this.port = port;
		this.threadCount = threadCount;
		
		try 
		{
			if(rootPath == null && "".equals(this.rootPath) || this.port == 0 )
				throw new Exception("The rootPath cannot be empty.");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		init();
		
		start();
	}
	
	
	/**
	 * @param root 请求的根目录名称 
	 * @param port 传入一个对应http的服务侦听的端口
	 */
	public HttpServer(String rootPath , int port) 
	{
		this.rootPath = rootPath;
		this.port = port;
		
		try 
		{
			if(rootPath == null && "".equals(this.rootPath) || this.port == 0 )
				throw new Exception("The rootPath cannot be empty.");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		init();
		
		start();
	}
	
	
	private void init()
	{
		this.context = new ServletContextHandler(ServletContextHandler.SESSIONS);  
    	this.context.setContextPath("/"+this.rootPath);
    	
		server = new Server(createExecutorThreadPool());
		
        server.addConnector(createConnector());
        server.setHandler(context);
	}
	

	private void start() 
	{
		try 
		{
			server.start();
//			server.join();
			System.out.println("-----> HttpServer Start On Port "+port + " root by : " + rootPath +  " Auto Jar ");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
		}
	}

	private ThreadPool createExecutorThreadPool() 
	{
    	ExecutorThreadPool threadPool = new ExecutorThreadPool(Executors.newFixedThreadPool(threadCount , new ThreadGroupFactory("HttpServer")));
        return threadPool;
    }
	
	private NetworkConnector createConnector() {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        return connector;
    }
	
	
	/**
	 * 在此注册添加servlet业务 http://xxxx.xxx.xxx:9001/servlet/test?adsdsd
	 */
	public void initServlets(Class...classs)
	{
		try
		{
			for (int i = 0; i < classs.length; i++) 
			{
				registerServlet(classs[i]);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public void registerServlets(Class...classs)
	{
		 initServlets(classs);
	}
	
	
	public void registerServlet(Class clazz, String... path) throws Exception 
	{
		String pathString = "";
		if (path == null || path.length < 1) 
		{
			pathString = "/"+clazz.getSimpleName();
		}
		else if (path != null && path.length > 1) 
		{
			throw new Exception("httpServlet path can not have more than one!");
		}
		else 
		{
			pathString = path[0];
			if (!pathString.startsWith("/")) 
			{
				pathString = "/" + pathString;
			}
		}
		
		context.addServlet(new ServletHolder((HttpServlet)clazz.newInstance()), pathString);
		System.out.println("Register Servlet By : " + rootPath +pathString);
	}

}
