package com.owobot.modules.admin.commands;

import com.owobot.OwoBot;
import com.owobot.commands.ModuleSlashCommands;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Set;

public class AdminSlashCommands extends Reflectional implements ModuleSlashCommands {
    public AdminSlashCommands(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public Set<SlashCommandData> getGlobalCommands() {
        return Set.of();
    }

    @Override
    public Set<SlashCommandData> getGuildCommands() {
        return Set.of(getAddPrefixCommand(),
                getRemovePrefixCommand(),
                getRefreshSlashCommandsCommand(),
                getListPrefixesCommand(),
                getEnableMusicChannelCommand(),
                getMusicChannelsCommand(),
                getAddMusicChannelCommand(),
                getRemoveMusicChannelCommand());
    }

    private SlashCommandData getAddPrefixCommand() {
        var addPrefixCommand = Commands.slash(AdminSlashCommandsNames.ADD_PREFIX_COMMAND_NAME.getName(), "Adds a new prefix for non-slash command operation of the bot.");
        addPrefixCommand.addOption(OptionType.STRING, "new-prefix", "New prefix to be added");
        addPrefixCommand.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        return addPrefixCommand;
    }

    private SlashCommandData getRemovePrefixCommand() {
        var removePrefixCommand = Commands.slash(AdminSlashCommandsNames.REMOVE_PREFIX_COMMAND_NAME.getName(), "Removes a old prefix for non-slash command operation of the bot.");
        removePrefixCommand.addOption(OptionType.STRING, "prefix-to-remove", "Old prefix to be removed");
        removePrefixCommand.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        return removePrefixCommand;
    }

    private SlashCommandData getRefreshSlashCommandsCommand() {
        var reloadSlashCommands = Commands.slash(AdminSlashCommandsNames.REFRESH_SLASH_COMMANDS_COMMAND_NAME.getName(), "Reloads guild slash commands this bot has to offer");
        reloadSlashCommands.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        return reloadSlashCommands;
    }

    private SlashCommandData getListPrefixesCommand() {
        return Commands.slash(AdminSlashCommandsNames.LIST_PREFIXES_COMMAND_NAME.getName(), "Lists all prefixes for non-slash command operation of the bot.");
    }

    private SlashCommandData getEnableMusicChannelCommand() {
        var enableMusicChannel = Commands.slash(AdminSlashCommandsNames.ENABLE_MUSIC_CHANNEL_COMMAND_NAME.getName(),
                "Tells if music channel setting is enabled. Operation argument enables or disables music channel");
        enableMusicChannel.addOptions(new OptionData(OptionType.STRING, "operation", "Weather to enable or disable the music channel if such was earlier assigned") // because damned boolean does not work!!!
                .addChoice("enable", "enable")
                .addChoice("disable", "disable"));
        enableMusicChannel.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        return enableMusicChannel;
    }

    private SlashCommandData getMusicChannelsCommand() {
        return Commands.slash(AdminSlashCommandsNames.GET_MUSIC_CHANNELS_COMMAND_NAME.getName(), "Returns the list of currently assigned music channels");
    }

    private SlashCommandData getAddMusicChannelCommand() {
        var addMusicChannel = Commands.slash(AdminSlashCommandsNames.ADD_MUSIC_CHANNEL_COMMAND_NAME.getName(), "Registers a new music channel.");
        addMusicChannel.addOption(OptionType.CHANNEL, "channel", "Channel to be registered as music enabled channel");
        addMusicChannel.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        return addMusicChannel;
    }

    private SlashCommandData getRemoveMusicChannelCommand() {
        var removeMusicChannel = Commands.slash(AdminSlashCommandsNames.REMOVE_MUSIC_CHANNEL_COMMAND_NAME.getName(), "Removes an existing music channel");
        removeMusicChannel.addOption(OptionType.CHANNEL, "channel", "Music channel to be removed");
        removeMusicChannel.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
        return removeMusicChannel;
    }
}
