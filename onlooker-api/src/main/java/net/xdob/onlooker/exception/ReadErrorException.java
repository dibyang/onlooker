package net.xdob.onlooker.exception;

public class ReadErrorException extends OnlookerException{

  public static final String READ_ERROR = "read_error";

  public ReadErrorException(String owner) {
    super(READ_ERROR,"read message fail for "+owner);
  }
}
