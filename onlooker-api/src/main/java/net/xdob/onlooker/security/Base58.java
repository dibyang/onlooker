package net.xdob.onlooker.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.UUID;

/**
 * BASE58编码支持
 * 
 * @author 杨志坚 Email: dib.yang@gmail.com
 * @since 0.2.0
 */
public class Base58 {

  public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
      .toCharArray();
  private static final int[] INDEXES = new int[128];

  static {
    for (int i = 0; i < INDEXES.length; i++) {
      INDEXES[i] = -1;
    }
    for (int i = 0; i < ALPHABET.length; i++) {
      INDEXES[ALPHABET[i]] = i;
    }
  }

  /**
   * Encodes the given bytes in base58. No checksum is appended.
   */
  public static String encode(byte[] input) {
    if (input.length == 0) {
      return "";
    }
    input = copyOfRange(input, 0, input.length);
    // Count leading zeroes.
    int zeroCount = 0;
    while (zeroCount < input.length && input[zeroCount] == 0) {
      ++zeroCount;
    }
    // The actual encoding.
    byte[] temp = new byte[input.length * 2];
    int j = temp.length;

    int startAt = zeroCount;
    while (startAt < input.length) {
      byte mod = divmod58(input, startAt);
      if (input[startAt] == 0) {
        ++startAt;
      }
      temp[--j] = (byte) ALPHABET[mod];
    }

    // Strip extra '1' if there are some after decoding.
    while (j < temp.length && temp[j] == ALPHABET[0]) {
      ++j;
    }
    // Add as many leading '1' as there were leading zeros.
    while (--zeroCount >= 0) {
      temp[--j] = (byte) ALPHABET[0];
    }

    byte[] output = copyOfRange(temp, j, temp.length);
    try {
      return new String(output, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // Cannot happen.
    }
  }

  public static byte[] decode(String input) throws IllegalArgumentException {
    if (input.length() == 0) {
      return new byte[0];
    }
    byte[] input58 = new byte[input.length()];
    // Transform the String to a base58 byte sequence
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);

      int digit58 = -1;
      if (c >= 0 && c < 128) {
        digit58 = INDEXES[c];
      }
      if (digit58 < 0) {
        throw new IllegalArgumentException("Illegal character " + c + " at " + i);
      }

      input58[i] = (byte) digit58;
    }
    // Count leading zeroes
    int zeroCount = 0;
    while (zeroCount < input58.length && input58[zeroCount] == 0) {
      ++zeroCount;
    }
    // The encoding
    byte[] temp = new byte[input.length()];
    int j = temp.length;

    int startAt = zeroCount;
    while (startAt < input58.length) {
      byte mod = divmod256(input58, startAt);
      if (input58[startAt] == 0) {
        ++startAt;
      }

      temp[--j] = mod;
    }
    // Do no add extra leading zeroes, move j to first non null byte.
    while (j < temp.length && temp[j] == 0) {
      ++j;
    }

    return copyOfRange(temp, j - zeroCount, temp.length);
  }

  public static BigInteger decodeToBigInteger(String input) throws IllegalArgumentException {
    return new BigInteger(1, decode(input));
  }

  //
  // number -> number / 58, returns number % 58
  //
  private static byte divmod58(byte[] number, int startAt) {
    int remainder = 0;
    for (int i = startAt; i < number.length; i++) {
      int digit256 = (int) number[i] & 0xFF;
      int temp = remainder * 256 + digit256;

      number[i] = (byte) (temp / 58);

      remainder = temp % 58;
    }

    return (byte) remainder;
  }

  //
  // number -> number / 256, returns number % 256
  //
  private static byte divmod256(byte[] number58, int startAt) {
    int remainder = 0;
    for (int i = startAt; i < number58.length; i++) {
      int digit58 = (int) number58[i] & 0xFF;
      int temp = remainder * 58 + digit58;

      number58[i] = (byte) (temp / 256);

      remainder = temp % 256;
    }

    return (byte) remainder;
  }

  private static byte[] copyOfRange(byte[] source, int from, int to) {
    byte[] range = new byte[to - from];
    System.arraycopy(source, from, range, 0, range.length);

    return range;
  }

  public static byte[] long2Bytes(long num) {
    byte[] byteNum = new byte[8];
    for (int ix = 0; ix < 8; ++ix) {
      int offset = 64 - (ix + 1) * 8;
      byteNum[ix] = (byte) ((num >> offset) & 0xff);
    }
    return byteNum;
  }

  public static byte[] uuid2Bytes(UUID uuid) {
    byte[] byteNum = new byte[16];
    byte[] mostSigBits = long2Bytes(uuid.getMostSignificantBits());
    byte[] leastSigBits = long2Bytes(uuid.getLeastSignificantBits());
    for (int ix = 0; ix < 8; ++ix) {
      byteNum[ix] = mostSigBits[ix];
      byteNum[8 + ix] = leastSigBits[ix];
    }
    return byteNum;
  }

  public static long bytes2Long(byte[] byteNum) {
    long num = 0;
    if (byteNum.length == 8) {
      for (int ix = 0; ix < 8; ++ix) {
        num <<= 8;
        num |= (byteNum[ix] & 0xff);
      }
    }
    return num;
  }

  public static UUID bytes2Uuid(byte[] byteNum) {
    UUID uuid = null;
    if (byteNum.length == 16) {
      byte[] mostBytes = new byte[8];
      byte[] leastBytes = new byte[8];
      for (int ix = 0; ix < 8; ++ix) {
        mostBytes[ix] = byteNum[ix];
        leastBytes[ix] = byteNum[ix + 8];
      }
      long mostSigBits = bytes2Long(mostBytes);
      long leastSigBits = bytes2Long(leastBytes);
      uuid = new UUID(mostSigBits, leastSigBits);
    }
    return uuid;
  }

  public static String encodeLong(long input) {
    return encode(long2Bytes(input));
  }

  public static long decodeLong(String input) {
    return bytes2Long(decode(input));
  }

  public static String encodeUuid(UUID input) {
    return encode(uuid2Bytes(input));
  }

  public static UUID decodeUuid(String input) {
    return bytes2Uuid(decode(input));
  }

  public static void main(String[] args) {
    // 123456789ABCDEFGHJKLMNPQRSTUVWXYZ
    long time = System.currentTimeMillis();
    int count = 1000000;
    for(int i = 0; i< count; i++) {
      UUID id = UUID.randomUUID();
      //System.out.println("oid=" + id);
      String eid = encodeUuid(id);
      System.out.println("eid=" + eid);
    }
    long offset = System.currentTimeMillis()-time;
    System.out.println("offset = " + offset+"ms");
    double t = 1.0*offset/count;
    System.out.println("t = " + t+"ms");

  }

}
