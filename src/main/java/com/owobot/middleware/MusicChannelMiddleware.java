package com.owobot.middleware;

import com.owobot.OwoBot;
import com.owobot.commands.Command;

import java.util.concurrent.TimeUnit;

public class MusicChannelMiddleware extends Middleware {
    public MusicChannelMiddleware(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean handle(Command command, MiddlewareStack stack, String... args) {
        var messageChannelId = command.getCommandMessage().getMessage().getChannel().getIdLong();
        var settings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(command.getCommandMessage().getGuild().getIdLong());

        if (!settings.isMusicChannelEnabled())
            return stack.next();

        if (!settings.getMusicChannelIds().contains(messageChannelId)) {
            command.getCommandMessage()
                    .getMessage()
                    .getChannel()
                    .sendMessage("You need to use this command in specified by administrators music channel!")
                    .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
            return false;
        }

        return stack.next();
    }
}
