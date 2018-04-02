package game.tools.protocol.flatbuffer;

import java.nio.ByteBuffer;

public interface FlatBufferProtocolObject 
{
	/**
	 * 返回一个对应类型的数据体对象
	 * @param type 内容体类型
	 * @param data 协议数据对象内容体
	 * @return 返回一个协议数据对象
	 */
	public <T> T getProtocolObject(int type , ByteBuffer data);
}
