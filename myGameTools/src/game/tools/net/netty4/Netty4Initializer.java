package game.tools.net.netty4;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

//netty 粘包问题处理
//key words: netty 粘包 解包 半包 TCP
//
//一般TCP粘包/拆包解决办法
//定长消息，例如每个报文长度固定，不够补空格
//使用回车换行符分割，在包尾加上分割符，例如Ftp协议
//消息分割，头为长度（消息总长度或消息体长度），通常头用一个int32表示
//复杂的应用层协议
//netty的几种解决方案
//特殊分隔符解码器：DelimiterBasedFrameDecoder
//客户端发送消息
//
//String message = "netty is a nio server framework &"
//                +"which enables quick and easy development &"
//                +"of net applications such as protocol &"
//                +"servers and clients!";
//服务端添加解码器：DelimiterBasedFrameDecoder
//
//ByteBuf delimiter = Unpooled.copiedBuffer("&".getBytes());
//ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));

////1024表示单条消息的最大长度，解码器在查找分隔符的时候，达到该长度还没找到的话会抛异常
//ch.pipeline().addLast(new StringDecoder());
//....
//ch.pipeline().addLast(new StringEncoder());
//打印输出：
//
//接收消息：[netty is a nio server framework ]
//接收消息：[which enables quick and easy development ]
//接收消息：[of net applications such as protocol]
//接收消息：[servers and clients!]
//参数解释：
//
//public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf delimiter) {
//    this(maxFrameLength, true, delimiter);
//}
//maxFrameLength：解码的帧的最大长度
//
//stripDelimiter：解码时是否去掉分隔符
//
//failFast：为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
//delimiter：分隔符
public class Netty4Initializer extends ChannelInitializer<SocketChannel> {
	
	protected Netty4Handler nettyHandler ;
	
//    protected ChannelHandler [] handlers;
//    public Netty4Initializer(Netty4Handler handler , ChannelHandler ...handlers) 
//    {
//        this.nettyHandler =  handler;
//        this.handlers = handlers;
//    }
//    
    public Netty4Initializer(Netty4Handler handler) 
    {
        this.nettyHandler =  handler;
    }
    

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception 
	{
		
//		添加		DelimiterBasedFrameDecoder		这个对象，可以依据分割符进行粘包处理
//		pipeline.addLast(new DelimiterBasedFrameDecoder(512,delimiter));
//		pipeline.addLast(new StringDecoder());
//		pipeline.addLast(new StringEncoder());
//		pipeline.addLast(nettyHandler);
		
		
//		ChannelPipeline pipeline = arg0.pipeline();
//		for (int i = 0; i < handlers.length; i++) 
//		    pipeline.addLast(handlers[i]);
//		pipeline.addLast(nettyHandler);
	}


	public Netty4Handler getNettyHandler() {		return nettyHandler;	}
}
