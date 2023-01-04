package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.modules.admin.AdminParameterNames;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

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
    public void setParameters(String parameters) {
        this.parameterMap = new LinkedHashMap<>();
        if (parameters != null)
            this.parameterMap.put(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName(), parameters);
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
