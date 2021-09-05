package com.hex.common.exception;

/**
 * @author: hs
 */
public class SerializeException extends RuntimeException {

    public SerializeException() {
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
