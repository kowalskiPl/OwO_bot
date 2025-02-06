package com.owobot.modules.admin.commands;

public enum AdminSlashCommandsNames {
    REFRESH_SLASH_COMMANDS_COMMAND_NAME("refresh-slash-commands"),
    REMOVE_MUSIC_CHANNEL_COMMAND_NAME("remove-music-channel"),
    ADD_MUSIC_CHANNEL_COMMAND_NAME("add-music-channel"),
    GET_MUSIC_CHANNELS_COMMAND_NAME("get-music-channels"),
    ENABLE_MUSIC_CHANNEL_COMMAND_NAME("enable-music-channel"),
    LIST_PREFIXES_COMMAND_NAME("list-prefixes"),
    REMOVE_PREFIX_COMMAND_NAME("remove-prefix"),
    ADD_PREFIX_COMMAND_NAME("add-prefix");

    private final String name;

    AdminSlashCommandsNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
