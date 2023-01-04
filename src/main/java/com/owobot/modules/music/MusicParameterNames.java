package com.owobot.modules.music;

public enum MusicParameterNames {
    MUSIC_PARAMETER_SONG_NAME("SongName"),
    MUSIC_PARAMETER_SONG_URL("SongUrl");

    private final String name;

    MusicParameterNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
