package com.owobot.modules.music.events;

import com.owobot.events.Event;
import com.owobot.events.Observable;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SendMessageEvent extends Event {
    private final TextChannel messageChannel;
    private final String text;
    private int delay;

    public SendMessageEvent(TextChannel messageChannel, String text, Observable sender) {
        super(sender);
        this.messageChannel = messageChannel;
        this.text = text;
    }

    public SendMessageEvent(TextChannel messageChannel, String text, int delayDelete, Observable sender) {
        super(sender);
        this.messageChannel = messageChannel;
        this.text = text;
        this.delay = delayDelete;
    }

    public TextChannel getMessageChannel() {
        return messageChannel;
    }

    public String getText() {
        return text;
    }

    public int getDelay() {
        return delay;
    }
}
