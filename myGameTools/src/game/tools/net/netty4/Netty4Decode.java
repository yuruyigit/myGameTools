package game.tools.net.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public abstract class Netty4Decode extends ByteToMessageDecoder
{

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1,
			List<Object> arg2) throws Exception {
		
	}
	
	
	/**
	 * 返回自己实现对象的复本
	 */
	public abstract Netty4Decode clone();
}