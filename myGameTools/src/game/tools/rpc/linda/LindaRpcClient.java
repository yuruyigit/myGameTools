package game.tools.rpc.linda;
import java.util.HashMap;
import com.alibaba.fastjson.JSONObject;
import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Handler;
import game.tools.net.netty4.ObjectInitializer;
import game.tools.net.netty4.client.Netty4Client;
import game.tools.utils.Util;
import game.tools.weight.WeightObjects;
import game.tools.weight.Weights;
import io.netty.channel.ChannelHandlerContext;

public class LindaRpcClient 
{
	/** 2017年8月24日 下午2:34:21 		重新从redis中加载对应注册的服务器列表数据间隔时间，这里单位为（分钟）*/
	private static final int RELOAD_SERVER_MAP_GAP_TIME = 3;
	
	/**
	 * 每个call执行的等待返回结果的时间长，单位（毫秒）
	 */
	private static final int CALL_TIME_OUT = 5000;
	
	private int totalWeight;
	
	private String registerRedis , lindaRpcKeyName;
	
	/** 2017年8月24日 上午11:51:57 		检测通信权重的命中率*/
	private boolean checkChannelHit;
	
	/** 2017年8月24日 下午7:06:35 	当前注册的rpc服务器的列表*/
	private HashMap<String , Linda> lindaMap;
	
	private HashMap<String, Netty4Client> nettyClientMap;
	
	private HashMap<String, LindaChannelHit> channelHitMap;
	
	/** 2018年3月20日 上午9:56:04 对应线程的结果列表*/
	private HashMap<Long, Object> threadResultMap = new HashMap<>(16);
	
	/** 2017年8月24日 下午4:37:56 	最后一次加载rpc服务器列表时间戳*/
	private long lastInitLindaServerMapTime;
	
	/** 2017年8月24日 下午6:41:33 	第一个nettyClient对象*/
	private Netty4Client firstNettyClient;
	
	private Object lock = new Object() , nettyClientLock = new Object();
	/**
	 * @param registerRedis	会话注册的服务器的redis地址，格式 ip:port:password 
	 * 客户端会3分钟同步一次，所注册过的服务器状态和权重，进行新的一次匹配计算
<pre>
基于netty4通信，与服务器建立连接，进行远程调用。
LindaClient lindaClient = new LindaClient("127.0.0.1:6379");
Object o = lindaClient.call(array);
</pre>
	 * @param checkChannelHit 是否是收集命中
	 */
	
	public LindaRpcClient(String registerRedis , String lindaRpcKeyName, boolean checkChannelHit) 
	{
		this.registerRedis = registerRedis;
		this.lindaRpcKeyName = lindaRpcKeyName;
		this.checkChannelHit = checkChannelHit;
		
		init();
	}
	
	public LindaRpcClient(String registerRedis , boolean checkChannelHit) 
	{
		this(registerRedis , null , checkChannelHit);
	}
	
	
	public LindaRpcClient(String registerRedis) 
	{
		this(registerRedis , false);
	}
	

	private void init() 
	{
		registerRedis();
		
		initLindaServer();
		
		initChannelHit();
	}
	
