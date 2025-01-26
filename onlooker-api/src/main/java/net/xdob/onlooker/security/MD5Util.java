package net.xdob.onlooker.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @since 0.2.0
 */
public abstract class MD5Util {

  public final static String MD5(String s) {
    byte[] btInput = s.getBytes();
    byte[] md = md5(btInput);
    return bytesToHexString(md);
  }

  public final static String MD5(byte[] bytes) {
    byte[] md = md5(bytes);
    return bytesToHexString(md);
  }

  public final static byte[] md5(byte[] bytes) {
    try {
      // 获得MD5摘要算法的 MessageDigest 对象
      MessageDigest mdInst = MessageDigest.getInstance("MD5");
      // 使用指定的字节更新摘要
      mdInst.update(bytes);
      // 获得密文
      return mdInst.digest();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return bytes;
  }

  public final static String MD5BASE58(String s) {

    try {
      byte[] btInput = s.getBytes();
      // 获得MD5摘要算法的 MessageDigest 对象
      byte[] md = md5(btInput);

      return Base58.encode(md);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /*
   * Convert byte[] to hex
   * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
   * 
   * @param src byte[] data
   * 
   * @return hex string
   */
  public static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder("");
    if (src == null || src.length <= 0) {
      return null;
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

  /**
   * Convert hex string to byte[]
   * 
   * @param hexString
   *          the hex string
   * @return byte[]
   */
  public static byte[] hexStringToBytes(String hexString) {
    if (hexString == null || hexString.equals("")) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  /**
   * Convert char to byte
   * 
   * @param c
   *          char
   * @return byte
   */
  private static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }

  public static void main(String[] args) {
    System.out.println(MD5Util.MD5("20121221"));
    System.out
        .println(MD5Util
            .MD5BASE58("app_id=9XNNXe66zOlSassjSKD5gry9BiN61IUEi8IpJmjBwvU07RXP0J3c4GnhZR3GKhMHa1A=format=jsontimestamp=2011-06-21 17:18:09uid=6741116727e1be4fdcaa83d7f61c489994ff6ed6"));
    System.out
        .println(MD5Util
            .MD5("app_id=9XNNXe66zOlSassjSKD5gry9BiN61IUEi8IpJmjBwvU07RXP0J3c4GnhZR3GKhMHa1A=format=jsontimestamp=2011-06-21 17:18:09uid=6741116727e1be4fdcaa83d7f61c489994ff6ed6"));

  }

}
