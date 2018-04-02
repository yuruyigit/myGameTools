package game.tools.net.netty4.protocol;
import com.alibaba.fastjson.JSONObject;

import game.tools.net.netty4.client.sync.LogicDecode;
import game.tools.net.netty4.client.sync.LogicEncode;
import game.tools.net.netty4.server.Netty4Server;
import io.netty.channel.Channel;

class PlayControl
{
	private Channel channel;
	private String id , userId ;
	
	public PlayControl(Channel channel, String string) 
	{
		this.channel = channel;
		this.id = channel.hashCode() + "";
		this.userId = string;
	}

	@Netty4Protocol(protocolNo = 120001)
	public void test1(JSONObject msg)
	{
		System.out.println( this.id + " " + this.userId +  " test1 120001 msg : " + msg);
		channel.writeAndFlush(msg);
	}
	
	@Netty4Protocol(protocolNo = 120002)
	public void test2(JSONObject msg)
	{
		System.out.println( this.id + " " + this.userId +  " test2 120002 msg : " + msg);
		channel.writeAndFlush(msg);
	}
	
}
public class Netty4ProtocolServerMain 
{
	public static void main(String[] args) throws Exception
	{
		Netty4Server ns = new Netty4Server(
				new LogicDecode(), 
				new LogicEncode(),
		new Netty4ProtocolHandler("game.tools.net" , new INetty4Protocol() 
		{
			@Override
			public int getProtocolNo(Object msg) 
			{
				return ((JSONObject)msg).getIntValue("protocolNo");
			}
		}));
		
		ns.start(1111);
	}
	
	@Netty4Protocol(protocolNo = 110001 , isStatic = true)		//如果是登录
	public static void doLogin(Channel channel , Object msg)
	{
		JSONObject o = (JSONObject)msg;
		
		PlayControl playControl = new PlayControl(channel , o.getString("userId"));
		
		Netty4ProtocolHandler.setAttributeKey(channel, playControl);
		
		System.out.println(channel.hashCode()  + " doLogin 110001 msg : " + msg);
		
		channel.writeAndFlush(msg);
	}
	
}


