package src.events;

import net.dv8tion.jda.api.entities.MessageChannel;

public class SendMessageEvent extends Event {
    private final MessageChannel messageChannel;
    private final String text;
    private int delay;

    public SendMessageEvent(MessageChannel messageChannel, String text, Observable sender) {
        super(sender);
        this.messageChannel = messageChannel;
        this.text = text;
    }

    public SendMessageEvent(MessageChannel messageChannel, String text, int delayDelete, Observable sender) {
        super(sender);
        this.messageChannel = messageChannel;
        this.text = text;
        this.delay = delayDelete;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public String getText() {
        return text;
    }

    public int getDelay() {
        return delay;
    }
}
