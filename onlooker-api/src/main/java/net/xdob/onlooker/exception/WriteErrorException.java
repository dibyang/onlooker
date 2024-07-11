package net.xdob.onlooker.exception;

public class WriteErrorException extends OnlookerException{

  public static final String WRITE_ERROR = "write_error";

  public WriteErrorException(String owner) {
    super(WRITE_ERROR,"write message fail for "+owner);
  }
}
