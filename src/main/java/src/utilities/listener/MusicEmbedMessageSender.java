package src.utilities.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import src.events.Event;
import src.events.Listener;
import src.events.SendMusicMessageEvent;
import src.model.AudioTrackRequest;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicEmbedMessageSender implements Listener {

    private static final Map<String, String> emojiMap = new LinkedHashMap<>(Map.of(
            "Pause", "U+23F8\t",
            "NextTrack", "U+23ED\t",
            "Stop", "U+23F9"
    ));

    @Override
    public void onEventReceived(Event event) {
        if (event instanceof SendMusicMessageEvent) {

        }
    }

    private EmbedBuilder constructEmbedPlayerMessage(AudioTrackRequest request) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Now playing: " + request.audioTrack.getInfo().title)
                .setColor(Color.BLUE)
                .setTimestamp(LocalDateTime.now())
                .addField("Requester", request.requester.getEffectiveName(), false)
                .addField("Author", request.audioTrack.getInfo().author, false);
        return builder;
    }

    private void sendEmbedPlayerMessage(AudioTrackRequest request) {
        var builder = constructEmbedPlayerMessage(request);
        var channel = request.channel;
        channel.sendMessageEmbeds(builder.build())
                .setActionRows(
                        ActionRow.of(Button.secondary("Pause", emojiMap.get("Pause")))
                );
    }
}
