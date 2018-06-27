package game.tools.net.netty4.deencode;

import java.util.List;

import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Decode;
import game.tools.net.netty4.protocol.INetty4Protocol;
import game.tools.net.netty4.protocol.Netty4ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ProtoBufDecode extends Netty4Decode
{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,List<Object> out) throws Exception 
	{
		ByteBuf buffer = in;
		String content = null;
		
		try 
		{
			if (buffer.readableBytes() < 4) 
				return ;
			
			buffer.markReaderIndex();
			
			/*
			 * 4byte[length]+2byte[short:commandId]+byte[protocalType]+4byte[stringlength]+json[string]
			 */
			int bodyLength = readInt(buffer);			//协议总长
			
			if(buffer.readableBytes() < bodyLength)		//数据体长度不对
			{
				buffer.resetReaderIndex();
				return ;
			}
			
			byte[] data = new byte[bodyLength];
			buffer.readBytes(data, 0, data.length);
			
			out.add(data);
			
			Netty4ProtocolHandler.channelHandler(ctx.channel() , data , "channelDecode");
		}
		catch (Exception e) 
		{
			LogUtil.error(content , e);
			System.err.println(content+"\n");
			e.printStackTrace();
		}		
	}
	

	private static int getInt(byte[] b) {
		int res = 0;
		int bLen = b.length;
		if (bLen < 5) {// int 最大到4个字节
			for (int i = 0; i < bLen; i++) {
				res += (b[i] & 0xFF) << (8 * i);
			}
		}
		return res;
	}
	
	private static int readInt(ByteBuf buffer ) 
	{
		byte [] b = new byte[4];
		buffer.readBytes(b);
		
		return byteToInt(b);
	}
	
	public static int readInt(byte [] b) 
	{
		return byteToInt(b);
	}
	
	public static int byteToInt(byte [] b)
	{
		int res = 0;
		int bLen = b.length;
		if (bLen < 5) {	// int 最大到4个字节
			for (int i = 0; i < bLen; i++) {
				res += (b[i] & 0xFF) << (8 * i);
			}
		}
		return res;
	}
	
	private static short readShort(ByteBuf buffer ) 
	{
		byte [] b = new byte[2];
		buffer.readBytes(b);
		return byteToShort(b , true); 
	}
	
	public final static short byteToShort(byte[] buf, boolean asc) 
	{
	    if (buf == null) 
	      throw new IllegalArgumentException("byte array is null!");
	    if (buf.length > 2) 
	      throw new IllegalArgumentException("byte array size > 2 !");
	    
	    short r = 0;
	    if (asc)		
	      for (int i = buf.length - 1; i >= 0; i--)
	      {
	        r <<= 8;r |= (buf[i] & 0x00ff);
	      }
	    else
	      for (int i = 0; i < buf.length; i++) 
	      {
	        r <<= 8;r |= (buf[i] & 0x00ff);
	      }
	    return r;
	}


	@Override
	public Netty4Decode clone() 
	{
		return new ProtoBufDecode();
	}
}