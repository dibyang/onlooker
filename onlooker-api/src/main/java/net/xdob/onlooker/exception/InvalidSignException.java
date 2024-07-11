package net.xdob.onlooker.exception;

public class InvalidSignException extends OnlookerException{

  public static final String INVALID_SIGN = "invalid_sign";

  public InvalidSignException(String owner) {
    super(INVALID_SIGN,"Invalid Sign for "+owner);
  }
}
