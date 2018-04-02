package game.tools.protocol;


import java.util.HashMap;

public abstract class Api
{
	private static final int PROCOTOL_CALC = 10000;
	
	private static final HashMap<Integer, Api> API_MODE_MAP = new HashMap<Integer , Api>();
	
	public abstract Object doHandler(Object ...obj) throws Exception;
	
	/**
	 * 注意，获取模块处理器，这里规范使用（协议号/100000=模块处理器编号）<br/>
	 * 因此协议号为100000开头，如：110001 , 120001 , 130001 , 140001 , <br/>
	 * 则对应的模块处理器编号为 11, 12, 13 ,14
	 *  
	 * @param protocolNo 传入对应的协议代号，如(110001)
	 * @return 返回一个协议号对应的模块处理器
	 */
	public static Api getApi(int protocolNo)
	{
		try 
		{
			if(API_MODE_MAP.size() == 0 )
				throw new Exception("Api Not Init");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		int modeNo = protocolNo / PROCOTOL_CALC;		//通过整除获取对应的模块编号
		
		return API_MODE_MAP.get(modeNo);
	}

	/**
	 * @param mode xieyi mode
	 * @param api mokuai 
	 */
	public static void init(int mode , Api api)
	{
		API_MODE_MAP.put(mode, api);
	}
	
	/**
	 * zzb  
	 * @param serverTag 传入的服务器标识，会根据标识字符串进行批量注�?
	 * @param isPrint  是否打印注册的处理事�?
	 */
	public static void init(String serverTag , boolean isPrint)
	{
		String [] keys = Protocol.getKeyArray();
		for (int i = 0; i < keys.length; i++) 
		{
			String key = keys[i];
			if(key.indexOf(serverTag) >= 0 )
			{
				
				String [] arr = Protocol.getString(key).split(",");
				int mode = Integer.parseInt(arr[0]);
				String cls = arr[1];
				try 
				{
					API_MODE_MAP.put(mode, (Api)Class.forName(cls).newInstance());
					if(isPrint)
						System.out.println("Register Api Cls as : " + cls);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * zzb  
	 * @param mode 模块ID
	 * @param clsPath 类路�?
	 */
	public static void registered(int mode , String clsPath)
	{
		try {
			API_MODE_MAP.put(mode, (Api)Class.forName(clsPath).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		Api api = Api.getApi(21);
		
	}
}
