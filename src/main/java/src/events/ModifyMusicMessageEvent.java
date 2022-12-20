package src.events;

import src.model.AudioTrackRequest;

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
