package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;
import com.owobot.modules.admin.AdminParameterNames;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressWarnings("CopyConstructorMissesField")
public class AddPrefixCommand extends Command {

    public AddPrefixCommand(String parentModule) {
        super(parentModule);
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
    protected String getInfo() {
        return "Used to add new prefix to the server prefix set. Accepts one parameter, new prefix";
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
        return "AddPrefixCommand{" +
                "parameters=" + parameterMap +
                ", name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                '}';
    }
}
