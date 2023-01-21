package com.owobot.model.database;

import org.bson.types.ObjectId;

import java.util.LinkedHashSet;
import java.util.Set;

public class GuildSettings {
    private ObjectId id;
    private long guildId;
    private Set<Long> musicChannelIds;
    private int volume;
    private Set<String> prefixes;

    private boolean isMusicChannelEnabled;

    public GuildSettings() {
    }

    public GuildSettings(long guildId, Set<Long> musicChannelIds, int volume) {
        this.guildId = guildId;
        this.musicChannelIds = musicChannelIds;
        this.volume = volume;
        this.isMusicChannelEnabled = false;
    }

    public void update(GuildSettings settings) {
        this.guildId = settings.guildId;
        this.musicChannelIds = settings.musicChannelIds;
        this.volume = settings.volume;
        this.isMusicChannelEnabled = settings.isMusicChannelEnabled;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public Set<Long> getMusicChannelIds() {
        return musicChannelIds;
    }

    public void setMusicChannelIds(Set<Long> musicChannelIds) {
        this.musicChannelIds = musicChannelIds;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Set<String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(Set<String> prefixes) {
        this.prefixes = prefixes;
    }

    public boolean isMusicChannelEnabled() {
        return isMusicChannelEnabled;
    }

    public void setMusicChannelEnabled(boolean musicChannelEnabled) {
        isMusicChannelEnabled = musicChannelEnabled;
    }

    public static GuildSettings getDefaultSettings() {
        var settings = new GuildSettings();
        settings.volume = 100;
        settings.musicChannelIds = new LinkedHashSet<>();
        settings.prefixes = new LinkedHashSet<>();
        settings.guildId = 0L;
        return settings;
    }

    @Override
    public String toString() {
        return "GuildSettings{" +
                "id=" + id +
                ", guildId=" + guildId +
                ", musicChannelIds=" + musicChannelIds +
                ", volume=" + volume +
                '}';
    }
}
