package com.owobot.modules.admin;

public enum AdminParameterNames {
    ADMIN_PARAMETER_TEXT_CHANNEL("TextChannel"),
    ADMIN_PARAMETER_PREFIX("Prefix");

    private final String name;
    AdminParameterNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
