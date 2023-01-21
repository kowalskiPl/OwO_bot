package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;

import java.util.LinkedHashSet;
import java.util.Set;

public class GetMusicChannelsCommand extends Command {

    public GetMusicChannelsCommand() {
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
    protected String getInfo() {
        return "Lists currently active music channels.";
    }
}
