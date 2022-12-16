package src.events;

import net.dv8tion.jda.api.entities.MessageChannel;

public class SendMessageEvent extends Event{
    private final MessageChannel messageChannel;
    private String text;

    public SendMessageEvent(MessageChannel messageChannel, String text) {
        this.messageChannel = messageChannel;
        this.text = text;
    }

    public SendMessageEvent(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public String getText() {
        return text;
    }
}
