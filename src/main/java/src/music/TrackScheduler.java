package src.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void enqueue(AudioTrack track) {
        if (!player.startTrack(track, true)) // check if the player is free and play the track if so, else enqueue
            queue.offer(track);
    }

    public void nextTrack(){
        player.startTrack(queue.poll(), false);
    }

    public void pause(){
        AudioTrack track = player.getPlayingTrack();
        if (track != null){
            player.setPaused(!player.isPaused());
        }
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
    }

    public AudioTrack getCurrentTrack(){
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
