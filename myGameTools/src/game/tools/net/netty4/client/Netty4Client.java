package game.tools.net.netty4.client;
import game.tools.net.netty4.Netty4Initializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class Netty4Client
{
	/** zzb 2014-4-10 下午8:41:55 要连接的ip地址*/
	protected String ip ;
	/** zzb 2014-4-9 上午11:54:34 服务器端 */
	protected int port;
	
	protected String name ;
	
	protected Channel channel;
	protected Bootstrap bootstrap = new Bootstrap();
	
	private ChannelFutureListener remover = new ChannelFutureListener()
	{
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
//        	System.out.println("operationComplete");
        	future.sync();
        }
    };
    
    
    protected Netty4Initializer initial ;
    
    protected Netty4Client()	{}
    
    public Netty4Client(String ip , int port , Netty4Initializer initial)
	{
		this.initial = initial;
		this.ip = ip ;
		this.port = port;
		
		this.name = ip + ":" + port;
		
		start(ip, port);
	}
	
	protected void start(String host , int port)
	{
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try 
		{
	        if (System.getProperty("ssl") != null)
	           SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

	        bootstrap.group(workerGroup).channel(NioSocketChannel.class)
            
//            .option(ChannelOption.TCP_NODELAY, true)
//            .option(ChannelOption.SO_BACKLOG, 100)
//	          .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
	        
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(initial);
            
//	        ChannelFuture future = bootstrap.connect(host, port);
	        		
	        ChannelFuture future = bootstrap.connect(host, port).sync();  
	        
	        channel = future.channel();
	        
//	        System.out.println("channel open = " + this.channel.isOpen());
	        
//	        channel.closeFuture().sync();  
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally 
        {
//            workerGroup.shutdownGracefully();
        }
	}
	
	private boolean connection(String ip , int port) throws Exception
	{
		channel = bootstrap.connect(ip, port).sync().channel();
		return isConnection();
			
	}
	
	public boolean isConnection()
	{
		return channel.isOpen();
	}
	
	public Channel getChannel() 
	{
		return channel;
	}

	public Object send(Object o )
	{
		try 
		{
			if(channel != null)
				channel.writeAndFlush(o);
			else
			{
				if(reConnection(ip, port))
					channel.writeAndFlush(o);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean reConnection(String ip2, int port2) throws Exception 
	{
		return connection(ip2,  port2);
	}

	public void close() throws Exception
	{
		if(channel == null)
			throw new Exception("Channel Is Null [通信通道为空，请检查是否成功创建了socket通道！]");
		
		channel.close();
		channel = null;
	}

	public String getIp() {		return ip;	}
	public int getPort() {		return port;	}
	public String getName() {		return name;	}

}
