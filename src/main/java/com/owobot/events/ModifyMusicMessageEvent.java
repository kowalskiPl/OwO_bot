package com.owobot.events;

import com.owobot.model.AudioTrackRequest;

public class ModifyMusicMessageEvent extends Event {
    private final AudioTrackRequest audioTrackRequest;
    private AudioTrackRequest nextTrack;


    public ModifyMusicMessageEvent(AudioTrackRequest audioTrackRequest, Observable sender) {
        super(sender);
        this.audioTrackRequest = audioTrackRequest;
    }

    public ModifyMusicMessageEvent(AudioTrackRequest audioTrackRequest, AudioTrackRequest nextTrack, Observable sender) {
        super(sender);
        this.audioTrackRequest = audioTrackRequest;
        this.nextTrack = nextTrack;
    }

    public AudioTrackRequest getAudioTrackRequest() {
        return audioTrackRequest;
    }

    public AudioTrackRequest getNextTrack() {
        return nextTrack;
    }
}
