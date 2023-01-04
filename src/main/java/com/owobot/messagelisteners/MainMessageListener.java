package com.owobot.messagelisteners;

import com.owobot.OwoBot;
import com.owobot.commands.CommandMessage;
import com.owobot.core.CommandResolver;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMessageListener extends MessageListener {
    private final Logger log = LoggerFactory.getLogger(MainMessageListener.class);
    CommandResolver resolver;

    public MainMessageListener(OwoBot owoBot) {
        super(owoBot);
        resolver = new CommandResolver(owoBot);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!event.isFromGuild())
            return;

        CommandMessage commandMessage = new CommandMessage(event.getMessage());
        var command = resolver.resolve(commandMessage);

        if (command.getName().equals("")){
            log.warn("Unsupported command received");
            return;
        }
        owoBot.getCommandListenerStack().onCommand(command);
    }
}
