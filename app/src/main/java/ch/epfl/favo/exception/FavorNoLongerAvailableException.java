package ch.epfl.favo.exception;

public class FavorNoLongerAvailableException extends RuntimeException {
  public FavorNoLongerAvailableException(String s) {
    super(s);
  }
}
