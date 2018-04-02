package game.tools.net.netty4.client.sync;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import game.tools.gzip.ZLibUtils;
import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Decode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LogicDecode extends Netty4Decode
{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,List<Object> out) throws Exception 
	{
		ByteBuf buffer = in;
		String content = null;
		
//		if(buffer.hasArray())
//			System.out.println(Arrays.toString(buffer.array()));
//		else
//			System.out.println("Not Array");
		
		try 
		{
			if (buffer.readableBytes() < 4) 
				return ;
			
			buffer.markReaderIndex();
			
			/*
			 * 4byte[length]+2byte[short:commandId]+byte[protocalType]+4byte[stringlength]+json[string]
			 */
			int totalLength = readInt(buffer);			//协议总长
			
			if(buffer.readableBytes() < totalLength)		//数据体长度不对
			{
				buffer.resetReaderIndex();
				return ;
			}
			
			buffer.readBytes(new byte[2]);
			buffer.readByte();
			
			
			int bodyLength = readInt(buffer);
			byte[] data = new byte[bodyLength];
			buffer.readBytes(data, 0, data.length);
			
//			System.out.println("bodyLength1 = " + bodyLength + " " +Arrays.toString(data));
//			
//			data = XXTEA.decrypt((byte[])data , Protocol.PROTOCOL_SECRET_KEY);
//			
//			System.out.println("bodyLength2 = " + bodyLength + " " +Arrays.toString(data));
			
//			if(data.length >= 1000)			//解密的数据，长度大于1000，先解密在解压
//				data = ZLibUtils.compress(data);
			
			content = new String(data, "utf-8").trim();
			
			JSONObject o = JSONObject.parseObject(content);
			
			out.add(o);
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
	
	private static int byteToInt(byte [] b)
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
		return new LogicDecode();
	}
}