package src.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import src.model.YouTubeVideo;
import src.utilities.listener.MessageSender;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GuildMusicManager {

    public final AudioPlayer player;

    public final TrackScheduler scheduler;

    public final Map<Integer, YouTubeVideo> recentSearchResults;

    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        recentSearchResults = new LinkedHashMap<>();
        scheduler.addListener(new MessageSender());
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
