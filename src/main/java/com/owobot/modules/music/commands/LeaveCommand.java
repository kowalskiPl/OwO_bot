package com.owobot.modules.music.commands;

import com.owobot.commands.Command;

import java.util.ArrayList;
import java.util.List;

public class LeaveCommand extends Command {
    public LeaveCommand() {
        super();
        name = "leave";
        triggers.add("leave");
        triggers.add("fuckOff");
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
