package ch.epfl.favo.exceptions;

public class NoPermissionGrantedException extends RuntimeException {
    public NoPermissionGrantedException(String s){
        super(s);
    }
}
