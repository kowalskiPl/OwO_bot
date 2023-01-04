package com.owobot.messagelisteners;

import com.owobot.OwoBot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class MessageListener extends ListenerAdapter {
    protected OwoBot owoBot;

    protected MessageListener(OwoBot owoBot) {
        this.owoBot = owoBot;
    }
}
