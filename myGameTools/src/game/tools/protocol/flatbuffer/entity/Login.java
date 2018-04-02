// automatically generated, do not modify

package game.tools.protocol.flatbuffer.entity;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Login extends Table {
  public static Login getRootAsLogin(ByteBuffer _bb) { return getRootAsLogin(_bb, new Login()); }
  public static Login getRootAsLogin(ByteBuffer _bb, Login obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Login __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String uid() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer uidAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public String pass() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer passAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public int age() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createLogin(FlatBufferBuilder builder,
      int uidOffset,
      int passOffset,
      int age) {
    builder.startObject(3);
    Login.addAge(builder, age);
    Login.addPass(builder, passOffset);
    Login.addUid(builder, uidOffset);
    return Login.endLogin(builder);
  }

  public static void startLogin(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addUid(FlatBufferBuilder builder, int uidOffset) { builder.addOffset(0, uidOffset, 0); }
  public static void addPass(FlatBufferBuilder builder, int passOffset) { builder.addOffset(1, passOffset, 0); }
  public static void addAge(FlatBufferBuilder builder, int age) { builder.addInt(2, age, 0); }
  public static int endLogin(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

