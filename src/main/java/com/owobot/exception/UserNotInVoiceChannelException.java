package com.owobot.exception;

public class UserNotInVoiceChannelException extends Exception{
    public UserNotInVoiceChannelException() {
    }

    public UserNotInVoiceChannelException(String message) {
        super(message);
    }
}
