package src.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private MessageChannel linkedTextChannel;

    private static final Logger log = LoggerFactory.getLogger(TrackScheduler.class);

    public Channel getLinkedTextChannel() {
        return linkedTextChannel;
    }

    public void setLinkedTextChannel(MessageChannel linkedTextChannel) {
        this.linkedTextChannel = linkedTextChannel;
    }

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void enqueue(AudioTrack track) {
        if (!player.startTrack(track, true)) {// check if the player is free and play the track if so, else enqueue
            queue.offer(track);
            linkedTextChannel.sendMessage("Added: " + track.getInfo().title + " to queue").queue();
        } else
            linkedTextChannel.sendMessage("Now playing: " + track.getInfo().title).queue();
    }

    public void enqueue(List<AudioTrack> tracks, String name) {
        if (!player.startTrack(tracks.get(0), true)) {
            tracks.forEach(queue::offer);
            linkedTextChannel.sendMessage("Added playlist: " + name + " to queue").queue();
        } else {
            for (int i = 1; i < tracks.size(); i++) {
                queue.offer(tracks.get(i));
            }
            linkedTextChannel.sendMessage("Now playing playlist: " + name + " to queue").queue();
        }
    }

    public void nextTrack() {
        var track = queue.poll();
        if (track != null) {
            player.startTrack(track, false);
            linkedTextChannel.sendMessage("Now playing: " + track.getInfo().title).queue();
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
}
