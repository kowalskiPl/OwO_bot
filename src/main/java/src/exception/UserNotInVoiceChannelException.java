package src.exception;

public class UserNotInVoiceChannelException extends Exception{
    public UserNotInVoiceChannelException() {
    }

    public UserNotInVoiceChannelException(String message) {
        super(message);
    }
}
