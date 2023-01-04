package com.owobot.modules.music.events;

import com.owobot.events.Event;
import com.owobot.events.Observable;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PlayerPauseEvent extends Event {
    private final TextChannel messageChannel;
    private final boolean isPaused;
    public PlayerPauseEvent(TextChannel messageChannel, boolean isPaused, Observable sender) {
        super(sender);
        this.messageChannel = messageChannel;
        this.isPaused = isPaused;
    }

    public TextChannel getMessageChannel() {
        return messageChannel;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
