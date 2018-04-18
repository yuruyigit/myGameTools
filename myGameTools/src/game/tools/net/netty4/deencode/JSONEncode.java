package game.tools.net.netty4.deencode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.alibaba.fastjson.JSONObject;

import game.tools.log.LogUtil;
import game.tools.net.netty4.Netty4Encode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;


public class JSONEncode extends Netty4Encode
{
	private static final String MSG_ENCODE = "utf-8";
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
	{
		
		try 
		{
			JSONObject o = (JSONObject)msg;
			o.put("srvTime", System.currentTimeMillis());
			
			String content = o.toString() + "\0";
//			String content = JSON.toJSONString(o ,SerializerFeature.DisableCircularReferenceDetect) + "\0";
			byte[] data = content.getBytes(MSG_ENCODE);
			
			
//			System.out.println("data1 = " +Arrays.toString(data));
//			
//			data = XXTEA.encrypt(data , Protocol.PROTOCOL_SECRET_KEY);
//			
//			System.out.println("data2 = " +Arrays.toString(data));
			
			
			//数据内容体 + 数据长度
			
			/*
			 * 4byte[length]+2byte[short:commandId]+byte[protocalType]+4byte[stringlength]+json[string]
			 */
			
			int bodyLength = data.length;
			int totalLength = bodyLength + 7;
			ByteBuffer buf = ByteBuffer.allocate(totalLength + 4 ).order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(totalLength);
			buf.putShort((short)104);
			buf.put((byte)0);
			buf.putInt(bodyLength);
			buf.put(data);
			
//			System.out.println("totalLength = " + totalLength + " " +Arrays.toString(buf.array()));
			data = buf.array();
			
//			System.out.println("non length = " + data.length);
//			if(data.length >= 1000)			//解密的数据，长度大于1000，先解密在解压
//			{
//				byte [] data1 = ZLibUtils.compress(data);
//				System.out.println("zip length = " + data1.length);
//			}
			
			out.writeBytes(data);
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
		return new JSONEncode();
	}
}