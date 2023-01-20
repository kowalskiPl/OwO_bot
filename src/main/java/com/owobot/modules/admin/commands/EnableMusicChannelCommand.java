package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;
import com.owobot.modules.admin.AdminParameterNames;

import java.util.*;

public class EnableMusicChannelCommand extends Command {

    public EnableMusicChannelCommand() {
        this.name = "enableMusic";
        this.triggers = new LinkedHashSet<>(
                Set.of(
                        "enableMusic",
                        "enableMusicChannel"
                )
        );
    }

    public EnableMusicChannelCommand(EnableMusicChannelCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new EnableMusicChannelCommand(this);
    }

    @Override
    public void setParameters(String parameters) {
        this.parameterMap = new LinkedHashMap<>();
        if (parameters != null) {
            this.parameterMap.put(AdminParameterNames.ADMIN_PARAMETER_ENABLE_MUSIC_CHANNEL.getName(), parameters);
        }
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("require:user," + Permissions.ADMINISTRATOR.getNode());
        return middlewares;
    }

    @Override
    protected String getInfo() {
        return "Enables or disables music channel setting. Use without arguments to see if music channel is enabled. Accepts true or false as parameters";
    }

    @Override
    public String toString() {
        return "EnableMusicChannelCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
