package com.hex.common.exception;

/**
 * @author: hs
 */
public class DecoderException extends RpcException {
    public DecoderException() {
    }

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
