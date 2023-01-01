package com.owobot.music;

import com.owobot.model.YouTubeVideo;
import com.owobot.utilities.ServiceContext;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GuildMusicManager {

    public final AudioPlayer player;

    public final TrackScheduler scheduler;

    public final Map<Integer, YouTubeVideo> recentSearchResults;

    public GuildMusicManager(AudioPlayerManager manager, long guildId) {
        player = manager.createPlayer();
        player.setFrameBufferDuration(ServiceContext.getConfig().getFrameBufferDuration());
        scheduler = new TrackScheduler(player, guildId);
        player.addListener(scheduler);
        recentSearchResults = new LinkedHashMap<>();
    }

    public AudioPlayerTaskSendHandler getSendHandler(){
        return new AudioPlayerTaskSendHandler(player);
    }

    public void addSearchResults(List<YouTubeVideo> videoList) {
        for (int i = 0; i < videoList.size(); i++){
            recentSearchResults.put(i, videoList.get(i));
        }
    }

    public YouTubeVideo getVideoAndClearSearchResults(int searchIndex) {
        var result = recentSearchResults.get(searchIndex);
        recentSearchResults.clear();
        return result;
    }

}
