package game.tools.net.netty4.protocol;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;

import game.tools.net.netty4.client.sync.INettyChannelRead;
import game.tools.net.netty4.client.sync.Netty4ClientSync;
import game.tools.net.netty4.deencode.JSONDecode;
import game.tools.net.netty4.deencode.JSONEncode;
import game.tools.net.netty4.deencode.ProtoBufDecode;
import game.tools.net.netty4.deencode.ProtoBufEncode;
import game.tools.protocol.protobuffer.Protocol.Login;
import game.tools.protocol.protobuffer.ProtocolBuffer;
import io.netty.channel.ChannelHandlerContext;

public class Netty4ProtocolClientMain 
{
	public static void main(String[] args) throws Exception
	{
		jsonProtocolTest();
//		protobufProtocolTest();
	}
	
	private static void protobufProtocolTest()
	{
		Runnable r = new Runnable() 
		{
			@Override
			public void run() 
			{
				ProtocolBuffer protocolBuffer = new ProtocolBuffer(110001, 
						Login.newBuilder()
						.setUserId("2017062613s37ssddddsffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff42")
						.setChannl("3000001")
						.setPlaform("1"));
				
				Netty4ClientSync client = new Netty4ClientSync("127.0.0.1", 1111, new ProtoBufDecode() , new ProtoBufEncode() , new INettyChannelRead() 
				{
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
						client.send(new ProtocolBuffer(110001, 
								Login.newBuilder()
								.setUserId("2017062613s37ssdddds42")
								.setChannl("3000001")
								.setPlaform("1")));
						client.send(new ProtocolBuffer(120002, 
								Login.newBuilder()
								.setUserId("2017062613s37ssdddds42")
								.setChannl("3000001")
								.setPlaform("1")));
					}
					catch (Exception e) 
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
	}
	
	private static void jsonProtocolTest()
	{
		Runnable r = new Runnable() 
		{
			@Override
			public void run() 
			{
				JSONObject o = JSONObject.parseObject("{\"protocolNo\":110001,\"platfromId\":\"1\",\"userId\":\"2017062613s37ssdddds42\",\"channelId\":\"3000001\"}");
				
				Netty4ClientSync client = new Netty4ClientSync("127.0.0.1", 1111, new JSONDecode() , new JSONEncode() , new INettyChannelRead() 
				{
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
						Object result = client.send(o);
						o = JSONObject.parseObject("{\"protocolNo\":120001}");
						client.send(o);
						Thread.sleep(300);
						o = JSONObject.parseObject("{\"protocolNo\":120002}");
						client.send(o);
					}
					catch (Exception e) 
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
	}
}


