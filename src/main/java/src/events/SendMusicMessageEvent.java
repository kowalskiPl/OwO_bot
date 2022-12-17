package src.events;

import net.dv8tion.jda.api.entities.MessageChannel;
import src.model.AudioTrackRequest;

public class SendMusicMessageEvent extends Event {
    private final AudioTrackRequest audioTrackRequest;

    public SendMusicMessageEvent(AudioTrackRequest audioTrackRequest, Observable sender) {
        super(sender);
        this.audioTrackRequest = audioTrackRequest;
    }


    public AudioTrackRequest getAudioTrackRequest() {
        return audioTrackRequest;
    }
}
