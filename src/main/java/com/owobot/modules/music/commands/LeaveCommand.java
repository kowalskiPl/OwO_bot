package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("musicChannel");
        return middlewares;
    }

    @Override
    public Command clone() {
        return new LeaveCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Stops the player, clears music queue and the bot leaves voice channel";
    }
}
