package com.owobot.modules.help.commands;

import com.owobot.commands.Command;
import com.owobot.modules.help.HelpCommandParameters;

import java.util.*;

public class GetHelpCommand extends Command {
    List<String> middlewares;

    public GetHelpCommand(String parentModule) {
        super(parentModule);
        name = "getHelp";
        triggers = new LinkedHashSet<>(Set.of("help"));
        middlewares = new ArrayList<>();
    }

    public GetHelpCommand(GetHelpCommand command) {
        super(command);
        this.middlewares = command.getMiddlewares();
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        if (!parameters.isEmpty()) {
            parameters = parameters.trim();
            parameterMap.put(HelpCommandParameters.HELP_PARAMETER_MODULE_NAME.getName(), parameters);
            if (parameters.equalsIgnoreCase("BotAdmin")){
                middlewares.add("botAdmin");
            }
        }
    }

    @Override
    public List<String> getMiddlewares() {
        return middlewares;
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
