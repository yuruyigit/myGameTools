package game.tools.protocol.flatbuffer;

import com.google.flatbuffers.FlatBufferBuilder;

interface SignleProtocolPkg
{
	/**
	 * @param builder 使用的flatBuilder对象
	 * @return 返回的是SignleProtocol中 protocolType , pkgObj 这两个的内存offset
	 */
	int [] create(FlatBufferBuilder builder);
}