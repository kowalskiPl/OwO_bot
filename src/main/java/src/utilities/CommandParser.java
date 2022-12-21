package src.utilities;

import net.dv8tion.jda.internal.utils.tuple.Pair;
import src.model.commands.Command;
import src.model.commands.EmptyCommand;
import src.model.commands.ParameterlessCommand;
import src.model.commands.ParametrizedCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private static final Pattern commandRecognitionPattern = Pattern.compile("^([!,\\-.])(\\w*)\s*?(.*)$");

    public static Command processMessage(String message) {
        Matcher m = commandRecognitionPattern.matcher(message);

        if (m.find()) {
            String prefix = m.group(1);
            if (!ServiceContext.getConfig().getPrefixes().contains(prefix)) {
                return new EmptyCommand();
            }

            String command = m.group(2).trim();
            String parameter = m.group(3).trim();

            if (parameter.isEmpty()) {
                return new ParameterlessCommand(command);
            }

            return new ParametrizedCommand(command, parameter.split("\s"));
        }

        return new EmptyCommand();
    }
}
