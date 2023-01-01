package com.owobot.events;

import com.owobot.model.AudioTrackRequest;

public class SendMusicMessageEvent extends Event {
    private final AudioTrackRequest audioTrackRequest;
    private AudioTrackRequest nextTrack;

    public SendMusicMessageEvent(AudioTrackRequest audioTrackRequest, AudioTrackRequest nextTrack, Observable sender) {
        super(sender);
        this.audioTrackRequest = audioTrackRequest;
        this.nextTrack = nextTrack;
    }

    public SendMusicMessageEvent(AudioTrackRequest audioTrackRequest, Observable sender) {
        super(sender);
        this.audioTrackRequest = audioTrackRequest;
    }

    public AudioTrackRequest getAudioTrackRequest() {
        return audioTrackRequest;
    }

    public AudioTrackRequest getNextTrack() {
        return nextTrack;
    }
}
