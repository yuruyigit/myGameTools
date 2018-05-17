package game.tools.rpc.linda;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import game.tools.method.IMethodNo;
import game.tools.method.MethodInvokeTools;
import game.tools.method.MethodObject;
import game.tools.net.netty4.Netty4Handler;
import game.tools.net.netty4.ObjectInitializer;
import game.tools.net.netty4.server.Netty4Server;
import game.tools.redis.RedisCmd;
import game.tools.redis.RedisOper;
import game.tools.utils.StringTools;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhibing.zhou<br/>
 *	基于netty4通信，与服务器客户端建立连接，进行远程调用。
 * <pre>
	LindaServer lindaServer = new LindaServer("127.0.0.1:6379",12345, new IRpc()
	{
		public Object execute(RpcPackage rpcPackage) 
		{
			//在此处理相应的逻辑
		}
	}
 * </pre>
 */
public class LindaRpcServer 
{	
	private static final String LINDA_RPC_KEY = "LINDA-RPC-SERVER";
	
	public static final int GAP_TIME = 1 * 60 * 1000;
	
	/** 协议处理函数集合 */
	private HashMap<Integer , MethodObject> protocolHandlerMap = new HashMap<Integer , MethodObject>();
	
	private String registerRedis;
	
	private String appointIp;
	
	private int port;
	
	private int weight;
	
	private ILindaRpcNo rpcNo;
	
	/** 2018年5月17日 下午3:13:56 rpc的linda实体对象*/
	private Linda linda;
	
	private String scanClassPackage;

	/** 2018年5月17日 下午3:25:43 上一次接收处理的时间戳*/
	private long lastHandlerTimeLong;
	
	/**
	
	 * @param registerRedis	需要注册的redis地址字符串，格式：ip:port:password
	 * @param port 服务器监听的端口
	 * @param weight 使用负载分配的初始权重
	 * @param rpc 	对应所实现功能的接口
<pre>
基于netty4通信，与服务器客户端建立连接，进行远程调用。
LindaServer lindaServer = new LindaServer("127.0.0.1:6379",12345, 1, new IRpc()
{
	public Object getNo(RpcPackage rpcPackage) 
	{
		//在此处理相应的逻辑
	}
}
</pre>
	 */
	public LindaRpcServer(String scanClassPackage , String registerRedis , int port , int weight, ILindaRpcNo rpc) 
	{
		this.scanClassPackage = scanClassPackage;
		this.registerRedis = registerRedis;
		this.port = port;
		this.weight = weight;
		this.rpcNo = rpc;
		
		init();
	}
	
	
	/**
	
	 * @param registerRedis	需要注册的redis地址字符串，格式：ip:port:password
	 * @param appointIp 
	 * @param port 服务器监听的端口
	 * @param weight 使用负载分配的初始权重
	 * @param rpc 	对应所实现功能的接口
<pre>
基于netty4通信，与服务器客户端建立连接，进行远程调用。
LindaServer lindaServer = new LindaServer("127.0.0.1:6379",12345, 1, new ILindaRpcNo()
{
	public Object getNo(RpcPackage rpcPackage) 
	{
		//在此处理相应的逻辑
	}
}
</pre>
	 */
	public LindaRpcServer(String scanClassPackage , String registerRedis , String appointIp ,int port , int weight, ILindaRpcNo rpc) 
	{
		this.scanClassPackage = scanClassPackage;
		this.registerRedis = registerRedis;
		this.appointIp = appointIp;
		this.port = port;
		this.weight = weight;
		this.rpcNo = rpc;
		
		init();
	}
	

	private void init() 
	{
		registerHander();
		
		registerRedis();
		registerNetty();
	}

	private void registerHander() 
	{
		protocolHandlerMap = MethodInvokeTools.getInvokeMethod(scanClassPackage, LindaRpcNo.class, new IMethodNo() 
		{
			public int getMethodNo(Annotation annot) 
			{
				LindaRpcNo rpcNo = (LindaRpcNo)annot;
				return rpcNo.rpcNo();
			}
		});
		
		lastHandlerTimeLong = System.currentTimeMillis();
	}
	
	private void registerNetty() 
	{
		class LindaHandler extends Netty4Handler
		{
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception 
			{
//				try 
//				{
//					Object result = executeHandler(new RpcPackage(new Object [] {"LindaRpcServer.LindaHandler.channelActive"}));
//				} 
//				catch (Exception e) 
//				{
//					e.printStackTrace();
//					game.tools.log.LogUtil.error(e);
//					throw e;
//				}
			}
			
			@Override
			public void channelInactive(ChannelHandlerContext ctx)
			{
//				try 
//				{
//					Object result = executeHandler(new RpcPackage(new Object [] {"LindaRpcServer.LindaHandler.channelInactive"}));
//				} 
//				catch (Exception e) 
//				{
//					e.printStackTrace();
//					game.tools.log.LogUtil.error(e);
//				}
			}
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg)  
			{
				try 
				{
					Object [] paramArray = (Object [] ) msg;
					
					Object result = executeHandler(new LindaRpcPackage(paramArray));
					
					Object [] resultArray = new Object [] {result , paramArray[paramArray.length - 1]};
					
					ctx.channel().writeAndFlush(resultArray);
					
					executeEndHander();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					game.tools.log.LogUtil.error(e);
				}
			}
		}
		
		Netty4Server nettyServer = new Netty4Server(new ObjectInitializer(new LindaHandler()));
		nettyServer.start(this.port);
	}
	

	private void executeEndHander() 
	{
		this.linda.hit();
		
		long nowTime = System.currentTimeMillis();
		long gapTime = nowTime - lastHandlerTimeLong;
		
		
		if(gapTime > 3000)
		{
			String jsonString = JSONObject.toJSONString(linda);
			
			RedisOper.execute(RedisCmd.hset, LINDA_RPC_KEY, linda.getName() , jsonString);
			
			lastHandlerTimeLong = System.currentTimeMillis();
			
			this.linda.reset();
		}
	}
	
	
	private Object executeHandler(LindaRpcPackage pkg) throws Exception
	{
		int protocolNo = rpcNo.getNo(pkg);
		
		MethodObject methodObject = protocolHandlerMap.get(protocolNo);
		
		if(methodObject == null)
			throw new Exception("LindaRpcServer.executeHandler:" + protocolNo + " Not Handler.");
		
		return methodObject.getMethod().invoke(methodObject.getObject() , pkg);
	}
	
	/**
	 * @param registerRedis 
	 */
	static final void connectionRedist(String registerRedis)
	{
		String [] array = registerRedis.split(":");
		String ip = array[0];
		int port = Integer.parseInt(array[1]);
		
		String password = null;
		if(array.length > 2)
			password = array[2];
		
		RedisOper.connection(ip , port , password);
	}
	
	
	static final HashMap<String, Linda> getLindaServerHashMap()
	{
		HashMap<String, Linda> lindaMap = RedisOper.getAllByHash(LindaRpcServer.LINDA_RPC_KEY , Linda.class);
		
		return lindaMap;
	}
	
	static final boolean updateWeight(Map<String, String[]> paramMap)
	{
		HashMap<String, String> updateMap = new HashMap<>();
		
		HashMap<String, Linda> lindaMap = RedisOper.getAllByHash(LindaRpcServer.LINDA_RPC_KEY , Linda.class);
		
		Iterator<String> keyIterator = lindaMap.keySet().iterator();
		while(keyIterator.hasNext())
		{
			String key = keyIterator.next();
			Linda linda = lindaMap.get(key);
			
			String newWeight = paramMap.get(key)[0];
			
			int weight = Integer.parseInt(newWeight);
			
			if(linda.getWeight() != weight)
			{
				linda.setWeight(weight);
				updateMap.put(key, JSONObject.toJSONString(linda));
			}
		}
		
		if(updateMap.size() > 0)
		{
			RedisOper.execute(RedisCmd.hmset, LindaRpcServer.LINDA_RPC_KEY, updateMap);
			return true;
		}
		return false;
	}
 
	private void registerRedis() 
	{
		connectionRedist(registerRedis);
			
		String localIp = this.appointIp;
		
		try 
		{
			if(StringTools.empty(localIp))
				localIp = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		
		String nameKey = localIp+":"+this.port;
		
		this.linda = new Linda();
		linda.setName(nameKey);
		linda.setIp(localIp);
		linda.setPort(this.port);
		linda.setWeight(weight);
		
		RedisOper.execute(RedisCmd.hset, LINDA_RPC_KEY, nameKey , JSONObject.toJSON(linda));
	}
	
	@LindaRpcNo(rpcNo = 156)
	public static void test(LindaRpcPackage pkg)
	{
		System.out.println("LindaRpcServer.test 156 " + pkg);
	}
	
	public static void main(String[] args) 
	{
		
//		HashMap<String, Runnable> map =  new HashMap<>();
//		map.put("test", LindaRpcServer::test);
		
//		Runnable r = map.get("test");
//		r.run();
		
		final int port = 12345;
		LindaRpcServer server = new LindaRpcServer("game.tools.rpc" , "127.0.0.1:6379",  port , 20, new ILindaRpcNo() 
		{
			@Override
			public int getNo(LindaRpcPackage rpcPackage) {
				return rpcPackage.get(1);
			}
		});
		
		
		final int port1 = 12346;
		LindaRpcServer server1 = new LindaRpcServer("game.tools.rpc" , "127.0.0.1:6379", port1 , 20, new ILindaRpcNo() 
		{
			@Override
			public int getNo(LindaRpcPackage rpcPackage) {
				return rpcPackage.get(1);
			}
		});

		
	}
	
	
	
}
