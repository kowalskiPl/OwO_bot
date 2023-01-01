package com.owobot.utilities;

import com.owobot.commands.ParametrizedCommand;
import com.owobot.commands.Command;
import com.owobot.commands.EmptyCommand;
import com.owobot.commands.ParameterlessCommand;

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
