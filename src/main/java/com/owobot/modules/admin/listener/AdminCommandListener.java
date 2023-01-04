package com.owobot.modules.admin.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.model.database.GuildSettings;
import com.owobot.modules.admin.AdminParameterNames;
import com.owobot.modules.admin.commands.AddPrefixCommand;
import com.owobot.modules.admin.commands.ListPrefixesCommand;
import com.owobot.modules.admin.commands.RemovePrefixCommand;
import com.owobot.utilities.Reflectional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminCommandListener extends Reflectional implements CommandListener {

    private static final Logger log = LoggerFactory.getLogger(AdminCommandListener.class);

    public AdminCommandListener(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean onCommand(Command command) {
        if (command instanceof AddPrefixCommand addPrefixCommand) {
            if (!owoBot.getConfig().isUseDB()) {
                command.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
            } else {
                var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(addPrefixCommand.getCommandMessage().getGuild().getIdLong());
                if (currentSettings == null) {
                    log.info("Settings are empty, creating new default set for guild: " + command.getCommandMessage().getGuild().getIdLong());

                    var defaults = GuildSettings.getDefaultSettings();
                    defaults.setGuildId(command.getCommandMessage().getGuild().getIdLong());
                    defaults.getPrefixes().add(command.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()));

                    owoBot.getMongoDbContext().saveNewGuildSettings(defaults);
                    log.info("Added new prefix and settings for guild: " + defaults.getGuildId());
                } else {
                    currentSettings.getPrefixes().add(command.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()));

                    owoBot.getMongoDbContext().updateSettings(currentSettings);
                    log.info("Added new prefix for guild: " + currentSettings.getGuildId());
                }
            }
            addPrefixCommand.getCommandMessage()
                    .getMessage()
                    .reply("Added " + addPrefixCommand.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()) + "as a new prefix")
                    .queue();
            return true;
        }

        if (command instanceof RemovePrefixCommand removePrefixCommand) {
            if (!owoBot.getConfig().isUseDB()) {
                command.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
            } else {
                var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(removePrefixCommand.getCommandMessage().getGuild().getIdLong());
                if (currentSettings == null) {
                    removePrefixCommand.getCommandMessage()
                            .getMessage()
                            .reply("Something went wrong and you don't have configuration file, try running addPrefix command first")
                            .queue();
                } else {
                    if (currentSettings.getPrefixes().size() == 1) {
                        removePrefixCommand.getCommandMessage()
                                .getMessage()
                                .reply("You can't remove all prefixes!")
                                .queue();
                    } else {
                        currentSettings.getPrefixes().remove(command.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()));
                        owoBot.getMongoDbContext().updateSettings(currentSettings);
                        removePrefixCommand.getCommandMessage()
                                .getMessage()
                                .reply("Removed prefix: " + command.getParameterMap().get(AdminParameterNames.ADMIN_PARAMETER_PREFIX.getName()))
                                .queue();
                    }
                }
            }
            return true;
        }

        if (command instanceof ListPrefixesCommand listPrefixesCommand) {
            if (!owoBot.getConfig().isUseDB()) {
                command.getCommandMessage().getMessage().reply("Database is disabled and this command will fail!").queue();
            } else {
                var currentSettings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(listPrefixesCommand.getCommandMessage().getGuild().getIdLong());
                if (currentSettings == null) {
                    listPrefixesCommand.getCommandMessage()
                            .getMessage()
                            .reply("Something went wrong and you don't have configuration file, try running addPrefix command first")
                            .queue();
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
                            .queue();
                }
            }
            return true;
        }
        return false;
    }
}
