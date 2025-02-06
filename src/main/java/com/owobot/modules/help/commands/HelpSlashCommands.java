package com.owobot.modules.help.commands;

import com.owobot.OwoBot;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HelpSlashCommands extends Reflectional {

    public HelpSlashCommands(OwoBot owoBot) {
        super(owoBot);
    }

    public Set<SlashCommandData> getGlobalCommands() {
        Set<SlashCommandData> globalCommands = new HashSet<>();

        globalCommands.add(Commands.slash("testglobalcommand", "A global test command for help module"));

        return globalCommands;
    }

    public Set<SlashCommandData> getGuildCommands() {
        Set<SlashCommandData> guildCommands = new HashSet<>();

        guildCommands.add(Commands.slash("testguildcommand", "A guild test command for help module"));

        var helpCommand = Commands.slash(HelpSlashCommandNames.GET_HELP_SLASH_COMMAND_NAME.getName(), "Fetches information about bot and available commands");

        var activeModules = new HashSet<>(owoBot.getModuleManager().getModules());
        activeModules = activeModules.stream().filter(module -> !module.getNameUserFriendly().equalsIgnoreCase("botadmin")).collect(Collectors.toCollection(HashSet::new));
        var helpCommandOptions = new OptionData(OptionType.STRING, "module", "Module name you want more info on");
        activeModules.forEach(module -> helpCommandOptions.addChoice(module.getNameUserFriendly(), module.getNameUserFriendly()));

        helpCommand.addOptions(helpCommandOptions);

        guildCommands.add(helpCommand);

        return guildCommands;
    }
}
