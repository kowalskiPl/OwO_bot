package com.owobot.modules.music.events;

import com.owobot.events.Event;
import com.owobot.events.Observable;
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
