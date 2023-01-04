package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.LinkedHashSet;
import java.util.Set;

public class StopCommand extends Command {
    public StopCommand() {
        name = "stop";
        triggers = new LinkedHashSet<>(Set.of("stop"));
    }

    public StopCommand(StopCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new StopCommand(this);
    }
}
