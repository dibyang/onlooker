package net.xdob.onlooker;

import java.nio.ByteBuffer;

public class TermIndex {
  private final long term;
  private final long index;

  public TermIndex(long term, long index) {
    this.term = term;
    this.index = index;
  }

  public long getTerm() {
    return term;
  }

  public long getIndex() {
    return index;
  }

  public byte[] toBytes(){
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.putLong(0,term);
    buffer.putLong(8, index);
    return buffer.array();
  }

  public static TermIndex of(long term, long index){
    return new TermIndex(term, index);
  }

  public static TermIndex of(byte[] bytes){
    if(bytes.length!=16){
      throw new IllegalArgumentException("bytes length must be 16.");
    }
    ByteBuffer wrap = ByteBuffer.wrap(bytes);
    return new TermIndex(wrap.getLong(0), wrap.getLong(8));
  }

}
