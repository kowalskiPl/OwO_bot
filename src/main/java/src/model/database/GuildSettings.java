package src.model.database;

import org.bson.types.ObjectId;

import java.util.Set;

public class GuildSettings {
    private ObjectId id;
    private long guildId;
    private Set<Long> musicChannelIds;
    private int volume;

    public GuildSettings() {
    }

    public GuildSettings(long guildId, Set<Long> musicChannelIds, int volume) {
        this.guildId = guildId;
        this.musicChannelIds = musicChannelIds;
        this.volume = volume;
    }

    public void update(GuildSettings settings) {
        this.guildId = settings.guildId;
        this.musicChannelIds = settings.musicChannelIds;
        this.volume = settings.volume;
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
