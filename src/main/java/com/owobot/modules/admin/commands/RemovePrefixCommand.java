package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;
import com.owobot.modules.admin.AdminParameterNames;

import java.util.*;

public class RemovePrefixCommand extends Command {
    public RemovePrefixCommand() {
        name = "removePrefix";
        triggers = new LinkedHashSet<>(Set.of(
                "removePrefix",
                "deletePrefix",
                "unsetPrefix"
        ));
    }

    protected RemovePrefixCommand(RemovePrefixCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new RemovePrefixCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Removes prefix from active prefixes for server. Accepts one parameter, prefix to remove";
    }

    @Override
    public void setParameters(String parameters) {
        this.parameterMap = new LinkedHashMap<>();
        if (parameters != null)
            this.parameterMap.put(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName(), parameters);
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("require:user," + Permissions.ADMINISTRATOR.getNode());
        middlewares.add("guildChannel");
        return middlewares;
    }

    @Override
    public String toString() {
        return "RemovePrefixCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
