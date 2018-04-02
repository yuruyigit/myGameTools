package game.tools.protocol;


import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author zzb
 */
public class Protocol 
{
	
	private static final HashMap<String, Integer> MSG_MAP = new HashMap<String, Integer>();
	
	private static Properties P = new Properties();
	
	static 
	{
		try 
		{
			P.load(new FileInputStream("conf/protocol.properties"));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static int getProtocol(String protocol)
	{
		Integer t = MSG_MAP.get(protocol);
		if(t == null)
		{
			int msgNo = Integer.parseInt(P.get(protocol).toString());
			MSG_MAP.put(protocol, msgNo);
			return msgNo;
		}
		return t;
	}
	
	
	public static String getString(String protocol)
	{
		return P.getProperty(protocol);
	}
	
	public static Object getObject(Object obj)
	{
		return P.get(obj);
	}
	
	public static String[] getKeyArray()
	{
		String [] arr = new String[P.size()];
		return P.keySet().toArray(arr);
	}
}
