// automatically generated, do not modify

package game.tools.protocol.flatbuffer.entity;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Create extends Table {
  public static Create getRootAsCreate(ByteBuffer _bb) { return getRootAsCreate(_bb, new Create()); }
  public static Create getRootAsCreate(ByteBuffer _bb, Create obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Create __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long uid() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public int pass() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public String token() { int o = __offset(8); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer tokenAsByteBuffer() { return __vector_as_bytebuffer(8, 1); }
  public double score() { int o = __offset(10); return o != 0 ? bb.getDouble(o + bb_pos) : 0; }
  public float attValue() { int o = __offset(12); return o != 0 ? bb.getFloat(o + bb_pos) : 0; }
  public int atk() { int o = __offset(14); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createCreate(FlatBufferBuilder builder,
      long uid,
      int pass,
      int tokenOffset,
      double score,
      float attValue,
      int atk) {
    builder.startObject(6);
    Create.addScore(builder, score);
    Create.addUid(builder, uid);
    Create.addAtk(builder, atk);
    Create.addAttValue(builder, attValue);
    Create.addToken(builder, tokenOffset);
    Create.addPass(builder, pass);
    return Create.endCreate(builder);
  }

  public static void startCreate(FlatBufferBuilder builder) { builder.startObject(6); }
  public static void addUid(FlatBufferBuilder builder, long uid) { builder.addLong(0, uid, 0); }
  public static void addPass(FlatBufferBuilder builder, int pass) { builder.addInt(1, pass, 0); }
  public static void addToken(FlatBufferBuilder builder, int tokenOffset) { builder.addOffset(2, tokenOffset, 0); }
  public static void addScore(FlatBufferBuilder builder, double score) { builder.addDouble(3, score, 0); }
  public static void addAttValue(FlatBufferBuilder builder, float attValue) { builder.addFloat(4, attValue, 0); }
  public static void addAtk(FlatBufferBuilder builder, int atk) { builder.addInt(5, atk, 0); }
  public static int endCreate(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

