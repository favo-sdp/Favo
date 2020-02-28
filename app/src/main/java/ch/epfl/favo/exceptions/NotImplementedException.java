package ch.epfl.favo.exceptions;

public class NotImplementedException extends RuntimeException {

        public NotImplementedException(String message, Throwable cause) {
            super(message, cause);
        }

        public NotImplementedException(String message) {
            super(message);
        }
        public NotImplementedException(){
            super();
        }

}
