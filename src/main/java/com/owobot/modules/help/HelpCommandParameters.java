package com.owobot.modules.help;

public enum HelpCommandParameters {
    HELP_PARAMETER_MODULE_NAME("HelpModule");

    private final String name;

    HelpCommandParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
