package net.xdob.onlooker.security;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author yangzj
 * @date 2021/6/8
 */
public enum UuidHelper {
  h;

  public static final int UUID_SHORT_LEN = 32;

  public String base58Uuid(UUID uuid) {
    ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    return Base58.encode(buffer.array());
  }

  public String base58Uuid(String value) {
    if(value.length()>= UUID_SHORT_LEN) {
      if(value.length()==UUID_SHORT_LEN){
        value = value.substring(0,8)+"-"+value.substring(8,12)
          +"-"+value.substring(12,16)+"-"+value.substring(16,20)
          +"-"+value.substring(20,32);
      }
      UUID uuid = UUID.fromString(value);
      return base58Uuid(uuid);
    }
    return value;
  }

  public UUID from(String value) {
    if(value.length()>= UUID_SHORT_LEN) {
      if(value.length()==UUID_SHORT_LEN){
        value = value.substring(0,8)+"-"+value.substring(8,12)
          +"-"+value.substring(12,16)+"-"+value.substring(16,20)
          +"-"+value.substring(20,32);
      }
      UUID uuid = UUID.fromString(value);
      return uuid;
    }else{
      byte[] byUuid = Base58.decode(value);
      ByteBuffer bb = ByteBuffer.wrap(byUuid);
      UUID uuid = new UUID(bb.getLong(), bb.getLong());
      return uuid;
    }
  }

  public UUID uuid() {
    return UUID.randomUUID();
  }

  public String base58Uuid() {
    return base58Uuid(uuid());
  }

  public static void main(String[] args) {
    String uid = UuidHelper.h.base58Uuid();
    System.out.println("uid = " + uid);
    final UUID uuid = UuidHelper.h.from(uid);
    System.out.println("uuid = " + uuid);
    String id = uuid.toString().replaceAll("-","");
    System.out.println("id = " + id);
    final String s = UuidHelper.h.base58Uuid(id);
    System.out.println("s = " + s);
  }

}
