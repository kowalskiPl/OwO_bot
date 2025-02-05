package com.owobot.modules.help.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashSet;
import java.util.Set;

public class HelpSlashCommands {

    public Set<SlashCommandData> getGlobalCommands() {
        Set<SlashCommandData> globalCommands = new HashSet<>();

        globalCommands.add(Commands.slash("testglobalcommand", "A global test command for help module"));

        return globalCommands;
    }

    public Set<SlashCommandData> getGuildCommands() {
        Set<SlashCommandData> guildCommands = new HashSet<>();

        guildCommands.add(Commands.slash("testguildcommand", "A guild test command for help module"));

        return guildCommands;
    }
}
