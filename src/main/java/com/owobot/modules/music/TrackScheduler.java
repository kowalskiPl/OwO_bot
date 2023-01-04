package com.owobot.modules.music;

import com.owobot.modules.music.model.AudioTrackRequest;
import com.owobot.modules.music.youtube.HttpYouTubeRequester;
import com.owobot.modules.music.youtube.YouTubeRequestResultParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrackRequest> queue;
    private AudioTrackRequest currentMusic;
    private long currentEmbedMessageId;
    private TextChannel currentEmbedLocation;
    private final long guildId;
    private final PlayerControlPanel controlPanel;

    private static final Logger log = LoggerFactory.getLogger(TrackScheduler.class);

    public TrackScheduler(AudioPlayer player, long guildId) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guildId = guildId;
        controlPanel = new PlayerControlPanel(this);
    }

    public synchronized TrackScheduler setCurrentEmbedMessageId(long embedMessage) {
        this.currentEmbedMessageId = embedMessage;
        return this;
    }

    public long getCurrentEmbedMessageId() {
        return currentEmbedMessageId;
    }

    public long getGuildId() {
        return guildId;
    }

    public synchronized TrackScheduler setCurrentEmbedLocation(TextChannel currentEmbedLocation) {
        this.currentEmbedLocation = currentEmbedLocation;
        return this;
    }

    public TextChannel getCurrentEmbedLocation() {
        return currentEmbedLocation;
    }

    public void enqueue(AudioTrackRequest audioTrackRequest) {
        if (!player.startTrack(audioTrackRequest.audioTrack, true)) {// check if the player is free and play the track if so, else enqueue
            queue.offer(audioTrackRequest);
            controlPanel.updateControlPanel(currentMusic, queue.peek());
        } else {
            controlPanel.startNewControlPanel(audioTrackRequest, queue.peek());
            currentMusic = audioTrackRequest;
        }
    }

    public void enqueue(List<AudioTrack> tracks, String name, TextChannel channel, Member member) {
        if (!player.startTrack(tracks.get(0), true)) {
            tracks.forEach(track -> {
                var thumbnailUrl = getThumbnailUrl(track);
                queue.offer(new AudioTrackRequest(track, channel, member, thumbnailUrl));
            });
        } else {
            String firstThumbnailUrl = getThumbnailUrl(tracks.get(0));
            for (int i = 1; i < tracks.size(); i++) {
                var thumbnailUrl = getThumbnailUrl(tracks.get(i));
                queue.offer(new AudioTrackRequest(tracks.get(i), channel, member, thumbnailUrl));
            }
            currentMusic = new AudioTrackRequest(tracks.get(0), channel, member, firstThumbnailUrl);
            controlPanel.startNewControlPanel(currentMusic, queue.peek());
        }
    }

    private String getThumbnailUrl(AudioTrack track) {
        var firstResult = HttpYouTubeRequester.queryYoutubeVideo(track.getInfo().uri);
        String firstThumbnailUrl = "";
        if (firstResult.isPresent()) {
            firstThumbnailUrl = YouTubeRequestResultParser.getThumbnailUrlFromYouTubeUrl(firstResult.get());
        }
        return firstThumbnailUrl;
    }

    public void nextTrack() {
        var track = queue.poll();
        if (track != null) {
            player.startTrack(track.audioTrack, false);
            currentMusic = track;
            controlPanel.updateControlPanel(track, queue.peek());
        }

        if (track == null) {
            currentMusic = null;
            currentEmbedLocation = null;
            currentEmbedMessageId = 0;
            controlPanel.setPlayingNothing();
        }
    }

    public void pause() {
        AudioTrack track = player.getPlayingTrack();
        if (track != null) {
            player.setPaused(!player.isPaused());
            controlPanel.pauseControlPanel(!player.isPaused());
        }
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
        player.setPaused(false);
        currentEmbedLocation = null;
        currentEmbedMessageId = 0;
        currentMusic = null;
        controlPanel.setPlayingNothing();
    }

    public void leave() {
        player.stopTrack();
        queue.clear();
        player.setPaused(false);
        currentEmbedLocation = null;
        currentEmbedMessageId = 0;
        currentMusic = null;
        controlPanel.leave();
    }

    public AudioTrack getCurrentTrack() {
        return player.getPlayingTrack();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        if (queue.isEmpty()){
            stop();
        }

        super.onTrackException(player, track, exception);
    }
}
