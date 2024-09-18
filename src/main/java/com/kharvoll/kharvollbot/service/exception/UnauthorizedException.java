package com.kharvoll.kharvollbot.service.exception;

public class UnauthorizedException extends KharvollBotException {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
