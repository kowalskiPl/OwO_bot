package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GetMusicChannelsCommand extends Command {

    public GetMusicChannelsCommand(String parentModule) {
        super(parentModule);
        this.name = "getMusicChannels";
        this.triggers = new LinkedHashSet<>(
                Set.of(
                        "getMusicChannels",
                        "listMusicChannels"
                )
        );
    }

    public GetMusicChannelsCommand(GetMusicChannelsCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new GetMusicChannelsCommand(this);
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("guildChannel");
        return middlewares;
    }

    @Override
    protected String getInfo() {
        return "Lists currently active music channels.";
    }

    @Override
    public String toString() {
        return "GetMusicChannelsCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
