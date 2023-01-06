package com.owobot.modules.help.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.help.HelpCommandParameters;
import com.owobot.modules.help.commands.GetHelpCommand;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.time.Duration;

public class HelpCommandListener extends Reflectional implements CommandListener {
    public HelpCommandListener(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean onCommand(Command command) {

        if (command instanceof GetHelpCommand getHelpCommand) {
            var moduleNames = owoBot.getModuleManager().getModuleNames();
            if (getHelpCommand.getParameterMap().isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Here is the list of loaded modules. Use <prefix>help with module name to get more info");

                StringBuilder sb = new StringBuilder();
                moduleNames.forEach(module -> sb.append("\n").append("--").append(module));
                builder.addField("Active modules:", sb.toString(), false);

                builder.setColor(Color.BLUE);
                builder.setFooter("This message and command will self-destruct in 30 seconds");

                getHelpCommand.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessageEmbeds(builder.build())
                        .delay(Duration.ofSeconds(30))
                        .queue(message -> message.delete().queue(msg -> getHelpCommand.getCommandMessage().getMessage().delete().queue()),
                                new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                return true;
            } else {
                var moduleName = getHelpCommand.getParameterMap().get(HelpCommandParameters.HELP_PARAMETER_MODULE_NAME.getName());
                var modules = owoBot.getModuleManager().getModules();
                var moduleInQuestion = modules.stream().filter(module -> module.getNameUserFriendly().equalsIgnoreCase(moduleName)).findFirst();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.BLUE);
                if (moduleInQuestion.isPresent()){
                    var loadedModule = moduleInQuestion.get();
                    builder.setAuthor("Commands for module:");
                    var commands = loadedModule.getCommands();
                    StringBuilder sb = new StringBuilder();

                    for (Command processedCommand : commands) {
                        if (!processedCommand.isButtonCommand())
                            sb.append("\n").append(processedCommand.getHelp()).append("\n");
                    }
                    builder.addField(loadedModule.getNameUserFriendly(), sb.toString(), false);
                    builder.setFooter("This message and command will self-destruct in 60 seconds");
                    getHelpCommand.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).delay(Duration.ofSeconds(60)).
                            queue(message -> message.delete().queue(leMessage -> getHelpCommand.getCommandMessage().getMessage().delete().queue()));
                } else {
                    builder.setAuthor("It seems like the module you provided doesn't exist or is inactive. Try 'help' to get a list of enabled modules");
                    builder.setFooter("This message and command will self-destruct in 20 seconds");
                    getHelpCommand.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).delay(Duration.ofSeconds(20)).
                            queue(message -> message.delete().queue(leMessage -> getHelpCommand.getCommandMessage().getMessage().delete().queue()));
                }
            }
            return true;
        }
        return false;
    }
}