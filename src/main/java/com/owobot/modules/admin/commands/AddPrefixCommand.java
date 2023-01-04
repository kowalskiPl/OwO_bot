package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.modules.admin.AdminParameterNames;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

@SuppressWarnings("CopyConstructorMissesField")
public class AddPrefixCommand extends Command {

    public AddPrefixCommand() {
        name = "addPrefix";
        triggers = new LinkedHashSet<>();
        triggers.add("addPrefix");
        triggers.add("newPrefix");
        triggers.add("setPrefix");
    }

    protected AddPrefixCommand(AddPrefixCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new AddPrefixCommand(this);
    }

    @Override
    public void setParameters(String parameters) {
        this.parameterMap = new LinkedHashMap<>();
        if (parameters != null)
            this.parameterMap.put(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName(), parameters);
    }

    @Override
    public String toString() {
        return "AddPrefixCommand{" +
                "parameters=" + parameterMap +
                ", name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                '}';
    }
}
