package net.xdob.onlooker.security;

import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 *
 * @author 杨志坚 Email: dib.yang@gmail.com
 * @since 0.2.0
 */
public class RSAUtil {
  static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);

  public static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
  public static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
  public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
  public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
  public static final String SHA256_WITH_RSA = "SHA256withRSA";
  public static final String MD5_WITH_RSA = "MD5withRSA";
  public static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1PADDING";
  private SecureRandom secrand = new SecureRandom();
  public Cipher rsaCipher;

  public static final String RSA = "RSA";// RSA、RSA/ECB/PKCS1Padding

  // public static String
  // Algorithm="RSA/ECB/PKCS1Padding";//RSA、RSA/ECB/PKCS1Padding

  private RSAUtil() {
  }

  public static RSAUtil create() {
    return new RSAUtil();
  }

  /**
   * 生成密钥对
   */
  public KeyPair generateKeyPair() {
    return generateKeyPair(2048);
  }

  /**
   * 生成密钥对
   *
   * @return KeyPair
   */
  public KeyPair generateKeyPair(int keySize) {
    KeyPair keyPair = null;
    try {
      KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
      // 密钥位数
      keyPairGen.initialize(keySize);
      // 密钥对
      keyPair = keyPairGen.generateKeyPair();



    } catch (Exception e) {
      System.err.println("Exception:" + e.getMessage());
    }
    return keyPair;
  }

  public int getKeySize(Key key) {
    if (key instanceof PrivateKey) {
      return getKeySize((PrivateKey) key);
    }
    if (key instanceof PublicKey) {
      return getKeySize((PublicKey) key);
    }
    return 0;
  }

  public int getKeySize(PrivateKey key) {
    String algorithm = key.getAlgorithm(); // 获取算法
    BigInteger prime = null;
    if (RSA.equals(algorithm)) { // 如果是RSA加密
      RSAPrivateKey keySpec = (RSAPrivateKey) key;
      prime = keySpec.getModulus();
    }
    return prime.toString(2).length();
  }

  public int getKeySize(PublicKey key) {
    String algorithm = key.getAlgorithm(); // 获取算法
    BigInteger prime = null;
    if (RSA.equals(algorithm)) { // 如果是RSA加密
      RSAPublicKey keySpec = (RSAPublicKey) key;
      prime = keySpec.getModulus();
    }
    return prime.toString(2).length();
  }

  public PublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] keyBytes;
    key = getKey(key,BEGIN_PUBLIC_KEY,END_PUBLIC_KEY);
    keyBytes = BaseEncoding.base64().decode(key);

    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(RSA);
    return keyFactory.generatePublic(keySpec);
  }

  private String getKey(String key,String begin,String end) {
    if(key.startsWith(begin)){
      key = key.substring(begin.length());
    }
    int index = key.indexOf(end);
    if(index>0){
      key = key.substring(0,index);
    }
    key = key.replaceAll("\n","")
        .replaceAll("\r","");
    return key;
  }

  public PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] keyBytes;
    key = getKey(key, BEGIN_PRIVATE_KEY, END_PRIVATE_KEY);
    keyBytes = BaseEncoding.base64().decode(key);

    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(RSA);
    return keyFactory.generatePrivate(keySpec);
  }

  public String getKeyString(Key key) {
    byte[] keyBytes = key.getEncoded();
    return BaseEncoding.base64().encode(keyBytes);
  }

  /**
   * 获取私钥，返回base64处理后的字符串
   * @param keyPair
   * @return
   */
  public String getPublicKeyWithBase64(KeyPair keyPair) {
    byte[] publicKey = keyPair.getPublic().getEncoded();
    return BaseEncoding.base64().encode(publicKey);
  }

  /**
   * 获取公钥，返回base64处理后的字符串
   * @param keyPair
   * @return
   */
  public String getPrivateKeyWithBase64(KeyPair keyPair) {
    byte[] privateKey = keyPair.getPrivate().getEncoded();
    return BaseEncoding.base64().encode(privateKey);
  }

  /**
   * 公钥加密
   *
   * @param key 密钥
   * @param data 待加密数据
   * @return byte[] 加密数据
   */
  public String encryptByPublicKey(String key, String data) {
    try {
      PublicKey publicKey = getPublicKey(key);
      return encode(publicKey, data);
    }catch (Exception e){
      LOG.warn("", e);
    }
    return null;
  }

  /**
   * 私钥解密
   *
   * @param key 密钥
   * @param data 待加密数据
   * @return byte[] 加密数据
   */
  public String decryptByPrivateKey(String key, String data) {
    try {
      PrivateKey privateKey = getPrivateKey(key);
      return decode(privateKey, data);
    }catch (Exception e){
      LOG.warn("", e);
    }
    return null;
  }

  public String encode(Key key, String content) throws NoSuchPaddingException, IOException {
    byte[] data = content.getBytes(StandardCharsets.UTF_8);
    return BaseEncoding.base64().encode(encode(key, data));
  }

  public byte[] encode(Key key, byte[] data) throws NoSuchPaddingException, IOException {
    try {
      rsaCipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
    } catch (NoSuchAlgorithmException e) {
      LOG.warn("", e);
    }
    try {
      rsaCipher.init(Cipher.ENCRYPT_MODE, key, secrand);
      return rsaCipher.doFinal(data);

    } catch (InvalidKeyException e) {
      throw new IOException("InvalidKey", e);
    }
    catch (IllegalBlockSizeException e) {
      throw new IOException("IllegalBlockSize", e);
    } catch (BadPaddingException e) {
      throw new IOException("BadPadding", e);
    }
  }

  /**
   * BASE64解码，再RSA解密
   *
   * @param key
   * @param content
   * @return String
   * @throws NoSuchPaddingException
   * @throws IOException
   */
  public String decode(Key key, String content) throws NoSuchPaddingException, IOException {
    byte[] data = null;
    try {
      data = BaseEncoding.base64().decode(content);
    } catch (Exception e1) {
      LOG.warn("", e1);
    }
    return new String(decode(key, data), StandardCharsets.UTF_8);
  }

  public byte[] decode(Key key, byte[] data) throws NoSuchPaddingException, IOException {

    try {
      rsaCipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
    } catch (NoSuchAlgorithmException e) {
      LOG.warn("", e);
    }
    try {
      rsaCipher.init(Cipher.DECRYPT_MODE, key, secrand);

      return rsaCipher.doFinal(data);
    } catch (InvalidKeyException e) {
      throw new IOException("InvalidKey", e);
    }
    catch (IllegalBlockSizeException e) {
      throw new IOException("IllegalBlockSize", e);
    } catch (BadPaddingException e) {
      throw new IOException("BadPadding", e);
    }
  }

  /**
   * MD5withRSA签名
   *
   * @param privateKeyStr 私钥
   * @param data 待签名数据
   * @return 签名(base64的字符串)
   */
  public String signMD5withRSA(String privateKeyStr, String data) {
    return sign(MD5_WITH_RSA, privateKeyStr, data);
  }

  /**
   * MD5withRSA数字签名验证.
   *
   * @param publicKeyStr 公钥
   * @param data 待签名数据
   * @param sign 签名(base64的字符串)
   * @return 是否验签通过
   */
  public boolean verifyMD5withRSA(String publicKeyStr, String data, String sign){
    return verify(MD5_WITH_RSA, publicKeyStr, data, sign);
  }


  /**
   * SHA256withRSA签名
   *
   * @param privateKeyStr 私钥
   * @param data 待签名数据
   * @return 签名(base64的字符串)
   */
  public String signSha256withRSA(String privateKeyStr, String data) {
    return sign(SHA256_WITH_RSA, privateKeyStr, data);
  }

  /**
   * 签名
   * @param algorithm 签名算法(SHA256withRSA/MD5withRSA)
   * @param privateKeyStr 私钥
   * @param data 待签名数据
   * @return 签名(base64的字符串)
   */
  public String sign(String algorithm, String privateKeyStr, String data) {
    try {
      Signature Sign = Signature.getInstance(algorithm);
      PrivateKey privateKey = getPrivateKey(privateKeyStr);
      Sign.initSign(privateKey);
      Sign.update(data.getBytes());
      byte[] signed = Sign.sign();
      return BaseEncoding.base64().encode(signed);
    } catch (Exception e) {
      LOG.warn("", e);
    }
    return null;
  }


  /**
   * SHA256withRSA数字签名验证.
   *
   * @param publicKeyStr    公钥
   * @param data   待签名数据
   * @param sign   签名(base64的字符串)
   * @return true, if successful
   */
  public boolean verifySHA256WithRSA(String publicKeyStr, String data, String sign){
    return verify(SHA256_WITH_RSA, publicKeyStr, data, sign);
  }

  /**
   * 数字签名验证
   * @param algorithm 签名算法(SHA256withRSA/MD5withRSA)
   * @param publicKeyStr 公钥
   * @param data 待签名数据
   * @param sign 签名(base64的字符串)
   * @return true, if successful
   */
  public boolean verify(String algorithm, String publicKeyStr, String data, String sign) {
    try {
      PublicKey publicKey = getPublicKey(publicKeyStr);
      //公钥解签
      Signature sig = Signature.getInstance(algorithm);
      sig.initVerify(publicKey);
      sig.update(data.getBytes());
      return sig.verify(BaseEncoding.base64().decode(sign));
    } catch (Exception e) {
      LOG.warn("", e);
      return false;
    }
  }


  public static void main(String[] args) {
    String text = "你是小铃铛 ExpTime=1226577284468$Pid=100013$Sid=rlpm001 你好啊!!!&&";
    System.out.println("text = " + text);
    RSAUtil rsa = RSAUtil.create();
    KeyPair keyPair = rsa.generateKeyPair();
    String pubKey = rsa.getPublicKeyWithBase64(keyPair);
    String priKey = rsa.getPrivateKeyWithBase64(keyPair);
    String encrypted = rsa.encryptByPublicKey(pubKey, text);
    String decrypted = rsa.decryptByPrivateKey(priKey, encrypted);
    System.out.println(encrypted);
    System.out.println(decrypted);
    String sign = rsa.signSha256withRSA(priKey, text);
    System.out.println("sign = " + sign);
    boolean verify = rsa.verifySHA256WithRSA(pubKey, text,  sign);
    System.out.println("verify = " + verify);

  }
}
