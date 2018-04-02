package game.tools.net.socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import game.tools.log.LogUtil;

public class SocketClient 
{
	private String ip ; 
	private int port;
	
	private Socket socket;
	
	public SocketClient(String ip , int port) 
	{
		this.ip = ip;
		this.port = port;
		
		connection(this.ip, this.port);
	}
	
	private void connection(String ip , int port)
	{
		try 
		{
            socket = new Socket(ip, port);
            
            System.out.println("connection to remote addr : " + socket.getRemoteSocketAddress());
        }
		catch (Exception e) 
		{
			e.printStackTrace();
        }
	}
	
	
	public void reConnection()
	{
		connection(this.ip, this.port);
	}
	
	public void reConnection(String ip , int port)
	{
		connection(ip , port);
	}
	
	public ByteBuffer send(ByteBuffer o )
	{
		if(isClose())
			return null;
		
		try 
		{
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());  
			
//			System.out.println("o.array() = " + o.array().length + " "+ Arrays.toString( o.array()));
			
			out.write(o.array());
			
			out.flush();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			InputStream is = socket.getInputStream(); 
			
//			ByteBuffer buffer = ByteBuffer.allocate(0);
//			
//			while(is.available() > 0)
// 			{
//				byte [] array = new byte[128];
//				is.read(array);
//				
//				buffer.put(array);
//			}
			
			System.out.println("is.available() = " + is.available() + " legnth = " + in.readLine().getBytes().length);
			
//			int length = is.available();
//			if(length <= 128)
//				length = 128;
			
			
			byte [] array = new byte[1024];
			is.read(array);
			
			ByteBuffer buffer = ByteBuffer.wrap(array);
			
 			return buffer;
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public void close()
	{
		try 
		{
			if(this.socket.isConnected())
				this.socket.close();
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		
		ArrayList<Integer> list = new ArrayList();
		list.add(new Integer(1));
		
		boolean n = list.contains(1);
		
		System.out.println(n);
			
			
			
			
//		JSONObject o = JSONObject.parseObject("{\"protocolNo\":110001,\"platfromId\":\"1\",\"userId\":\"201706261537ssdds42\",\"channelId\":\"3000001\"}");
//		
////		long startTime = System.currentTimeMillis();
//		SocketClient client1 = new SocketClient("192.168.1.55", 20012);
//		
//		
//		byte [] data = o.toJSONString().getBytes();
//		
//		int bodyLength = data.length;
//		int totalLength = 4 + 2 + 1 + 4 + bodyLength;
//		
//		
//		ByteBuffer buf = ByteBuffer.allocate(totalLength).order(ByteOrder.LITTLE_ENDIAN);
//		buf.putInt(totalLength);
//		buf.putShort((short)101);
//		buf.put((byte)0);
//		buf.putInt(bodyLength);
//		buf.put(data);
//		
//		Object result = client1.send(buf);
//		
//		System.out.println("sss" + result);
	}

	public boolean isClose()
	{
		return socket.isClosed();
	}
}
