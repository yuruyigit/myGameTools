package game.tools.net.netty4.deencode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Encode;
import game.tools.net.netty4.protocol.INetty4Protocol;
import game.tools.net.netty4.protocol.Netty4ProtocolHandler;
import game.tools.protocol.protobuffer.ProtocolBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


public class ProtoBufEncode extends Netty4Encode
{
	private static final String MSG_ENCODE = "utf-8";
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
	{
		
		try 
		{

			Channel channel = ctx.channel();
			
			ProtocolBuffer buffer = (ProtocolBuffer)msg;
			
			//数据内容体 + 数据长度
			/*
			 * 4byte[length]+2byte[short:commandId]+byte[protocalType]+4byte[stringlength]+json[string]
			 */
			int bodyLength = buffer.getBytes().length + 4;
			ByteBuffer buf = ByteBuffer.allocate(bodyLength + 4).order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(bodyLength);
			buf.putInt(buffer.getProtocolNo());
			buf.put(buffer.getBytes());
			
			byte [] data = buf.array();
			
//			System.out.println("ProtoBufEncode = " + Arrays.toString(data));
			
			out.writeBytes(data);
			
			Netty4ProtocolHandler.channelHandler(ctx.channel() , "channelEncode" , buffer.getProtocolNo() , buffer.getBuild());
		}
		catch(Exception e )
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception 
	{
		byte [] data1 = {123, 112, 114, 111, 116, 111, 99, 111, 108, 78, 111, 58, 49, 48, 48, 48, 51, 44, 100, 97, 116, 97, 58, 91, 123, 110, 97, 109, 101, 58, 39, -27, -68, -96, -28, -72, -119, 39, 125, 44, 123, 110, 97, 109, 101, 58, 39, -26, -99, -114, -27, -101, -101, 39, 125, 93, 125};
		System.out.println(new String(data1 , "utf-8"));
		byte [] data2 = {123, 34, 100, 97, 116, 97, 34, 58, 91, 123, 34, 110, 97, 109, 101, 34, 58, 34, -27, -68, -96, -28, -72, -119, 34, 125, 44, 123, 34, 110, 97, 109, 101, 34, 58, 34, -26, -99, -114, -27, -101, -101, 34, 125, 93, 44, 34, 112, 114, 111, 116, 111, 99, 111, 108, 78, 111, 34, 58, 49, 48, 48, 48, 51, 125, 0};
		System.out.println(new String(data2 , "utf-8"));
	}

	@Override
	public Netty4Encode clone() 
	{
		return new ProtoBufEncode();
	}
}