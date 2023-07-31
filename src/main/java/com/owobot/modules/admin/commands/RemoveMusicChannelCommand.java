package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RemoveMusicChannelCommand extends Command {

    public RemoveMusicChannelCommand() {
        this.name = "removeMusicChannel";
        this.triggers = new LinkedHashSet<>(
                Set.of(
                        "removeMusicChannel",
                        "deleteMusicChannel"
                )
        );
    }

    public RemoveMusicChannelCommand(RemoveMusicChannelCommand command) {
        super(command);
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("require:user," + Permissions.ADMINISTRATOR.getNode());
        middlewares.add("guildChannel");
        return middlewares;
    }

    @Override
    public Command clone() {
        return new RemoveMusicChannelCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Removes specified music channels. Accepts multiple channel mentions.";
    }

    @Override
    public String toString() {
        return "RemoveMusicChannelCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
