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
	 * 消息入口特殊接收处理
	 * @param channel
	 * @param msg
	 */
	public default void channelRead(Channel channel , Object msg){};
	
	

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
	 * channel连接异常
	 * @param channel
	 */
	public default void exceptionCaught(Channel channel){};
}
