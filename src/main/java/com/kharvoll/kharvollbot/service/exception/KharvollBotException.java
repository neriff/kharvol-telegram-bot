package com.kharvoll.kharvollbot.service.exception;

public class KharvollBotException extends RuntimeException {

    public KharvollBotException() {
        super();
    }

    public KharvollBotException(String message) {
        super(message);
    }

    public KharvollBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public KharvollBotException(Throwable cause) {
        super(cause);
    }

    protected KharvollBotException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
