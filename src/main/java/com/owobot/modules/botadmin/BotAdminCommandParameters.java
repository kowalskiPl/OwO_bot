package com.owobot.modules.botadmin;

public enum BotAdminCommandParameters {
    BOT_ADMIN_TIME_TO_SHUTDOWN("TimeToShutdown");

    private final String name;

    BotAdminCommandParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

