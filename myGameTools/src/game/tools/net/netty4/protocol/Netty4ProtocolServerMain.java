package game.tools.net.netty4.protocol;
import com.alibaba.fastjson.JSONObject;

import game.tools.net.netty4.deencode.JSONDecode;
import game.tools.net.netty4.deencode.JSONEncode;
import game.tools.net.netty4.deencode.ProtoBufDecode;
import game.tools.net.netty4.deencode.ProtoBufEncode;
import game.tools.net.netty4.server.Netty4Server;
import game.tools.println.ProtocolPrintln;
import game.tools.protocol.protobuffer.Protocol.Login;
import game.tools.protocol.protobuffer.ProtocolBuffer;
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
//	@Netty4Protocol(protocolNo = 120002)
//	public void test2(Login msg)	
//	{
//		System.out.println( this.id + " " + this.userId +  " test2 120002 msg : " + msg);
//		channel.writeAndFlush(new ProtocolBuffer(120002, msg.toBuilder()));
//	}
	
}
public class Netty4ProtocolServerMain 
{
	public static void main(String[] args) throws Exception
	{
		Netty4Server ns = new Netty4Server(
				new JSONDecode(), 
				new JSONEncode(),
//				new ProtoBufDecode(), 
//				new ProtoBufEncode(),
		new Netty4ProtocolHandler("game.tools.net" , new INetty4Protocol() 
		{
			@Override
			public int getProtocolNo(Object msg) 
			{
				return ((JSONObject)msg).getIntValue("protocolNo");
				
//				byte [] bufArray = (byte [])msg;
//				byte [] protoNoArray = {bufArray[0],bufArray[1],bufArray[2],bufArray[3]};
//				return  ProtoBufDecode.readInt(protoNoArray);
			}
			
			@Override
			public void channelEncode(Channel channel, int protocolNo,  Object msg) 
			{
				ProtocolPrintln.protocolPrintln(true , channel ,  "0" , "0" , protocolNo , msg);
				
//				System.out.println("channelEncode " + channel.id());
			}
			
			@Override
			public Object channelReadStart(Channel channel, int protocolNo, Object msg) 
			{
				ProtocolPrintln.protocolPrintln(false , channel ,  "0" , "0" , protocolNo , msg);
				
				return null;
			}
			
			@Override	
			public void channelReadEnd(Channel channel, int protocolNo, Object msg, Object channelReadStartReturnResult) 
			{
//				System.out.println("channelReadEnd protocolNo " + protocolNo + " msg : " + msg.toString().replaceAll("\n", "|"));
			}
			
		},30));
		
		ns.start(1111);
	}
	
	/**
	 * @param channel
	 * @param msg
	 * @param attach
	 */
	@Netty4Protocol(protocolNo = 110001)		//如果是登录
	public void doLogin(Channel channel,  JSONObject msg )
	{
		JSONObject o = (JSONObject)msg;
		
		PlayControl playControl = new PlayControl(channel , o.getString("userId"));
		
		Netty4ProtocolHandler.setPlayControlAttribute(channel, playControl);
		
		System.out.println(channel.hashCode()  + " doLogin 110001 msg : " + msg);
		
		channel.writeAndFlush(msg);
	}
	
//	@Netty4Protocol(protocolNo = 110001)		//如果是登录
//	public void doLogin(Channel channel,  Login msg ) throws Exception
//	{
//		PlayControl playControl = new PlayControl(channel , msg.getUserId());
//		Netty4ProtocolHandler.setPlayControlAttribute(channel, playControl);
//		
//		channel.writeAndFlush(new ProtocolBuffer(110001, msg.toBuilder()));
//	}
}


