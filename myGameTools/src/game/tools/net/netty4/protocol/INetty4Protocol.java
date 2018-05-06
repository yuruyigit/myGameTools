package game.tools.net.netty4.protocol;

import io.netty.channel.Channel;

public interface INetty4Protocol 
{
	/**
	 * 返回获取包里的协议号， 用于进行协议号函数处理
	 * @param msg
	 * @return
	 */
	public int getProtocolNo(Object msg);
	
	
	/**
	 * 如遇处理消息函数，拦截的异常处理。
	 * @param channel
	 * @param e
	 */
	public default void channelReadException(Channel channel , Exception e) {};
	
	
	
	/**
	 * 消息入口接收处理函数开始接口。主要用于特殊接收处理。
	 * @param channel
	 * @param msg
	 */
	public default Object channelReadStart(Channel channel , int protocolNo ,  Object msg){	return null;	};
	
	/**
	 * 消息入口接收处理函数结束接口。主要用于特殊接收处理。
	 * @param channel
	 * @param msg
	 * @param handlerStartReturnResult 该参数由channelHandlerStart返回结果的对象，回传入于该函数。
	 * @return
	 */
	public default void channelReadEnd(Channel channel ,  int protocolNo ,  Object msg, Object channelReadStartReturnResult){};
	
	

	/**
	 * 新的连接激活
	 * @param channel
	 */
	public default void channelActive(Channel channel){};
	
	

	/**
	 * 关闭连接或失去连接
	 * @param channel
	 */
	public default void channelInactive(Channel channel){};
	
	/**
	 * 通道进行加密时
	 * @param channel
	 */
	public default void channelEncode(Channel channel, Object msg){};
	
	

	/**通道进行解密时
	 * @param channel
	 */
	public default void channelDecode(Channel channel , Object msg){};
	

	/**
	 * channel连接异常
	 * @param channel
	 */
	public default void exceptionCaught(Channel channel){}


}
