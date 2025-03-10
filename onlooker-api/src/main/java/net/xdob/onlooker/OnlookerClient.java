package net.xdob.onlooker;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OnlookerClient {

  void start();

  void stop();

  /**
   * 发送消息令牌(签名防伪)
   * @param owner 属主
   * @param messageToken 消息令牌(签名防伪)
   * @return 消息返回结果
   */
  CompletableFuture<List<LookResponse>> setMessage(String owner, MessageToken messageToken);

  /**
   * 获取属主的被观察消息令牌
   * @param owner 属主
   * @return 消息返回结果
   */
  CompletableFuture<List<LookResponse>> getMessage(String owner);

  /**
   * 发送消息令牌(签名防伪)
   * @param owner 属主
   * @param messageToken 消息令牌(签名防伪)
   * @param waitMS 等待结果的毫秒值
   * @return 消息返回结果
   */
  CompletableFuture<List<LookResponse>> setMessage(String owner, MessageToken messageToken, int waitMS);

  /**
   * 获取属主的被观察消息令牌
   * @param owner 属主
   * @param waitMS 等待结果的毫秒值
   * @return 消息返回结果
   */
  CompletableFuture<List<LookResponse>> getMessage(String owner, int waitMS);

  /**
   * 获取属主的被观察消息令牌
   * @param owner 属主
   * @return 消息返回结果
   */
  CompletableFuture<List<MessageToken>> getMessageToken(String owner);

  /**
   * 获取属主的被观察消息令牌
   * @param owner 属主
   * @param waitMS 等待结果的毫秒值
   * @return 消息返回结果
   */
  CompletableFuture<List<MessageToken>> getMessageToken(String owner, int waitMS);
}
