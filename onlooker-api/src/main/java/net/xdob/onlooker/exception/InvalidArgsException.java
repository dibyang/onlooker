package net.xdob.onlooker.exception;

public class InvalidArgsException extends OnlookerException{

  public static final String INVALID_ARGS = "invalid_args";

  public InvalidArgsException(String message) {
    super(INVALID_ARGS, message);
  }

  public InvalidArgsException(String message, Throwable cause) {
    super(INVALID_ARGS, message, cause);
  }
}
