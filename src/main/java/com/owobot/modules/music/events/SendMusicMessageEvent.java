package com.owobot.modules.music.events;

import com.owobot.events.Event;
import com.owobot.events.Observable;
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
