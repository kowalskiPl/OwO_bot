package src.events;

import net.dv8tion.jda.api.entities.MessageChannel;
import src.model.AudioTrackRequest;

public class SendMusicMessageEvent extends SendMessageEvent {
    private final AudioTrackRequest audioTrackRequest;

    public SendMusicMessageEvent(MessageChannel messageChannel, AudioTrackRequest audioTrackRequest) {
        super(messageChannel);
        this.audioTrackRequest = audioTrackRequest;
    }

    public AudioTrackRequest getAudioTrackRequest() {
        return audioTrackRequest;
    }
}
