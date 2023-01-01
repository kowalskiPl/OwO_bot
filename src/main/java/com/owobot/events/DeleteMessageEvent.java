package com.owobot.events;

import net.dv8tion.jda.api.entities.MessageChannel;

public class DeleteMessageEvent extends Event {
    private final MessageChannel channel;
    private final long messageID;
    private final int previousMessages;

    public DeleteMessageEvent(MessageChannel channel, long messageID, int previousMessages, Observable sender) {
        super(sender);
        this.channel = channel;
        this.messageID = messageID;
        this.previousMessages = previousMessages;
    }

    public MessageChannel getMessageChannel() {
        return channel;
    }

    public long getMessageID() {
        return messageID;
    }

    public int getPreviousMessages() {
        return previousMessages;
    }
}
