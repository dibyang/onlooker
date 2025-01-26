package net.xdob.onlooker.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class SHA256 {

  public static final String SHA_256 = "SHA-256";

  public static String sha256String(String data) {
    String s = "";
    byte[] bytes = sha256(data.getBytes(StandardCharsets.UTF_8));
    if(bytes!=null){
      s = byte2Hex(bytes);
    }
    return s;
  }

  public static String sha256String(byte[] data) {
    String s = "";
    byte[] bytes = sha256(data);
    if(bytes!=null){
        s = byte2Hex(bytes);
    }
    return s;
  }

  public static byte[] sha256(String data) throws UnsupportedEncodingException {
    return sha256(data.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] sha256(byte[] data) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance(SHA_256);
      messageDigest.update(data);
      return messageDigest.digest();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 将byte转为16进制
   *
   * @param bytes
   * @return
   */
  private static String byte2Hex(byte[] bytes) {
    StringBuffer stringBuffer = new StringBuffer();
    String temp = null;
    for (int i = 0; i < bytes.length; i++) {
      temp = Integer.toHexString(bytes[i] & 0xFF);
      if (temp.length() == 1) {
        // 1得到一位的进行补0操作
        stringBuffer.append("0");
      }
      stringBuffer.append(temp);
    }
    return stringBuffer.toString();
  }

}
