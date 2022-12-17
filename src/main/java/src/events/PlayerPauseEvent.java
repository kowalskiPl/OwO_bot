package src.events;

import net.dv8tion.jda.api.entities.MessageChannel;

public class PlayerPauseEvent extends Event{
    private final MessageChannel messageChannel;
    private final boolean isPaused;
    public PlayerPauseEvent(MessageChannel messageChannel, boolean isPaused, Observable sender) {
        super(sender);
        this.messageChannel = messageChannel;
        this.isPaused = isPaused;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
