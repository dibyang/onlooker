package net.xdob.onlooker;

/**
 * 观察到的消息令牌
 */
public class MessageToken {
  /**
   * 观察到的消息
   */
  private String message;
  /**
   * 消息的签名者
   */
  private String signer;
  /**
   * 消息签名
   */
  private String sign;

  /**
   * 获取观察到的消息
   * @return 观察到的消息
   */
  public String getMessage() {
    return message;
  }

  /**
   * 设置观察到的消息
   * @param message 观察到的消息
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * 获取消息的签名者
   * @return 消息的签名者
   */
  public String getSigner() {
    return signer;
  }

  /**
   * 设置消息的签名者
   * @param signer 消息的签名者
   */
  public void setSigner(String signer) {
    this.signer = signer;
  }

  /**
   * 获取消息签名
   * @return 消息签名
   */
  public String getSign() {
    return sign;
  }

  /**
   * 设置消息签名
   * @param sign 消息签名
   */
  public void setSign(String sign) {
    this.sign = sign;
  }
}
