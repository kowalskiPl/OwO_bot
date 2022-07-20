package src.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.model.AudioTrackRequest;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrackRequest> queue;
    private MessageChannel linkedTextChannel;

    private static final Map<String, String> emojiMap = new LinkedHashMap<>(Map.of(
            "Pause", "U+23F8\t",
            "NextTrack", "U+23ED\t",
            "Stop", "U+23F9"
    ));

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

    public void enqueue(AudioTrackRequest audioTrackRequest) {
        if (!player.startTrack(audioTrackRequest.audioTrack, true)) {// check if the player is free and play the track if so, else enqueue
            queue.offer(audioTrackRequest);
            linkedTextChannel.sendMessage("Added: " + audioTrackRequest.audioTrack.getInfo().title + " to queue").queue();
        } else
            linkedTextChannel.sendMessage("Now playing: " + audioTrackRequest.audioTrack.getInfo().title).queue();
    }

    public void enqueue(List<AudioTrack> tracks, String name, MessageChannel channel, Member member) {
        if (!player.startTrack(tracks.get(0), true)) {
            tracks.forEach(track -> queue.offer(new AudioTrackRequest(track, channel, member)));
            linkedTextChannel.sendMessage("Added playlist: " + name + " to queue").queue();
        } else {
            for (int i = 1; i < tracks.size(); i++) {
                queue.offer(new AudioTrackRequest(tracks.get(i), channel, member));
            }
            linkedTextChannel.sendMessage("Now playing playlist: " + name + " to queue").queue();
        }
    }

    public void nextTrack() {
        var track = queue.poll();
        if (track != null) {
            player.startTrack(track.audioTrack, false);
            track.channel.sendMessage("Now playing: " + track.audioTrack.getInfo().title).queue();
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

    private EmbedBuilder constructEmbedPlayerMessage(AudioTrackRequest request) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Now playing: " + request.audioTrack.getInfo().title)
                .setColor(Color.BLUE)
                .setTimestamp(LocalDateTime.now())
                .addField("Requester", request.requester.getEffectiveName(), false)
                .addField("Author", request.audioTrack.getInfo().author, false);
        return builder;
    }

    private void sendEmbedPlayerMessage(AudioTrackRequest request){
        var builder = constructEmbedPlayerMessage(request);
        var channel = request.channel;
        channel.sendMessageEmbeds(builder.build())
                .setActionRows(
                        ActionRow.of(Button.secondary("Pause", emojiMap.get("Pause")))
                );
    }
}
