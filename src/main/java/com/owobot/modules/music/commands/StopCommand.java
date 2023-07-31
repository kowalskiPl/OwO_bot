package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StopCommand extends Command {
    public StopCommand(String parentModule) {
        super(parentModule);
        name = "stop";
        triggers = new LinkedHashSet<>(Set.of("stop"));
    }

    public StopCommand(StopCommand command) {
        super(command);
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("musicChannel");
        middlewares.add("guildChannel");
        return middlewares;
    }

    @Override
    public Command clone() {
        return new StopCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Stops the currently played music and clears the queue";
    }
}
