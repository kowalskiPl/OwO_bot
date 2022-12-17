package src.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.internal.JDAImpl;
import src.events.Event;
import src.events.Listener;
import src.events.ModifyMusicMessageEvent;
import src.events.SendMusicMessageEvent;
import src.model.AudioTrackRequest;
import src.music.TrackScheduler;

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
        if (event instanceof SendMusicMessageEvent sendMusicMsgEvt) {
            if (sendMusicMsgEvt.getSender() instanceof TrackScheduler scheduler) {
                sendEmbedPlayerMessage(sendMusicMsgEvt.getAudioTrackRequest(), scheduler);
            }
        }

        if (event instanceof ModifyMusicMessageEvent modifyMusicMessageEvent) {
            if (modifyMusicMessageEvent.getSender() instanceof TrackScheduler scheduler) {

                modifyMusicMessageEvent.getAudioTrackRequest().channel.retrieveMessageById(scheduler.getEmbedMessageId())
                        .queue(message -> message.editMessageEmbeds(constructEmbedPlayerMessage(modifyMusicMessageEvent.getAudioTrackRequest()).build()).queue(),
                                new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                //TODO: error handling when different channels were used
//                var guildId = scheduler.getGuildId();
//                var guild = modifyMusicMessageEvent.getAudioTrackRequest().channel.getJDA().getGuildById(guildId);
            }
        }
    }

    private EmbedBuilder constructEmbedPlayerMessage(AudioTrackRequest request) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Now playing: " + request.audioTrack.getInfo().title)
                .setColor(Color.BLUE)
                .addField("Requester", request.requester.getEffectiveName(), false)
                .addField("Author", request.audioTrack.getInfo().author, false);
        return builder;
    }

    private void sendEmbedPlayerMessage(AudioTrackRequest request, TrackScheduler scheduler) {
        var builder = constructEmbedPlayerMessage(request);
        var channel = request.channel;
        channel.sendMessageEmbeds(builder.build())
                .setActionRows(
                        ActionRow.of(Button.secondary("Pause", "Pause"),
                                Button.secondary("Next", "Next"))
                )
                .queue(message -> scheduler.setEmbedMessageId(message.getIdLong()), new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }
}
