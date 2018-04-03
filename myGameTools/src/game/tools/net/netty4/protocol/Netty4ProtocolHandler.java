package game.tools.net.netty4.protocol;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Handler;
import game.tools.threadpool.ThreadGroupFactory;
import game.tools.utils.ClassUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @author zhouzhibin
 * <pre>
 *	反射注解netty4的处理函数
 * 	Netty4Server ns = new Netty4Server(
	new LogicDecode(), 
	new LogicEncode(),
	new Netty4ProtocolHandler("src" , new INetty4Protocol() 
	{
		Override
		public int getProtocolNo(Object msg) 
		{
			return ((JSONObject)msg).getIntValue("protocolNo");
		}
	}));
	
	//该注解函数来绑协议。
	Netty4Protocol(protocolNo = 110001)
	public static void protocol110001(Channel channel , Object msg)
	{
		System.out.println("protocol 110001 msg : " + msg);
		channel.writeAndFlush(msg);
	}
	
	</pre>
 */
public class Netty4ProtocolHandler extends Netty4Handler
{
	
	/**
	 *	如果使用 Netty4ProtocolHandler，则进行绑定的附加属性对象
	 */
	private static final AttributeKey<Object> ATTRIBUTE_KEY = AttributeKey.valueOf("Netty4ProtocolHandler-AttributeKey");
	
	/** 协议处理函数集合 */
	private HashMap<Integer , Method> protocolHandlerMap = new HashMap<Integer , Method>();
	
	private ExecutorService threadPool;
	
	private INetty4Protocol netty4Protocol;
	
	
	/**
	 * @param scanClassPackage 传入要扫描获取@ProtocolNo注解的包路径，用点隔开。如game.tools.net
	 * @param netty4Protocol 获取该数据的协议号方式，用于自己定义实现。<br/>
	 * 如果特殊处理，则重写INetty4Protocol中的，channelRead,channelReadException,channelActive,channelInactive,exceptionCaught
	 */
	public Netty4ProtocolHandler(String scanClassPackage , INetty4Protocol netty4Protocol)
	{
		this(scanClassPackage , netty4Protocol , 10);
	}
	
	/**
	 *@param scanClassPackage 传入要扫描获取@ProtocolNo注解的包路径，用点隔开。如game.tools.net
	 * @param netty4Protocol 获取该数据的协议号方式，用于自己定义实现。
	 * @param handlerThreadCount 协议处理的线程池的数量。<br/>
	 * 如果特殊处理，则重写INetty4Protocol中的，channelRead,channelReadException,channelActive,channelInactive,exceptionCaught
	 */
	public Netty4ProtocolHandler(String scanClassPackage ,INetty4Protocol netty4Protocol,int handlerThreadCount )
	{
		try 
		{
			if("src".equalsIgnoreCase(scanClassPackage) || null == scanClassPackage)
				throw new Exception("scanClassPackage Not src and null");
			
			this.threadPool =  new ThreadPoolExecutor(handlerThreadCount , handlerThreadCount * 2, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>() ,new ThreadGroupFactory("Netty4ProtocolHandler"));
			
			this.netty4Protocol = netty4Protocol;
			
			init(scanClassPackage);
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
	
	private void init(String scanClassPackage) throws Exception
	{
		Set<String> setString = ClassUtils.getClassName(scanClassPackage, true);
		
		for (String string : setString) 
		{
			Class clzss = ClassLoader.getSystemClassLoader().loadClass(string);
			Method[] methodArray = clzss.getDeclaredMethods();
			
			for (Method method : methodArray) 
			{
				Annotation annot = method.getAnnotation(Netty4Protocol.class);

				if(annot != null)
				{
					Netty4Protocol protocol = ((Netty4Protocol)annot);
					
					if(protocolHandlerMap.containsKey(protocol.protocolNo()))
					{
						throw new Exception("Netty4ProtocolHandler : duplicate protocolNo key !! at " + protocol.protocolNo());
					}
					
					protocolHandlerMap.put(protocol.protocolNo(), method);
				}
			}
		}
	}
	
	
	/**
	 * 设置渠道附加对象
	 * @param channel
	 * @param attachObject
	 */
	public static void setAttributeKey(Channel channel , Object attachObject)
	{
		channel.attr(ATTRIBUTE_KEY).set(attachObject);
	}
	
	/**
	 * @param channel
	 * @return 返回渠道附加对象
	 */
	public static Object getAttributeKey(Channel channel)
	{
		return channel.attr(ATTRIBUTE_KEY).get();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{

		Channel channel = ctx.channel();
		
		threadPool.execute(new Runnable() 
		{
			@Override
			public void run() 
			{
				try 
				{
					netty4Protocol.channelRead(channel, msg);
					
					int protocolNo = netty4Protocol.getProtocolNo(msg);
					
					Method protocolHandlerMethod = protocolHandlerMap.get(protocolNo);
					
					if(protocolHandlerMethod == null)
						throw new Exception("protocolNo:" + protocolNo + " not Handler.");
					
					Annotation annot = protocolHandlerMethod.getAnnotation(Netty4Protocol.class);
					
					Netty4Protocol protocol = ((Netty4Protocol)annot);
					
					synchronized (channel)
					{
						if(protocol.isStatic())			//如果是静态
							protocolHandlerMethod.invoke(null, channel , msg);
						else
						{
							Object o = getAttributeKey(channel);
							protocolHandlerMethod.invoke(o , msg);
						}
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					game.tools.log.LogUtil.error(e);
					
					netty4Protocol.channelReadException(channel, e);
				}
			}
		});
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{
		netty4Protocol.channelInactive(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		netty4Protocol.channelInactive(ctx.channel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception 
	{
		netty4Protocol.exceptionCaught(ctx.channel());
	}
}



