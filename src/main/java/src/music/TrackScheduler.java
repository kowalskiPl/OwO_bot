package src.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.events.Event;
import src.events.Listener;
import src.events.Observable;
import src.events.SendMessageEvent;
import src.model.AudioTrackRequest;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter implements Observable {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrackRequest> queue;
    private final List<Listener> listeners;

    private static final Logger log = LoggerFactory.getLogger(TrackScheduler.class);

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.listeners = new ArrayList<>();
    }

    public void enqueue(AudioTrackRequest audioTrackRequest) {
        if (!player.startTrack(audioTrackRequest.audioTrack, true)) {// check if the player is free and play the track if so, else enqueue
            queue.offer(audioTrackRequest);
            sendMessageEvent(constructMessageSendEvent(audioTrackRequest.channel, "Added: " + audioTrackRequest.audioTrack.getInfo().title + " to queue"));
        } else
            sendMessageEvent(constructMessageSendEvent(audioTrackRequest.channel, "Now playing: " + audioTrackRequest.audioTrack.getInfo().title));
    }

    public void enqueue(List<AudioTrack> tracks, String name, MessageChannel channel, Member member) {
        if (!player.startTrack(tracks.get(0), true)) {
            tracks.forEach(track -> queue.offer(new AudioTrackRequest(track, channel, member)));
            sendMessageEvent(constructMessageSendEvent(channel, "Added playlist: " + name + " to queue"));
        } else {
            for (int i = 1; i < tracks.size(); i++) {
                queue.offer(new AudioTrackRequest(tracks.get(i), channel, member));
            }
            sendMessageEvent(constructMessageSendEvent(channel, "Now playing playlist: " + name));
        }
    }

    public void nextTrack() {
        var track = queue.poll();
        if (track != null) {
            player.startTrack(track.audioTrack, false);
            sendMessageEvent(constructMessageSendEvent(track.channel, "Now playing: " + track.audioTrack.getInfo().title));
        }
    }

    public void pause() {
        AudioTrack track = player.getPlayingTrack();
        if (track != null) {
            player.setPaused(!player.isPaused());
        }
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
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

    private SendMessageEvent constructMessageSendEvent(MessageChannel channel, String message) {
        return new SendMessageEvent(channel, message);
    }

    private void sendMessageEvent(SendMessageEvent event) {
        log.debug("Event sent: " + event.getClass());
        notifyListeners(event);
    }
}
