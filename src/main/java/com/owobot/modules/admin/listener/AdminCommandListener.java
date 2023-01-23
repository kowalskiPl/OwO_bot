package com.owobot.modules.admin.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.model.database.GuildSettings;
import com.owobot.modules.admin.AdminParameterNames;
import com.owobot.modules.admin.commands.*;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AdminCommandListener extends Reflectional implements CommandListener {

    private static final Logger log = LoggerFactory.getLogger(AdminCommandListener.class);

    public AdminCommandListener(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean onCommand(Command command) {
        if (command instanceof AddPrefixCommand addPrefixCommand) {
            return handleAddPrefixCommand(addPrefixCommand);
        }

        if (command instanceof RemovePrefixCommand removePrefixCommand) {
            return handleRemovePrefixCommand(removePrefixCommand);
        }

        if (command instanceof ListPrefixesCommand listPrefixesCommand) {
            return handleListPrefixesCommand(listPrefixesCommand);
        }

        if (command instanceof EnableMusicChannelCommand musicChannelCommand) {
            return handleEnableMusicChannelCommand(musicChannelCommand);
        }

        if (command instanceof AddMusicChannelCommand musicChannelCommand) {
            return handleAddMusicChannelCommand(musicChannelCommand);
        }

        if (command instanceof RemoveMusicChannelCommand musicChannelCommand) {
            return handleRemoveMusicChannelCommand(musicChannelCommand);
        }

        if (command instanceof GetMusicChannelsCommand musicChannelCommand) {
            return handleGetMusicChannelsCommand(musicChannelCommand);
        }
        return false;
    }

    private boolean handleGetMusicChannelsCommand(Command musicChannelCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            musicChannelCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var currentConfig = owoBot.getMongoDbContext().getGuildSettingsByGuildID(musicChannelCommand.getCommandMessage().getGuild().getIdLong());
            if (currentConfig.getMusicChannelIds().isEmpty()) {
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("No music channel has been specified.")
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(musicChannelCommand, 30);
                return true;
            }

            StringBuilder channels = new StringBuilder();
            currentConfig.getMusicChannelIds().forEach(channel -> channels.append("<#").append(channel).append("> "));

            musicChannelCommand.getCommandMessage()
                    .getMessage()
                    .getChannel()
                    .sendMessage("Currently available music channels: " + channels.toString())
                    .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
            deleteMessage(musicChannelCommand, 30);
        }
        return true;
    }

    private boolean handleRemoveMusicChannelCommand(Command musicChannelCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            musicChannelCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var mentions = musicChannelCommand.getCommandMessage().getMessage().getMentions().getChannels();
            if (mentions.isEmpty()) {
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("No channel has been specified! Please specify a channel to delete from music channels.")
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(musicChannelCommand, 30);
                return true;
            }
            var currentConfig = owoBot.getMongoDbContext().getGuildSettingsByGuildID(musicChannelCommand.getCommandMessage().getGuild().getIdLong());
            var result = currentConfig.getMusicChannelIds().removeAll(mentions.stream().map(ISnowflake::getIdLong).collect(Collectors.toSet()));
            owoBot.getMongoDbContext().updateSettings(currentConfig);

            if (currentConfig.getMusicChannelIds().isEmpty()) {
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("There is no music channel defined. Users will be able to use music commands in any channel now!")
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(musicChannelCommand, 30);
                return true;
            }

            if (result) {
                String removedChannels = mentions.stream().map(Channel::getName).collect(Collectors.joining(", "));
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("Removed following channel(s) as music channels: " + removedChannels)
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(musicChannelCommand, 30);
                return true;
            } else {
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("Given channel is invalid. Make sure it is a valid music channel!")
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(musicChannelCommand, 30);
            }
        }
        return true;
    }

    private boolean handleAddMusicChannelCommand(AddMusicChannelCommand musicChannelCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            musicChannelCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var mentions = musicChannelCommand.getCommandMessage().getMessage().getMentions().getChannels();
            if (mentions.isEmpty()) {
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("No channel has been specified! Please specify a channel to set as music channel.")
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(musicChannelCommand, 30);
                return true;
            }
            var currentConfig = owoBot.getMongoDbContext().getGuildSettingsByGuildID(musicChannelCommand.getCommandMessage().getGuild().getIdLong());
            currentConfig.getMusicChannelIds().addAll(mentions.stream().map(ISnowflake::getIdLong).collect(Collectors.toSet()));
            owoBot.getMongoDbContext().updateSettings(currentConfig);

            String addedChannels = mentions.stream().map(Channel::getName).collect(Collectors.joining(", "));
            musicChannelCommand.getCommandMessage()
                    .getMessage()
                    .getChannel()
                    .sendMessage("Added following channel(s) as music channels: " + addedChannels)
                    .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
            deleteMessage(musicChannelCommand, 30);
            return true;
        }

        return true;
    }

    private boolean handleEnableMusicChannelCommand(EnableMusicChannelCommand musicChannelCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            musicChannelCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(musicChannelCommand.getCommandMessage().getGuild().getIdLong());
            if (currentSettings == null) {
                musicChannelCommand.getCommandMessage()
                        .getMessage()
                        .reply("Something went wrong and you don't have configuration file, try running addPrefix command first")
                        .queue();
                deleteMessage(musicChannelCommand, 30);
            } else {
                var parameter = musicChannelCommand.getParameterMap().getOrDefault(AdminParameterNames.ADMIN_PARAMETER_ENABLE_MUSIC_CHANNEL.getName(), "");

                if (parameter.isEmpty()) {
                    String enabled = currentSettings.isMusicChannelEnabled() ? "enabled" : "disabled";
                    musicChannelCommand.getCommandMessage()
                            .getMessage()
                            .reply("Music channel is " + enabled)
                            .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    deleteMessage(musicChannelCommand, 30);
                    return true;
                }

                if (!(parameter.equalsIgnoreCase("true") || parameter.equalsIgnoreCase("false"))) {
                    musicChannelCommand.getCommandMessage()
                            .getMessage()
                            .reply("Invalid argument! This command accepts only true or false as parameters")
                            .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    deleteMessage(musicChannelCommand, 30);
                } else {
                    boolean enable = Boolean.parseBoolean(parameter);
                    String message;
                    if (enable) {
                        message = "Music channel has been enabled";
                    } else {
                        message = "Music channel has been disabled";
                    }
                    currentSettings.setMusicChannelEnabled(enable);
                    owoBot.getMongoDbContext().updateSettings(currentSettings);
                    musicChannelCommand.getCommandMessage()
                            .getMessage()
                            .reply(message)
                            .queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
                    deleteMessage(musicChannelCommand, 30);
                }
            }
        }
        return true;
    }

    private boolean handleListPrefixesCommand(ListPrefixesCommand listPrefixesCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            listPrefixesCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(listPrefixesCommand.getCommandMessage().getGuild().getIdLong());
            if (currentSettings == null) {
                listPrefixesCommand.getCommandMessage()
                        .getMessage()
                        .reply("Something went wrong and you don't have configuration file, try running addPrefix command first")
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(listPrefixesCommand, 30);
            } else {
                var currentPrefixes = currentSettings.getPrefixes();
                StringBuilder sb = new StringBuilder();
                sb.append("Current prefixes: ");
                for (var prefix : currentPrefixes) {
                    sb.append("'").append(prefix).append("'").append(" ");
                }
                listPrefixesCommand.getCommandMessage()
                        .getMessage()
                        .reply(sb.toString())
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                deleteMessage(listPrefixesCommand, 30);
            }
        }
        return true;
    }

    private boolean handleRemovePrefixCommand(RemovePrefixCommand removePrefixCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            removePrefixCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(removePrefixCommand.getCommandMessage().getGuild().getIdLong());
            if (currentSettings == null) {
                removePrefixCommand.getCommandMessage()
                        .getMessage()
                        .reply("Something went wrong and you don't have configuration file, try running addPrefix command first")
                        .queue();
                deleteMessage(removePrefixCommand, 30);
            } else {
                if (currentSettings.getPrefixes().size() == 1) {
                    removePrefixCommand.getCommandMessage()
                            .getMessage()
                            .reply("You can't remove all prefixes!")
                            .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    deleteMessage(removePrefixCommand, 30);
                } else {
                    currentSettings.getPrefixes().remove(removePrefixCommand.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()));
                    owoBot.getMongoDbContext().updateSettings(currentSettings);
                    removePrefixCommand.getCommandMessage()
                            .getMessage()
                            .reply("Removed prefix: " + removePrefixCommand.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()))
                            .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    deleteMessage(removePrefixCommand, 30);
                }
            }
        }
        return true;
    }

    private boolean handleAddPrefixCommand(AddPrefixCommand addPrefixCommand) {
        if (!owoBot.getConfig().isUseDB()) {
            addPrefixCommand.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
        } else {
            var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(addPrefixCommand.getCommandMessage().getGuild().getIdLong());
            if (currentSettings == null) {
                log.info("Settings are empty, creating new default set for guild: " + addPrefixCommand.getCommandMessage().getGuild().getIdLong());

                var defaults = GuildSettings.getDefaultSettings();
                defaults.setGuildId(addPrefixCommand.getCommandMessage().getGuild().getIdLong());
                defaults.getPrefixes().add(addPrefixCommand.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()));

                owoBot.getMongoDbContext().saveNewGuildSettings(defaults);
                log.info("Added new prefix and settings for guild: " + defaults.getGuildId());
            } else {
                currentSettings.getPrefixes().add(addPrefixCommand.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()));

                owoBot.getMongoDbContext().updateSettings(currentSettings);
                log.info("Added new prefix for guild: " + currentSettings.getGuildId());
            }
        }
        addPrefixCommand.getCommandMessage()
                .getMessage()
                .reply("Added " + addPrefixCommand.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()) + " as a new prefix")
                .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
        deleteMessage(addPrefixCommand, 30);
        return true;
    }

    private void deleteMessage(Command command, int delay) {
        command.getCommandMessage().getMessage().delete().queueAfter(delay, TimeUnit.SECONDS);
    }
}
