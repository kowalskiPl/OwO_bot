package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SkipCommand extends Command {
    public SkipCommand() {
        name = "skip";
        triggers = new LinkedHashSet<>(Set.of("skip"));
    }

    public SkipCommand(SkipCommand command) {
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
        return new SkipCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Skips the currently played song";
    }

    @Override
    public void setParameters(String parameters) {
        super.setParameters(parameters);
    }
}
