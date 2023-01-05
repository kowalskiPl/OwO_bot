package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.LinkedHashSet;
import java.util.Set;

public class PauseCommand extends Command {
    public PauseCommand() {
        name = "pause";
        triggers = new LinkedHashSet<>(Set.of("pause"));
    }

    public PauseCommand(PauseCommand command) {
        super(command);
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
