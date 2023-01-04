package com.owobot.modules.music;

public enum MusicParameterNames {
    MUSIC_PARAMETER_BUTTON_ID("PressedButtonId"),
    MUSIC_PARAMETER_IS_SONG_URL("IsUrl"),
    MUSIC_PARAMETER_SONG_NAME("SongName");

    private final String name;

    MusicParameterNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
