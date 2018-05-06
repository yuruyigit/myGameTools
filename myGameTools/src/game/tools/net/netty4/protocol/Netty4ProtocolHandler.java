package game.tools.net.netty4.protocol;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Handler;
import game.tools.protocol.protobuffer.ProtocolBuffer;
import game.tools.threadpool.ThreadGroupFactory;
import game.tools.utils.ClassUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

class MethodObject
{

	/** 2018年4月19日 上午2:21:07 protocol解析数据函数名*/
	private static final String protobuffer_parse_form = "parseFrom";
	
	private Object object;
	
	private int protocolNo;
	
	private Method method;
	
	private Method protobufferParseFromMethod;

	public MethodObject(int protocolNo, Object object, Method method) 
	{
		this.object = object;
		this.protocolNo = protocolNo;
		this.method = method;
		Class clzssParams [] = this.method.getParameterTypes();
		Class paramClass = clzssParams[clzssParams.length - 1];
		try 
		{
			if(paramClass.getSuperclass().getName().indexOf("com.google.protobuf") >= 0)
				this.protobufferParseFromMethod = paramClass.getMethod(MethodObject.protobuffer_parse_form, byte[].class);
		}
		catch (Exception e)		{		}
	}

	public Object getObject() {		return object;	}
	public int getProtocolNo() {		return protocolNo;	}
	public Method getMethod() {		return method;	}
	public Method getProtobufferParseFromMethod() {		return protobufferParseFromMethod;	}
}

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
	param channel 	该参数为通信通道对象
	param msg		为该协议请求的消息对象
	param attach  	该通使用Netty4ProtocolHandler处理器，所绑定的对象
	Netty4Protocol(protocolNo = 110001 , isStatic = true)		//如果是登录
	public void doLogin(Channel channel,  Object attach ,  Object msg)
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
	private static final AttributeKey<Object> ATTRIBUTE_PLAY_CONTROL = AttributeKey.valueOf("Netty4ProtocolHandler-PlayControl") ,
											ATTRIBUTE_NETTY4_PROTOCOL = AttributeKey.valueOf("Netty4ProtocolHandler-INetty4Protocol");
	
	/** 协议处理函数集合 */
	private HashMap<Integer , MethodObject> protocolHandlerMap = new HashMap<Integer , MethodObject>();
	
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
			
			this.threadPool =  new ThreadPoolExecutor(handlerThreadCount , handlerThreadCount * 3, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>() ,new ThreadGroupFactory("Netty4ProtocolHandler-Execute"));
			
			this.netty4Protocol = netty4Protocol;
			
			if(this.netty4Protocol == null)
				throw new Exception("netty4Protocol is null error !!");
			
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
		
		boolean isCreate = false;
		
		for (String string : setString) 
		{
			Class clzss = ClassLoader.getSystemClassLoader().loadClass(string);
			
			isCreate = isCreate(clzss);
			
			Object clzssObject = null;
			
			if(isCreate)			
				clzssObject = clzss.newInstance();
			
			Method[] methodArray = clzss.getDeclaredMethods();
			
			for (Method method : methodArray) 
			{
				method.setAccessible(true);
				
				Annotation annot = method.getAnnotation(Netty4Protocol.class);

				if(annot != null)
				{
					Netty4Protocol protocol = ((Netty4Protocol)annot);
					
					int protocolNo = protocol.protocolNo();
					
					if(protocolHandlerMap.containsKey(protocolNo))
						throw new Exception("Netty4ProtocolHandler : duplicate protocolNo key !! at " + protocolNo);
					
					MethodObject methodObject = new MethodObject(protocolNo, clzssObject, method);
					
					protocolHandlerMap.put(protocolNo, methodObject);
				}
			}
		}
	}
	
	/**
	 * @return 是否有空的构建函数，用于创建对象使用
	 */
	private boolean isCreate(Class clzss)
	{
		boolean isAbs = Modifier.isAbstract(clzss.getModifiers()) ;
		if(isAbs)			//如果是抽象类
			return false;
		
		Constructor [] constrArray = clzss.getConstructors();
		for (Constructor constructor : constrArray) 
		{
			if(constructor.getParameterTypes().length == 0)
				return true;
		}
		return false;
	}
	
	
	/**
	 * 设置渠道附加对象
	 * @param channel
	 * @param attachObject
	 */
	public static void setAttributeKey(Channel channel , AttributeKey<Object> attribute , Object attachObject)
	{
		channel.attr(attribute).set(attachObject);
	}
	
	/**
	 * @param channel
	 * @return 返回渠道附加对象
	 */
	public static Object getAttributeKey(Channel channel , AttributeKey<Object> attribute)
	{
		return channel.attr(attribute).get();
	}
	
	
	public static void setPlayControlAttribute(Channel channel ,  Object attachObject)	{		setAttributeKey(channel, ATTRIBUTE_PLAY_CONTROL, attachObject);	}
	public static Object getPlayControlAttribute(Channel channel )	{		return getAttributeKey(channel, ATTRIBUTE_PLAY_CONTROL);	}
	
	public static INetty4Protocol getNetty4ProtocolAttribute(Channel channel)	{		return (INetty4Protocol)getAttributeKey(channel , ATTRIBUTE_NETTY4_PROTOCOL);	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object revMsg) throws Exception 
	{
		Channel channel = ctx.channel();
		
		threadPool.execute(new Runnable() 
		{
			@Override
			public void run() 
			{
				try 
				{
					Object msg = revMsg;
					
					int protocolNo = netty4Protocol.getProtocolNo(msg);
					
					MethodObject methodObject = protocolHandlerMap.get(protocolNo);
					
					if(methodObject == null)
						throw new Exception("not methodObject by : " + protocolNo);
					
					Method method = methodObject.getMethod();
					
					if(method == null)
						throw new Exception("protocolNo:" + protocolNo + " not Handler.");
					
					Method parseFromMethod = methodObject.getProtobufferParseFromMethod();
					if(parseFromMethod != null)
						msg = parseFromMethod.invoke(null, ProtocolBuffer.subBytes((byte[])msg, 4));			//如果protobuf调用各自己对象的解析函数
					
					synchronized (channel)
					{
						Object handlerStartReturnResult = netty4Protocol.channelReadStart(channel, protocolNo, msg);
						
						if(methodObject.getObject() != null)
							method.invoke(methodObject.getObject(), channel, msg);
						else
						{
							Object attach = getPlayControlAttribute(channel);
							
							if(attach != null)
								method.invoke(attach , msg);
							else
								method.invoke(null, channel, msg);
						}
						
						netty4Protocol.channelReadEnd(channel, protocolNo, msg , handlerStartReturnResult);
					}
				}
				catch (InvocationTargetException e) 
				{
					Exception ec = (Exception)e.getTargetException();
					
					ec.printStackTrace();
					game.tools.log.LogUtil.error(ec);
					
					netty4Protocol.channelReadException(channel, ec);
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
		Channel channel = ctx.channel();
		
		System.out.println("channelActive " + channel.id());
		
		Object attach = getAttributeKey(channel , ATTRIBUTE_NETTY4_PROTOCOL);
		
		if(attach == null)
		{
			synchronized (channel) 
			{
				attach = getAttributeKey(channel , ATTRIBUTE_NETTY4_PROTOCOL);
				
				if(attach == null)
					setAttributeKey(channel, ATTRIBUTE_NETTY4_PROTOCOL , this.netty4Protocol);
			}
		}
			
		netty4Protocol.channelActive(ctx.channel());
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



