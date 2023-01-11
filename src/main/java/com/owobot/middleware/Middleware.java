package com.owobot.middleware;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.Nullable;

public abstract class Middleware extends Reflectional {

    public Middleware(OwoBot owoBot) {
        super(owoBot);
    }
    @Nullable
    public String buildHelp(Command command){
        return null;
    }
    public abstract boolean handle(Command command, MiddlewareStack stack,String... args);
}
