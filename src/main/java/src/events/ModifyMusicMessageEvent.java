package src.events;

import src.model.AudioTrackRequest;

public class ModifyMusicMessageEvent extends Event{
    private final AudioTrackRequest audioTrackRequest;

    public ModifyMusicMessageEvent(AudioTrackRequest audioTrackRequest, Observable sender) {
        super(sender);
        this.audioTrackRequest = audioTrackRequest;
    }

    public AudioTrackRequest getAudioTrackRequest() {
        return audioTrackRequest;
    }
}
