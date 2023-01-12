package com.owobot.middleware.permission;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.middleware.Middleware;
import com.owobot.middleware.MiddlewareStack;

public class RequireMusicChannelMiddleware extends Middleware {
    public RequireMusicChannelMiddleware(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean handle(Command command, MiddlewareStack stack, String... args) {
        return false;
    }
}
