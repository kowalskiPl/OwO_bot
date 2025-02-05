package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class RefreshSlashCommandsCommand extends Command {
    public RefreshSlashCommandsCommand(Command command) {
        super(command);
    }

    public RefreshSlashCommandsCommand(String parentModule) {
        super(parentModule);
        name = "refreshSlashCommands";
        triggers = new LinkedHashSet<>();
        triggers.add("refreshSlashCommands");
        triggers.add("reloadSlashCommands");
    }

    @Override
    public Command clone() {
        return new RefreshSlashCommandsCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Reloads the slash commands manually in case automatic refresh fails";
    }

    public void setParameters(String parameters) {
        this.parameterMap = new LinkedHashMap<>();
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("require:user," + Permissions.ADMINISTRATOR.getNode());
        middlewares.add("guildChannel");
        return middlewares;
    }
}
