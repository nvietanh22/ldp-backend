package vn.lottefinance.landingpage.exception;

public class ResourceAlreadyDisabledException extends RuntimeException{
    public ResourceAlreadyDisabledException() {
    }

    public ResourceAlreadyDisabledException(String message) {
        super(message);
    }

    public ResourceAlreadyDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyDisabledException(Throwable cause) {
        super(cause);
    }

    public ResourceAlreadyDisabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
