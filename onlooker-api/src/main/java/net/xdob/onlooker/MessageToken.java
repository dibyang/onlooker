package net.xdob.onlooker;

import java.util.HashMap;

/**
 * 观察到的消息令牌
 */
public class MessageToken extends HashMap<String, String> {
  public static final String MESSAGE = "message";
  public static final String SIGNER = "signer";
  public static final String SIGN = "sign";


  /**
   * 获取观察到的消息
   * @return 观察到的消息
   */
  public String getMessage() {
    return get(MESSAGE);
  }

  /**
   * 设置观察到的消息
   * @param message 观察到的消息
   */
  public void setMessage(String message) {
    this.put(MESSAGE, message);
  }

  /**
   * 获取消息的签名者
   * @return 消息的签名者
   */
  public String getSigner() {
    return get(SIGNER);
  }

  /**
   * 设置消息的签名者
   * @param signer 消息的签名者
   */
  public void setSigner(String signer) {
    this.put(SIGNER, signer);
  }

  /**
   * 获取消息签名
   * @return 消息签名
   */
  public String getSign() {
    return get(SIGN);
  }

  /**
   * 设置消息签名
   * @param sign 消息签名
   */
  public void setSign(String sign) {
    this.put(SIGN, sign);
  }
}
