package com.owobot.events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DeleteMessageEvent extends Event {
    private final TextChannel channel;
    private final long messageID;
    private final int previousMessages;

    public DeleteMessageEvent(TextChannel channel, long messageID, int previousMessages, Observable sender) {
        super(sender);
        this.channel = channel;
        this.messageID = messageID;
        this.previousMessages = previousMessages;
    }

    public TextChannel getMessageChannel() {
        return channel;
    }

    public long getMessageID() {
        return messageID;
    }

    public int getPreviousMessages() {
        return previousMessages;
    }
}
