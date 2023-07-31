package com.owobot.modules.help.commands;

import com.owobot.commands.Command;
import com.owobot.modules.help.HelpCommandParameters;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class GetHelpCommand extends Command {

    public GetHelpCommand(String parentModule) {
        super(parentModule);
        name = "getHelp";
        triggers = new LinkedHashSet<>(Set.of("help"));
    }

    public GetHelpCommand(GetHelpCommand command) {
        super(command);
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        if (!parameters.isEmpty()) {
            parameterMap.put(HelpCommandParameters.HELP_PARAMETER_MODULE_NAME.getName(), parameters);
        }
    }

    @Override
    public Command clone() {
        return new GetHelpCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Returns information about loaded modules. Accepts one parameter, a module name";
    }

    @Override
    public String toString() {
        return "GetHelpCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
