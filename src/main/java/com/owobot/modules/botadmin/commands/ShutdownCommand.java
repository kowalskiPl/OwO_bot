package com.owobot.modules.botadmin.commands;

import com.owobot.commands.Command;
import com.owobot.modules.botadmin.BotAdminCommandParameters;

import java.util.ArrayList;
import java.util.List;

public class ShutdownCommand extends Command {

    public ShutdownCommand(String parentModule) {
        super(parentModule);
        name = "shutdown";
        triggers.add("shutdown");
        triggers.add("turnOff");
        triggers.add("sayonara");
    }

    public ShutdownCommand(ShutdownCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new ShutdownCommand(this);
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("botAdmin");
        return middlewares;
    }

    @Override
    public void setParameters(String parameters) {
        if (!parameters.isEmpty())
            parameterMap.put(BotAdminCommandParameters.BOT_ADMIN_TIME_TO_SHUTDOWN.getName(), parameters);
        else
            parameterMap.put(BotAdminCommandParameters.BOT_ADMIN_TIME_TO_SHUTDOWN.getName(), "60");
    }

    @Override
    protected String getInfo() {
        return "Turns off the bot, minimum delay is 10s. Takes time in seconds as parameter or defaults to 60s";
    }
}
