package ch.epfl.favo.common;

public class FavorNoLongerAvailableException extends RuntimeException {
  public FavorNoLongerAvailableException(String s) {
    super(s);
  }
}
