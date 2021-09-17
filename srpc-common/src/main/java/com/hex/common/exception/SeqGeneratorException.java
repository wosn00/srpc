package com.hex.common.exception;

/**
 * @author guohs
 * @date 2021/9/17
 */
public class SeqGeneratorException extends RuntimeException {

    public SeqGeneratorException() {
    }

    public SeqGeneratorException(String message) {
        super(message);
    }

    public SeqGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

}
