package ch.epfl.favo.common;

public class IllegalRequestException extends RuntimeException {
  public IllegalRequestException(String s) {
    super(s);
  }
}
