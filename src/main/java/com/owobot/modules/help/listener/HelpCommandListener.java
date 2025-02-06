package com.owobot.modules.help.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.help.HelpCommandParameters;
import com.owobot.modules.help.commands.GetHelpCommand;
import com.owobot.modules.help.commands.HelpSlashCommandNames;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.time.Duration;
import java.util.stream.Collectors;

public class HelpCommandListener extends Reflectional implements CommandListener {
    public HelpCommandListener(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean onCommand(Command command) {

        if (command instanceof GetHelpCommand getHelpCommand) {
            var isGuildMessage = getHelpCommand.getCommandMessage().isGuildMessage();
            if (getHelpCommand.getParameterMap().isEmpty()) {
                EmbedBuilder builder = getBaseEmbedBuilder(false);
                if (isGuildMessage) {
                    builder.setFooter("This message and command will self-destruct in 30 seconds");
                    getHelpCommand.getCommandMessage()
                            .getMessage()
                            .getChannel()
                            .sendMessageEmbeds(builder.build())
                            .delay(Duration.ofSeconds(30))
                            .queue(message -> message.delete().queue(msg -> getHelpCommand.getCommandMessage().getMessage().delete().queue()),
                                    new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                } else {
                    getHelpCommand.getCommandMessage()
                            .getMessage()
                            .getChannel()
                            .sendMessageEmbeds(builder.build())
                            .queue();
                }
                return true;

            } else {
                var moduleName = getHelpCommand.getParameterMap().get(HelpCommandParameters.HELP_PARAMETER_MODULE_NAME.getName());
                var modules = owoBot.getModuleManager().getModules();
                var moduleInQuestion = modules.stream().filter(module -> module.getNameUserFriendly().equalsIgnoreCase(moduleName)).findFirst();

                if (moduleInQuestion.isPresent()) {
                    EmbedBuilder builder = getModuleSpecificEmbedBuilder(moduleInQuestion.get());

                    if (isGuildMessage) {
                        builder.setFooter("This message and command will self-destruct in 60 seconds");
                        getHelpCommand.getCommandMessage()
                                .getMessage()
                                .getChannel()
                                .sendMessageEmbeds(builder.build())
                                .delay(Duration.ofSeconds(60))
                                .queue(message -> message.delete()
                                        .queue(leMessage -> getHelpCommand.getCommandMessage().getMessage()
                                                .delete()
                                                .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE))));
                    } else {
                        getHelpCommand.getCommandMessage()
                                .getMessage()
                                .getChannel()
                                .sendMessageEmbeds(builder.build())
                                .queue();
                    }

                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.BLUE);
                    builder.setAuthor("It seems like the module you provided doesn't exist or is inactive. Try 'help' to get a list of enabled modules");
                    if (isGuildMessage) {
                        builder.setFooter("This message and command will self-destruct in 20 seconds");
                        getHelpCommand.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).delay(Duration.ofSeconds(20)).
                                queue(message -> message.delete().queue(leMessage -> getHelpCommand.getCommandMessage().getMessage().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE))));
                    } else {
                        builder.setFooter("This message will self-destruct in 20 seconds");
                        getHelpCommand.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).delay(Duration.ofSeconds(20)).
                                queue(message -> message.delete().queue());
                    }

                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onSlashCommand(SlashCommandInteractionEvent event) {
        if (event.getName().equals(HelpSlashCommandNames.GET_HELP_SLASH_COMMAND_NAME.getName())) {
            return handleSlashHelpCommand(event);
        }
        return false;
    }

    @Override
    public void shutdown() {

    }

    private boolean handleSlashHelpCommand(SlashCommandInteractionEvent event) {
        var selectedModule = event.getOption("module");
        if (selectedModule != null) {
            var moduleName = selectedModule.getAsString();
            var modules = owoBot.getModuleManager().getModules();
            var moduleInQuestion = modules.stream().filter(module -> module.getNameUserFriendly().equalsIgnoreCase(moduleName)).findFirst();
            if (moduleInQuestion.isPresent()) {
                EmbedBuilder builder = getModuleSpecificEmbedBuilder(moduleInQuestion.get());
                event.replyEmbeds(builder.build()).queue();
            } else {
                event.reply("something went wrong, module not found").queue();
            }

        } else {
            var embed = getBaseEmbedBuilder(true);
            event.replyEmbeds(embed.build()).queue();
        }
        return true;
    }

    private EmbedBuilder getBaseEmbedBuilder(boolean fromSlashCommand) {
        var moduleNames = owoBot.getModuleManager().getModuleNames();

        //always get rid of botAdmin module in slash command from server, no one needs to know from there that this module exists
        if (fromSlashCommand) {
            moduleNames = moduleNames.stream().filter(module -> !module.equalsIgnoreCase("botadmin")).collect(Collectors.toSet());
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Here is the list of loaded modules. Use help command with module name to get more info");
        StringBuilder sb = new StringBuilder();
        moduleNames.forEach(module -> sb.append("\n").append("--").append(module));
        builder.addField("Active modules:", sb.toString(), false);
        builder.setColor(Color.BLUE);

        return builder;
    }

    private EmbedBuilder getModuleSpecificEmbedBuilder(Module module) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.BLUE);
        builder.setAuthor("Commands for module:");
        builder.setTitle(module.getNameUserFriendly());

        var commands = module.getCommands();

        for (Command processedCommand : commands) {
            if (!processedCommand.isButtonCommand())
                builder.addField(processedCommand.getName(), processedCommand.getHelp(), false);
        }

        return builder;
    }
}
