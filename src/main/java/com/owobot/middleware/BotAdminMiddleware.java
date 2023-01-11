package com.owobot.middleware;

import com.owobot.OwoBot;
import com.owobot.commands.Command;

import java.util.concurrent.TimeUnit;

public class BotAdminMiddleware extends Middleware {
    public BotAdminMiddleware(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean handle(Command command, MiddlewareStack stack, String... args) {
        var message = command.getCommandMessage();
        if (!message.getMessageChannel().getType().isGuild()) {
            return false;
        }

        var userId = command.getCommandMessage().getMember().getIdLong();

        if (owoBot.getBotAdmins().getUserByID(userId).getUserId() == 0) {
            command.getCommandMessage().getMessageChannel().sendMessage("You have to be a bot administrator to use this command!")
                    .queue(newMessage -> newMessage.delete().queueAfter(30, TimeUnit.SECONDS));
            return false;
        }

        return stack.next();
    }
}
