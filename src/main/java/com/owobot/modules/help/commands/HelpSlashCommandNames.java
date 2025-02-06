package com.owobot.modules.help.commands;

public enum HelpSlashCommandNames {
    GET_HELP_SLASH_COMMAND_NAME("help");

    private final String name;

    HelpSlashCommandNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
