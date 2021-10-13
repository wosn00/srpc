package com.hex.common.exception;

/**
 * @author: hs
 */
public class EncoderException extends RpcException {
    public EncoderException() {
    }

    public EncoderException(String message) {
        super(message);
    }

    public EncoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
