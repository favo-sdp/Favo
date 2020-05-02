package ch.epfl.favo.exception;

public class IllegalRequestException extends RuntimeException {
  public IllegalRequestException(String s) {
    super(s);
  }
}
