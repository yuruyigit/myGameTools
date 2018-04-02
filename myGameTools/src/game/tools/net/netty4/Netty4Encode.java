package game.tools.net.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public abstract class Netty4Encode extends MessageToByteEncoder<Object>
{

	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2)
			throws Exception {
		
	}
	
	
	/**
	 * 返回自己实现对象的复本
	 */
	public abstract Netty4Encode clone();
	
}