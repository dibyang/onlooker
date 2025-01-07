package net.xdob.onlooker;

import com.ls.luava.common.N3Map;
import com.ls.luava.common.Types;
import com.ls.luava.common.Types2;

import java.util.HashMap;


/**
 * 观察到的消息令牌
 */
public class MessageToken extends HashMap<String,String> {
  public static final String MESSAGE = "message";
  public static final String SIGNER = "signer";
  public static final String SIGN = "sign";
  public static final String TERM = "term";
  public static final String INDEX = "index";

  public static final MessageToken empty = new MessageToken();

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

  /**
   * 获取当前任期
   * @return 消息签名
   */
  public long getTerm() {
    return Types2.cast(get(TERM),Long.class).orElse(0L);
  }

  /**
   * 设置当前任期
   * @param term 当前任期
   */
  public void setTeam(long term) {
    this.put(TERM, String.valueOf(term));
  }

  /**
   * 获取当前任期
   * @return 消息签名
   */
  public long getIndex() {
    return Types2.cast(get(INDEX),Long.class).orElse(0L);
  }

  /**
   * 设置当前任期
   * @param index 当前任期
   */
  public void setIndex(long index) {
    this.put(INDEX, String.valueOf(index));
  }

}
