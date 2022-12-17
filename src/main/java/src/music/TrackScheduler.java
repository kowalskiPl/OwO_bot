package src.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.events.*;
import src.events.Event;
import src.model.AudioTrackRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter implements Observable {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrackRequest> queue;
    private final List<Listener> listeners;
    private long currentEmbedMessageId;
    private MessageChannel currentEmbedLocation;
    private final long guildId;

    private static final Logger log = LoggerFactory.getLogger(TrackScheduler.class);

    public TrackScheduler(AudioPlayer player, long guildId) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.listeners = new ArrayList<>();
        this.guildId = guildId;
    }

    public TrackScheduler setCurrentEmbedMessageId(long embedMessage) {
        this.currentEmbedMessageId = embedMessage;
        return this;
    }

    public long getCurrentEmbedMessageId() {
        return currentEmbedMessageId;
    }

    public long getGuildId() {
        return guildId;
    }

    public TrackScheduler setCurrentEmbedLocation(MessageChannel currentEmbedLocation) {
        this.currentEmbedLocation = currentEmbedLocation;
        return this;
    }

    public MessageChannel getCurrentEmbedLocation() {
        return currentEmbedLocation;
    }

    public void enqueue(AudioTrackRequest audioTrackRequest) {
        if (!player.startTrack(audioTrackRequest.audioTrack, true)) {// check if the player is free and play the track if so, else enqueue
            queue.offer(audioTrackRequest);
            sendEvent(new SendMessageEvent(audioTrackRequest.channel, "Added: " + audioTrackRequest.audioTrack.getInfo().title + " to queue",10, this));
        } else {
            sendEvent(new SendMusicMessageEvent(audioTrackRequest, this));
        }
    }

    public void enqueue(List<AudioTrack> tracks, String name, MessageChannel channel, Member member) {
        if (!player.startTrack(tracks.get(0), true)) {
            tracks.forEach(track -> queue.offer(new AudioTrackRequest(track, channel, member)));
            sendEvent(new SendMessageEvent(channel, "Added playlist: " + name + " to queue", 5, this));
        } else {
            for (int i = 1; i < tracks.size(); i++) {
                queue.offer(new AudioTrackRequest(tracks.get(i), channel, member));
            }
            sendEvent(new SendMessageEvent(channel, "Now playing playlist: " + name, this));
        }
    }

    public void nextTrack() {
        var track = queue.poll();
        if (track != null) {
            player.startTrack(track.audioTrack, false);
            sendEvent(new ModifyMusicMessageEvent(track, this));
        }

        if (track == null){
            sendEvent(new DeleteMessageEvent(currentEmbedLocation, currentEmbedMessageId, 0, this));
            currentEmbedLocation = null;
            currentEmbedMessageId = 0;
        }
    }

    public void pause() {
        AudioTrack track = player.getPlayingTrack();
        if (track != null) {
            player.setPaused(!player.isPaused());
            sendEvent(new PlayerPauseEvent(currentEmbedLocation, !player.isPaused(), this));
        }
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
        player.setPaused(false);
        sendEvent(new DeleteMessageEvent(currentEmbedLocation, currentEmbedMessageId, 0, this));
        currentEmbedLocation = null;
        currentEmbedMessageId = 0;
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
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void notifyListeners(Event event) {
        this.listeners.forEach(listener -> listener.onEventReceived(event));
    }

    private void sendEvent(Event event){
        log.info("Sending event: " + event.getClass());
        notifyListeners(event);
    }
}
