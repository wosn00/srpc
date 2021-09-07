package com.hex.common.exception;

/**
 * @author: hs
 */
public class RegistryException extends RuntimeException {
    public RegistryException() {
    }

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }

}
