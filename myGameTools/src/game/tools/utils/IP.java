package game.tools.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IP 
{
	public static String getLocalIp() 
	{
        String sip = null;
        InetAddress ip = null;
        boolean bFindIP = false;
        Enumeration<NetworkInterface> netInterfaces = null;
        
        try 
        {
            netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) 
        {
            e.printStackTrace();
        }
        
        while (netInterfaces.hasMoreElements()) 
        {
            if (bFindIP) 
            {
                break;
            }
            NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
            // ----------特定情况，可以考虑用ni.getName判断
            // 遍历所有ip
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) 
            {
                ip = (InetAddress) ips.nextElement();
                // 	127.开头的都是lookback地址
                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) 
                {
                    bFindIP = true;
                    break;
                }
            }
        }
        
        if (ip != null) 
        {
            sip = ip.getHostAddress();
        }
        return sip;

    }
	
	public static void main(String[] args) 
	{
		System.out.println(IP.getLocalIp());
	}
}
