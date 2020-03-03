package ch.epfl.favo.exception;

public class NoPermissionGrantedException extends RuntimeException {
    public NoPermissionGrantedException(String s){
        super(s);
    }
}
