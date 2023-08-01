package com.owobot.middleware;

import com.owobot.OwoBot;
import com.owobot.commands.Command;

public class GuildCommandMiddleware extends Middleware{
    public GuildCommandMiddleware(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean handle(Command command, MiddlewareStack stack, String... args) {
        var isMessageFromGuild = command.getCommandMessage().isGuildMessage();
        if (!isMessageFromGuild) {
            command.getCommandMessage().getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("You have to execute this command from a server text channel!")).queue();
        }
        return isMessageFromGuild && stack.next();
    }
}
