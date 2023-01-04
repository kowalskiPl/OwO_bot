package com.owobot.core;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandMessage;
import com.owobot.commands.EmptyCommand;
import com.owobot.model.database.GuildSettings;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandResolver extends Reflectional {
    private static final Pattern commandRecognitionPattern = Pattern.compile("^(.)(\\w*)\s*(.*)$");
    private static final Logger log = LoggerFactory.getLogger(CommandResolver.class);

    public CommandResolver(OwoBot owoBot) {
        super(owoBot);
    }

    public Command resolve(CommandMessage commandMessage) {
        if (commandMessage.getAuthor().isBot())
            return new EmptyCommand();

        Matcher matcher = commandRecognitionPattern.matcher(commandMessage.getMessage().getContentRaw());

        if (matcher.find()) {
            var prefix = matcher.group(1);
            if (owoBot.getConfig().isUseDB()) {
                var settings = owoBot.getMongoDbContext().getGuildSettingsByGuildID(commandMessage.getGuild().getIdLong());
                if (settings == null) {
                    var newSettings = GuildSettings.getDefaultSettings();
                    newSettings.setGuildId(commandMessage.getGuild().getIdLong());
                    newSettings.setPrefixes(owoBot.getConfig().getPrefixes());
                    owoBot.getMongoDbContext().saveNewGuildSettings(newSettings);

                    var command = processMessage(commandMessage, matcher, prefix, newSettings.getPrefixes());
                    if (command != null) return command;
                } else {
                    var guildPrefixes = settings.getPrefixes();
                    var command = processMessage(commandMessage, matcher, prefix, guildPrefixes);
                    if (command != null) return command;
                }
            } else {
                var defaultPrefixes = owoBot.getConfig().getPrefixes();
                var command = processMessage(commandMessage, matcher, prefix, defaultPrefixes);
                if (command != null) return command;
            }
        }

        return new EmptyCommand();
    }

    @Nullable
    private Command processMessage(CommandMessage commandMessage, Matcher matcher, String prefix, Set<String> guildPrefixes) {
        if (guildPrefixes.contains(prefix)) {
            var trigger = matcher.group(2);
            var command = owoBot.getCommandCache().getCommand(trigger);
            if (command != null) {
                command.setCommandMessage(commandMessage);
                if (matcher.group(3) != null)
                    command.setParameters(matcher.group(3));
                log.info("Processed command: " + command);
                return command;
            }
        }
        return null;
    }
}
