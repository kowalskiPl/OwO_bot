package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PauseCommand extends Command {
    public PauseCommand(String parentModule) {
        super(parentModule);
        name = "pause";
        triggers = new LinkedHashSet<>(Set.of("pause"));
    }

    public PauseCommand(PauseCommand command) {
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
        return new PauseCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Pauses the music player";
    }
}
