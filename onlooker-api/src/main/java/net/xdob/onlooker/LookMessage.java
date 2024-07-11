package net.xdob.onlooker;

import com.ls.luava.common.Jsons;
import com.ls.luava.common.Types2;

import java.util.UUID;

public class LookMessage {
  private LookRequestType lookRequestType = LookRequestType.GET;
  private UUID uid = UUID.randomUUID();
  /**
   * 属主
   */
  private String owner;
  private Object data;
  private String error;
  private String errorMessage;
  private String sender;

  public LookMessage() {
  }

  public LookMessage(LookRequestType lookRequestType) {
    this.lookRequestType = lookRequestType;
  }

  public LookMessage(UUID uid, String owner, LookRequestType lookRequestType) {
    this.uid = uid;
    this.owner = owner;
    this.lookRequestType = lookRequestType;
  }

  public UUID getUid() {
    return uid;
  }

  public LookRequestType getLookRequestType() {
    return lookRequestType;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public <T> T getData(Class<T> tClass) {
    return Types2.cast(data, tClass).orElse(null);
  }

  public <T> void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return Jsons.i.toJson(this);
  }
}
