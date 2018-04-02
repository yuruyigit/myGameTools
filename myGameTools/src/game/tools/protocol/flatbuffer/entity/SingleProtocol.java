// automatically generated, do not modify

package game.tools.protocol.flatbuffer.entity;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class SingleProtocol extends Table {
  public static SingleProtocol getRootAsSingleProtocol(ByteBuffer _bb) { return getRootAsSingleProtocol(_bb, new SingleProtocol()); }
  public static SingleProtocol getRootAsSingleProtocol(ByteBuffer _bb, SingleProtocol obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public SingleProtocol __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int type() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public byte content(int j) { int o = __offset(6); return o != 0 ? bb.get(__vector(o) + j * 1) : 0; }
  public int contentLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer contentAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }

  public static int createSingleProtocol(FlatBufferBuilder builder,
      int type,
      int contentOffset) {
    builder.startObject(2);
    SingleProtocol.addContent(builder, contentOffset);
    SingleProtocol.addType(builder, type);
    return SingleProtocol.endSingleProtocol(builder);
  }

  public static void startSingleProtocol(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addType(FlatBufferBuilder builder, int type) { builder.addInt(0, type, 0); }
  public static void addContent(FlatBufferBuilder builder, int contentOffset) { builder.addOffset(1, contentOffset, 0); }
  public static int createContentVector(FlatBufferBuilder builder, byte[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addByte(data[i]); return builder.endVector(); }
  public static void startContentVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endSingleProtocol(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

