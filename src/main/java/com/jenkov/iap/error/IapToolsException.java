package com.jenkov.iap.error;

/**
 * Created by jjenkov on 05-03-2016.
 */
public class IapToolsException extends RuntimeException {

    public IapToolsException() {
        super();
    }

    public IapToolsException(String message) {
        super(message);
    }

    public IapToolsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IapToolsException(Throwable cause) {
        super(cause);
    }

    protected IapToolsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
