package net.xdob.onlooker;

import net.xdob.onlooker.exception.OnlookerException;

/**
 * 观察消息的存储管理
 */
public interface MessageAdmin {
  /**
   * 存储观察消息
   * @param owner 消息属主
   * @param token 消息令牌（带签名）
   * @return 是否存储成功
   */
  void setMessage(String owner, MessageToken token) throws OnlookerException;

  /**
   * 提取属主的消息
   * @param owner 消息属主
   * @return 属主的消息令牌
   */
  MessageToken getMessage(String owner);
}
