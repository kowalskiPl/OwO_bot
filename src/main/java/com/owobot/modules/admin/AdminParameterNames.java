package com.owobot.modules.admin;

public enum AdminParameterNames {

    ADMIN_PARAMETER_MUSIC_CHANNEL("MusicChannel"),
    ADMIN_PARAMETER_ENABLE_MUSIC_CHANNEL("EnableMusicChannel"),
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
