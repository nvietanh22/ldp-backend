package vn.lottefinance.landingpage.exception;

public class CustomedBadRequestException extends RuntimeException {
    public CustomedBadRequestException() {
    }

    public CustomedBadRequestException(String message) {
        super(message);
    }

    public CustomedBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomedBadRequestException(Throwable cause) {
        super(cause);
    }

    public CustomedBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