	private void registerRedis() 
	{
		LindaRpcServer.connectionRedist(this.registerRedis);
	}
	
	
	private void initLindaServer() 
	{
		lindaMap = LindaRpcServer.getLindaServerHashMap(this.lindaRpcKeyName);
		
		WeightObjects weightObjects = Weights.calcWeight(lindaMap , "weight");
		
		this.totalWeight = weightObjects.getTotalWeight();
		
		lastInitLindaServerMapTime = System.currentTimeMillis();
		
		if(nettyClientMap == null)
			this.nettyClientMap = new HashMap<>(this.lindaMap.size());
	}
	
	
	private void initChannelHit()
	{
		if(this.checkChannelHit)
		{
			if(channelHitMap == null)
				this.channelHitMap = new HashMap<String,LindaChannelHit>(this.lindaMap.size());
		}
	}
	
	
	private synchronized Netty4Client createNettyClient(Linda linda) 
	{
		if(nettyClientMap.containsKey(linda.getName()))			//如果存在了这个客户端
				return nettyClientMap.get(linda.getName());
		
		class LindaHandler extends Netty4Handler
		{
			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception 
			{
				System.out.println("LindaRpcClient.channelInactive");
			}
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
			{
				try 
				{
					Object [] resultArray = (Object []) msg;
					
					long threadId = (long) resultArray[resultArray.length - 1];
					
					Object object = threadResultMap.get(threadId);
					
					if(object != null)
					{
						if(object instanceof Thread)
						{
							Thread syncThread = (Thread)object;
							
							Object result = resultArray[1];
							
							threadResultMap.put(threadId , result);
							
							if(syncThread != null)
							{
								synchronized (syncThread) 
								{
									syncThread.notify();
								}
							}
						}
					}
					else
					{
						throw new Exception("ThreadResultMap Thread Is Empty!");
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					LogUtil.error(e);
				}
			}
		}
		
		Netty4Client netty = new Netty4Client(linda.getIp(), linda.getPort(), new ObjectInitializer(new LindaHandler()));
		
		if(this.nettyClientMap.size() == 0)
			firstNettyClient = netty;
		
		this.nettyClientMap.put(linda.getName(), netty);
		
		return netty;
	}
	
	/** 
	 * @return 根据权重分部随机获取一个网络通信客户端
	 */
	private synchronized Netty4Client getNettyClient()
	{
		if(this.totalWeight <= 0)
		{
			try {
				throw new Exception("Linda Rpc totalWeight is 0 !!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		long nowTime = System.currentTimeMillis();
		long gapTime = (nowTime - lastInitLindaServerMapTime) / 1000 / 60;
		if(gapTime >= RELOAD_SERVER_MAP_GAP_TIME)
		{
			synchronized (nettyClientLock) 
			{
				if(gapTime >= RELOAD_SERVER_MAP_GAP_TIME)		
					initLindaServer();
			}
		}
		
		if(lindaMap.size() == 1)
		{
			if(firstNettyClient != null)
				return firstNettyClient;
			else
			{
				createNettyClient(this.lindaMap.values().iterator().next());
				return firstNettyClient;
			}
		}
		
		Linda linda = Weights.getRandomWeight(this.lindaMap, this.totalWeight);
		
		Netty4Client nettyClient = this.nettyClientMap.get(linda.getName());
		if(nettyClient == null)
		{
			synchronized (nettyClientLock) 
			{
				nettyClient = this.nettyClientMap.get(linda.getName());
				
				if(nettyClient == null)
					nettyClient = createNettyClient(linda);
			}
		}
		
		return nettyClient;
	}
	
	/**
	 * @param netty 检查统计通信通道的命中率
	 */
	private void checkChannelHit(Netty4Client netty)
	{
		if(!checkChannelHit)
			return ;
		
		LindaChannelHit channelHit = channelHitMap.get(netty.getName());
		if(channelHit == null)
		{
			synchronized(lock)
			{
				channelHit = channelHitMap.get(netty.getName());
				
				if(channelHit == null)
				{
					channelHit = new LindaChannelHit(0);
					channelHitMap.put(netty.getName(), channelHit);
				}
			}
		}
		channelHit.addHitCount();
	}
	
	/**
	 * 检测通信客户端是否满的
	 */
	private void checkNettyClientFull()
	{
		if(nettyClientMap == null)
			this.nettyClientMap = new HashMap<>(this.lindaMap.size());
		
		if(this.nettyClientMap.size() != this.lindaMap.size())					//如果客户端与服务器数据不同
		{
			for (Linda linda: lindaMap.values()) 
			{
				if(!this.nettyClientMap.containsKey(linda.getName()))			//如果不存在
				{
					Netty4Client client = createNettyClient(linda);
					this.nettyClientMap.put(linda.getName(), client);
				}
			}
		}
	}
	
	/**
	 * @param params
	 * @return 返回标记线程ID后的参数数组
	 */
	private final Object[] signThreadParam(Object ...params)
	{
		int srcLength = params.length;
		
		Object [] newParams = new Object[srcLength + 1];
		
		System.arraycopy(params, 0, newParams, 0 , srcLength);
		
		newParams[srcLength] = Thread.currentThread().getId();
		
		params = null;
		
		return newParams;
	}
			
	
	/**
	 * 全部调用一次
	 */
	public void callall(Object ...params)
	{
		checkNettyClientFull();

		params = signThreadParam(params);
		
		for (Netty4Client netty : nettyClientMap.values()) 
		{
			netty.send(params);
		}
	}
	
	/**
	 * 权重随机异步远程调用
	 * @param params 传入对应执行的函数参数
	 */
	public void callnosync(Object ...params)
	{
		params = signThreadParam(params);
		
		Netty4Client netty = getNettyClient();
		
		netty.send(params);
	}
	
	/**
	 * 权重随机同步远程调用
	 * @param params 传入对应执行的函数参数
	 * @return
	 */
	public Object call(Object ...params)
	{
		Netty4Client netty = getNettyClient();
		
		Thread syncThread = Thread.currentThread();
		
		checkChannelHit(netty);
		
		synchronized (syncThread) 
		{
			long threadId = syncThread.getId();
			
			threadResultMap.put(threadId , syncThread);
			
			params = signThreadParam(params);
			
			netty.send(params);
			
			try 
			{
				syncThread.wait(CALL_TIME_OUT);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
			return threadResultMap.remove(threadId);
		}
	}
	
	
	/**
	 * 打印通道命中情况
	 */
	public String getChannelHitSInfoString()
	{
		if(!checkChannelHit)
		{
			System.err.println("checkChannelHit not true ");
			return null;
		}
		
		return JSONObject.toJSONString(channelHitMap);
	}
	
	public static void main(String[] args) throws Exception 
	{
		Object [] array = {1,12,3,"张三",1.2f,"第二步，是对全国18家铁路局和3家专业运输公司（中铁集装箱运输有限责任公司、中铁特货运输有限责任公司、中铁快运股份有限公司）的公司制改革，即对运输主业的改革。对于这部分企业，中铁总正在加紧做好相关准备工作，并形成指导意见，改制方案和公司章程、议事规则范本。其中，完善企业化、市场化运行机制是这部分企业改革的指导方针，中铁总计划在2017年年底前启动直属运输企业改革。　　最后一步是改革最难的部分，即对中铁总公司的公司制改革。上述人士称，对总公司的改革原则就是要实现绝对的政企分开，目前方案正在国务院和财政部审核，待上述部门作出批复意见后方可实施推进。　　财新记者获悉，在近期中铁总举行的推进铁路公司制改革会议上，中铁总总经理陆东福表示，铁路企业公司制改革今年是起步和探索阶段，2018年要全面深化开展，目的是建立收、支、利刚性"};
		
		for (int j = 0; j < 1; j++) 
		{
			LindaRpcClient lindaClient = new LindaRpcClient("127.0.0.1:6379" , "LOGIN", true);
			
			
			Thread t1 = new Thread(()->{
				for (int i = 0; i < 30; i++) {
					int index = getIndex(lindaClient);
					Object result = lindaClient.call(156, index ,"参数1","参数2",array);
					try {
						Thread.sleep(Util.getRandomInt(1,500));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(index + ":" + Thread.currentThread().getId() + ": t1 result = " + result);
				}
			});
			
			Thread t2 = new Thread(()->{
				for (int i = 0; i < 30; i++) {
					int index = getIndex(lindaClient);
					Object result = lindaClient.call(156, index ,"参数1","参数2","参数3");
					try {
						Thread.sleep(Util.getRandomInt(1,500));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(index + ":" + Thread.currentThread().getId() + ": t1 result = " + result);
				}
			});
			
			Thread t3 = new Thread(()->{
				for (int i = 0; i < 30; i++) {
					int index = getIndex(lindaClient);
					Object result = lindaClient.call(156,index , "参数1","参数2","参数3");
					try {
						Thread.sleep(Util.getRandomInt(1,500));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(index + ":" + Thread.currentThread().getId() + ": t1 result = " + result);
				}
			});
			
			t1.start();
			t2.start();
			t3.start();
			
			Thread.sleep(15000);
			System.out.println("getChannelHitSInfoString = " + lindaClient.getChannelHitSInfoString());
//			Thread.sleep(1500);
			
		}
	}
	
	private static int INDEX;
	private static int getIndex(Object lock)
	{
		synchronized (lock) {
			return ++INDEX;
		}
	}
}
