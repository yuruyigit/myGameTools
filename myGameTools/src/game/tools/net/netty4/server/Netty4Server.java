package game.tools.net.netty4.server;
import game.tools.net.netty4.Netty4Decode;
import game.tools.net.netty4.Netty4Encode;
import game.tools.net.netty4.Netty4Handler;
import game.tools.net.netty4.Netty4Initializer;
import game.tools.threadpool.ThreadGroupFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class Netty4Server 
{
	private static final boolean SSL = System.getProperty("ssl") != null;
	private ServerBootstrap b;
	/***是否打印心跳*/
	private static boolean isPrintHeartbeat;
	private Netty4Initializer initial ;
	
	private int bossThreadCount , workThreadCount;
	
	public Netty4Server(Netty4Initializer initial)
	{
		this(initial , 10 , 50);
	}
	
	public Netty4Server(Netty4Initializer initial , int bossThreadCount , int workThreadCount) 
	{
		this.initial = initial;
		this.bossThreadCount = bossThreadCount;
		this.workThreadCount = workThreadCount;
	}
	
	
	public Netty4Server(Netty4Decode decode , Netty4Encode encode , Netty4Handler handler) 
	{
		this(decode , encode , handler , 10 , 50);
	}
	
	public Netty4Server(Netty4Decode decode , Netty4Encode encode , Netty4Handler handler , int bossThreadCount , int workThreadCount) 
	{
		this.initial = new Netty4Initializer(handler) 
		{
			@Override
			protected void initChannel(SocketChannel arg0) throws Exception {
				 ChannelPipeline pipeline = arg0.pipeline();
				 
		         pipeline.addLast(decode.clone());
		         pipeline.addLast(encode.clone());
		         
		         pipeline.addLast(handler);
			}
		};
		this.bossThreadCount = bossThreadCount;
		this.workThreadCount = workThreadCount;
	}
	
	
	public void start(int port)
	{
		try 
		{
			ChannelFutureListener remover = new ChannelFutureListener()
			{
		        @Override
		        public void operationComplete(ChannelFuture future) throws Exception {
		        	future.sync();
		        }
		    };
		    
			final SslContext sslCtx;
	        if (SSL) 
	        {
	            SelfSignedCertificate ssc = new SelfSignedCertificate();
	            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
	        } 
	        else 
	        {
	            sslCtx = null;
	        }
			
	    
			EventLoopGroup bossGroup = new NioEventLoopGroup(this.bossThreadCount , new ThreadGroupFactory("Netty4Server-BossGroup"));
			EventLoopGroup workerGroup = new NioEventLoopGroup(this.workThreadCount , new ThreadGroupFactory("Netty4Server-WorkGroup"));
			
			try 
			{
				b = new ServerBootstrap();
				b.group(bossGroup, workerGroup) .channel(NioServerSocketChannel.class)
				
//			ChannelOption.SO_BACKLOG, 1024 		默认值50
//				BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
//			ChannelOption.SO_KEEPALIVE, true
//				是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
//			ChannelOption.TCP_NODELAY, true 	默认为false
//				在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。
//				这里就涉及到一个名为Nagle的算法，该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
//				TCP_NODELAY 用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
//			ChannelOption.SO_REUSEADDR, true
//				SO_REUSEADDR允许启动一个监听服务器并捆绑其众所周知端口，即使以前建立的将此端口用做他们的本地端口的连接仍存在。这通常是重启监听服务器时出现，若不设置此选项，则bind时将出错。
//				SO_REUSEADDR允许在同一端口上启动同一服务器的多个实例，只要每个实例捆绑一个不同的本地IP地址即可。对于TCP，我们根本不可能启动捆绑相同IP地址和相同端口号的多个服务器。
//				SO_REUSEADDR允许单个进程捆绑同一端口到多个套接口上，只要每个捆绑指定不同的本地IP地址即可。这一般不用于TCP服务器。
//				SO_REUSEADDR允许完全重复的捆绑：当一个IP地址和端口绑定到某个套接口上时，还允许此IP地址和端口捆绑到另一个套接口上。一般来说，这个特性仅在支持多播的系统上才有，而且只对UDP套接口而言（TCP不支持多播）
//			ChannelOption.SO_RCVBUF  AND  ChannelOption.SO_SNDBUF 
//				定义接收或者传输的系统缓冲区buf的大小，
//			ChannelOption.ALLOCATOR 
//				Netty4使用对象池，重用缓冲区
//				bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//				bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
				
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(initial);
				
				Channel channel = b.bind(port).sync().channel();
//				Tools.println("Server Monitor in " + port);
//				channel.closeFuture().sync();
				channel.closeFuture().addListener(remover);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
//			finally 
//			{
//				bossGroup.shutdownGracefully();
//				workerGroup.shutdownGracefully();
//			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("---->initNet... Port " + port);
	}

	public static final boolean isPrintHeartbeat() {		return isPrintHeartbeat;	}
	public static final void setPrintHeartbeat(boolean isPrintHeartbeat) {		Netty4Server.isPrintHeartbeat = isPrintHeartbeat;	}	
}
