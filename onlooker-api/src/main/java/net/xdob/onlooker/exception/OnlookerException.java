package net.xdob.onlooker.exception;

public class OnlookerException extends Exception{
  private final String errorCode;
  public OnlookerException() {
    this.errorCode = "inner_error";
  }

  public OnlookerException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public OnlookerException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public OnlookerException(String errorCode, Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
