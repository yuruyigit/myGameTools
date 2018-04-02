package game.tools.net.netty4;

import game.tools.net.netty4.Netty4Handler;
import game.tools.net.netty4.Netty4Initializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class StringInitializer extends Netty4Initializer 
{

	public StringInitializer(Netty4Handler handler) 
	{
		super(handler);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception 
	{
		ChannelPipeline pipeline = ch.pipeline();
	    pipeline.addLast(new StringEncoder());
	    pipeline.addLast(new StringDecoder());
	    pipeline.addLast(nettyHandler);
	}

}
