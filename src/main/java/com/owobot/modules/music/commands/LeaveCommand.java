package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.LinkedHashSet;
import java.util.Set;

public class LeaveCommand extends Command {
    public LeaveCommand() {
        name = "leave";
        triggers = new LinkedHashSet<>(Set.of("leave"));
    }

    public LeaveCommand(LeaveCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new LeaveCommand(this);
    }
}
