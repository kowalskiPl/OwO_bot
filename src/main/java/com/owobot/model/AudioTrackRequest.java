package com.owobot.model;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class AudioTrackRequest {
    public AudioTrack audioTrack;
    public TextChannel channel;
    public Member requester;
    public String thumbnailUrl;

    public AudioTrackRequest(AudioTrack audioTrack, TextChannel channel, Member requester, String thumbnailUrl) {
        this.audioTrack = audioTrack;
        this.channel = channel;
        this.requester = requester;
        this.thumbnailUrl = thumbnailUrl;
    }
}
