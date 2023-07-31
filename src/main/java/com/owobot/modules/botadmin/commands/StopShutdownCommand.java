package com.owobot.modules.botadmin.commands;

import com.owobot.commands.Command;

public class StopShutdownCommand extends Command {
    public StopShutdownCommand(String parentModule) {
        super(parentModule);
        name = "stopShutdown";
        triggers.add("stopShutdown");
        triggers.add("resume");
        triggers.add("cancelShutdown");
    }

    protected StopShutdownCommand(StopShutdownCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new StopShutdownCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Stops bot shutdown";
    }
}
