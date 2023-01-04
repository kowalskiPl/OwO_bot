package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.LinkedHashSet;
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
    public Command clone() {
        return new SkipCommand(this);
    }

    @Override
    public void setParameters(String parameters) {
        super.setParameters(parameters);
    }
}
