package game.tools.net.netty4;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ObjectInitializer extends Netty4Initializer
{

	public ObjectInitializer(Netty4Handler handler) {
		super(handler);
	}
	
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception 
	{
		ChannelPipeline pipeline = arg0.pipeline();
		pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(nettyHandler);
	}
	
}
