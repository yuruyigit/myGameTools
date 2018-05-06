package game.tools.net.netty4.client.sync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;
import game.tools.net.netty4.Netty4Decode;
import game.tools.net.netty4.Netty4Encode;
import game.tools.net.netty4.Netty4Handler;
import game.tools.net.netty4.Netty4Initializer;
import game.tools.net.netty4.client.Netty4Client;
import game.tools.net.netty4.deencode.JSONDecode;
import game.tools.net.netty4.deencode.JSONEncode;
import game.tools.net.netty4.deencode.ProtoBufDecode;
import game.tools.net.netty4.deencode.ProtoBufEncode;
import game.tools.protocol.protobuffer.ProtocolBuffer;
import game.tools.protocol.protobuffer.Protocol.Login;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class Netty4ClientSync extends Netty4Client
{
	private Thread syncThread ;
	
	private Netty4HandlerSyc nettyHandler;
	
	class Netty4HandlerSyc extends Netty4Handler
	{
		private Object result;
		private ChannelHandlerContext ctx;
		private Channel channel;
		private INettyChannelRead channelRead;
		
		public Netty4HandlerSyc(INettyChannelRead channelRead) 
		{
			this.channelRead = channelRead;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception 
		{
			this.ctx = ctx;
			this.channel = ctx.channel();
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
		{
			cause.printStackTrace();
			super.exceptionCaught(ctx, cause);
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
		{
			if(msg != null)
			{
				if(syncThread != null)
				{
					synchronized (syncThread) 
					{
//						this.result = syncThread.getId() + " " + msg;
						this.result = msg;
						
						syncThread.notify();
						syncThread = null;
					}
				}
				
				return ;
			}
			
			try 
			{
				JSONObject o = (JSONObject)msg;
				
				o.put("revTime", System.currentTimeMillis());
				
				if(syncThread != null)
				{
					synchronized (syncThread) 
					{
//						this.result = syncThread.getId() + " " + msg;
						this.result = msg;
						
						syncThread.notify();
						syncThread = null;
					}
				}
				else
				{
					if(channelRead != null)
						channelRead.channelRead(ctx, msg);

//					System.out.println("channelRead = " + msg);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception 
		{
			this.ctx = null;
			this.channel = null;
			this.result = null;
		}

		public Object getResult() {		return result;	}
		public void setResult(Object result) {			this.result = result;		}
		public ChannelHandlerContext getCtx() {		return ctx;	}
		public Channel getChannel() {		return channel;	}
	}
	
	
	class Netty4InitializerSync extends Netty4Initializer
	{

		private Netty4Decode decode;
		private Netty4Encode encode;
		
		public Netty4InitializerSync(Netty4Handler handler , Netty4Decode decode , Netty4Encode encode)
		{
			super(handler);
			
			this.decode = decode;
			this.encode = encode;
		}
		
		@Override
		protected void initChannel(SocketChannel ch) throws Exception 
		{
			 ChannelPipeline pipeline = ch.pipeline();
	         pipeline.addLast(decode);
	         pipeline.addLast(encode);
	         pipeline.addLast(nettyHandler);
		}
	}
	
//	public Netty4ClientSync(String ip, int port, Netty4Decode decode , Netty4Encode encode)
//	{
//		this(ip , port , decode , encode , null);
//	}
			
	/**
	 * @param ip 对应的ip地址
	 * @param port 对应的端口
	 * @param decode	对应的解析
	 * @param encode	对应的解析
	 * @param channelRead  接收主动推送处理函数接口
	 */
	public Netty4ClientSync(String ip, int port, Netty4Decode decode , Netty4Encode encode , INettyChannelRead channelRead)
	{
		this.nettyHandler = new Netty4HandlerSyc(channelRead);
		
		this.ip = ip ;
		this.port = port;
		
		Netty4InitializerSync initial = new Netty4InitializerSync( nettyHandler , decode, encode);
		
		start(ip, port , initial);
	}
	
	protected void start(String host , int port , Netty4InitializerSync initial)
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
	        
	        System.out.println("channel open = " + this.channel.isOpen());
	        
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
	
	
	public synchronized Object send(Object o) 
	{
		syncThread = Thread.currentThread();
		synchronized (syncThread) 
		{
			super.send(o);
			try 
			{
				syncThread.wait(5000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		Object retResult =  this.nettyHandler.getResult();
		this.nettyHandler.setResult(null);
		
		return retResult;
	}
	
	public static void main(String[] args) throws Exception
	{
//		JSONObject o = JSONObject.parseObject("{\"protocolNo\":110001,\"platfromId\":\"1\",\"userId\":\"2017062613s37ssdddds42\",\"channelId\":\"3000001\"}");
		Runnable r = new Runnable() {
			
			@Override
			public void run() 
			{
				JSONObject o = JSONObject.parseObject("{\"protocolNo\":110001,\"platfromId\":\"1\",\"userId\":\"2017062613s37ssdddds42\",\"channelId\":\"3000001\"}");
				
				ProtocolBuffer protocolBuffer = new ProtocolBuffer(110001, 
						Login.newBuilder()
						.setUserId("2017062613s37ssdddds42")
						.setChannl("3000001")
						.setPlaform("1"));
				
				System.out.println(protocolBuffer.toString());
				
//				Netty4ClientSync client = new Netty4ClientSync("127.0.0.1", 1111, new JSONDecode() , new JSONEncode() , new INettyChannelRead() {
				Netty4ClientSync client = new Netty4ClientSync("127.0.0.1", 1111, new ProtoBufDecode() , new ProtoBufEncode() , new INettyChannelRead() {
					
					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
					{
						System.out.println("revc push " + msg);
					}
				});
				
				for (int j = 0; j < 1; j++) 
				{
					
					try 
					{
						
						Object result = client.send(protocolBuffer);
						
						client.send(new ProtocolBuffer(111001, 
								Login.newBuilder()
								.setUserId("2017062613s37ssdddds42")
								.setChannl("3000001")
								.setPlaform("1")));
						
						
						client.send(new ProtocolBuffer(120002, 
								Login.newBuilder()
								.setUserId("2017062613s37ssdddds42")
								.setChannl("3000001")
								.setPlaform("1")));
						
						
//						Object result = client.send(o);
//						o = JSONObject.parseObject("{\"protocolNo\":120001}");
//						client.send(o);
						
						Thread.sleep(300);
						
//						o = JSONObject.parseObject("{\"protocolNo\":120002}");
//						client.send(o);
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}
		};
		ExecutorService exe = Executors.newCachedThreadPool();
		
		for (int i = 0; i < 1; i++) 
		{
			exe.execute(r);
		}
		
		
//		Object result = client.send(o);
//		
//		o = JSONObject.parseObject("{\"protocolNo\":120001}");
//		client.send(o);
//		
//		o = JSONObject.parseObject("{\"protocolNo\":120002}");
//		client.send(o);
		
		System.out.println("send ok ");
//		
//		Runnable run = new Runnable() {
//			
//			@Override
//			public void run() {
//				JSONObject o = JSONObject.parseObject("{\"protocolNo\":110001,\"platfromId\":\"1\",\"userId\":\"2017062613s37ssdddds42\",\"channelId\":\"3000001\"}");
//				
//				long startTime = System.currentTimeMillis();
//				Netty4ClientSync client = new Netty4ClientSync("192.168.1.19", 20012, new LogicDecode() , new LogicEncode() , new INettyChannelRead() {
//					
//					@Override
//					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
//					{
//						System.out.println("revc push " + msg);
//					}
//				});
//				
//				long endTime = System.currentTimeMillis();
//				System.out.println("create = " + (endTime - startTime));
//				client.send(o);
//				
////				o = JSONObject.parseObject("{\"protocolNo\":140033}");
//				o = JSONObject.parseObject("{\"protocolNo\":130011}");
//				int index = 0;
//				startTime = System.currentTimeMillis();
//				endTime = System.currentTimeMillis();
//				
//				while((endTime - startTime) <= 1000)
//				{
//					
//					o.put("index", index);
//					o.put("sendTime", System.currentTimeMillis());
//					
//					client.send(o);
//					
//					endTime = System.currentTimeMillis();
//					index ++;
//				}
//				
//				System.out.println("index = " + index);
//				
//			}
//		};
//		
//		
//		Thread t1 = new Thread(run);
//		Thread t2 = new Thread(run);
//		
//		t1.start();
//		t2.start();
		
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130042}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130011}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130055,\"itemCount\":\"1312,3\"}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130011}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130011}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130011}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130011}");
//		sends(client, o);
//		o = JSONObject.parseObject("{\"protocolNo\":130011}");
//		sends(client, o);
	}
	
	private static void sends(Netty4ClientSync client , JSONObject o)
	{
		long startTime = System.currentTimeMillis();
		Object result = client.send(o);
		long endTime = System.currentTimeMillis();
		System.out.println("send = " + (endTime - startTime) + "\t result = " + result);
	}
	

}
