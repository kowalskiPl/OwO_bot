package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddMusicChannelCommand extends Command {

    public AddMusicChannelCommand() {
        this.name = "addMusicChannel";
        this.triggers = new LinkedHashSet<>(Set.of(
                "addMusicChannel",
                "registerMusicChannel"
        ));
    }

    public AddMusicChannelCommand(AddMusicChannelCommand command) {
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
        return new AddMusicChannelCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Adds new channel as the music channel. Accepts mentions of channels as parameters";
    }

    @Override
    public String toString() {
        return "AddMusicChannelCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
