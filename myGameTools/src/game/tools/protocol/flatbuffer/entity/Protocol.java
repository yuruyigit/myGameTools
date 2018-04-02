// automatically generated, do not modify

package game.tools.protocol.flatbuffer.entity;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Protocol extends Table {
  public static Protocol getRootAsProtocol(ByteBuffer _bb) { return getRootAsProtocol(_bb, new Protocol()); }
  public static Protocol getRootAsProtocol(ByteBuffer _bb, Protocol obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Protocol __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int protocolNo() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public SingleProtocol protocolArray(int j) { return protocolArray(new SingleProtocol(), j); }
  public SingleProtocol protocolArray(SingleProtocol obj, int j) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int protocolArrayLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }

  public static int createProtocol(FlatBufferBuilder builder,
      int protocolNo,
      int protocolArrayOffset) {
    builder.startObject(2);
    Protocol.addProtocolArray(builder, protocolArrayOffset);
    Protocol.addProtocolNo(builder, protocolNo);
    return Protocol.endProtocol(builder);
  }

  public static void startProtocol(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addProtocolNo(FlatBufferBuilder builder, int protocolNo) { builder.addInt(0, protocolNo, 0); }
  public static void addProtocolArray(FlatBufferBuilder builder, int protocolArrayOffset) { builder.addOffset(1, protocolArrayOffset, 0); }
  public static int createProtocolArrayVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startProtocolArrayVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endProtocol(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishProtocolBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

