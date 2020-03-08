package ch.epfl.favo.common;

public class NoPermissionGrantedException extends RuntimeException {
  public NoPermissionGrantedException(String s) {
    super(s);
  }
}
