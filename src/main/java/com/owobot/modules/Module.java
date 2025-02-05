package com.owobot.modules;

import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Set;

public interface Module extends Comparable<Module> {

    public String getName();

    public Set<CommandListener> getCommandListeners();

    public Set<Command> getCommands();

    public Set<SlashCommandData> getGlobalSlashCommands();

    public Set<SlashCommandData> getGuildSlashCommands();

    public String getNameUserFriendly();
}
