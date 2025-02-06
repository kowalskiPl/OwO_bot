package com.owobot.commands;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Set;

public interface ModuleSlashCommands {
    Set<SlashCommandData> getGlobalCommands();
    Set<SlashCommandData> getGuildCommands();
}
