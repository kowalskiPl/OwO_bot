package src.model;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class AudioTrackRequest {
    public AudioTrack audioTrack;
    public MessageChannel channel;
    public Member requester;

    public AudioTrackRequest(AudioTrack audioTrack, MessageChannel channel, Member requester) {
        this.audioTrack = audioTrack;
        this.channel = channel;
        this.requester = requester;
    }
}
