package game.tools.net.netty4.client.sync;

import io.netty.channel.ChannelHandlerContext;

public interface INettyChannelRead 
{
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception ; 
}
