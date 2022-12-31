package src.utilities.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.events.*;
import src.events.Event;
import src.model.AudioTrackRequest;
import src.music.TrackScheduler;
import src.utilities.SimpleTimeConverter;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicEmbedMessageSender implements Listener {

    private static final Logger log = LoggerFactory.getLogger(MusicEmbedMessageSender.class);

    private static final Map<String, String> emojiMap = new LinkedHashMap<>(Map.of(
            "Pause", "U+23F8\t",
            "NextTrack", "U+23ED\t",
            "Stop", "U+23F9"
    ));

    @Override
    public boolean onEventReceived(Event event) {
        if (event instanceof SendMusicMessageEvent sendMusicMsgEvt) {
            if (sendMusicMsgEvt.getSender() instanceof TrackScheduler scheduler) {
                sendEmbedPlayerMessage(sendMusicMsgEvt.getAudioTrackRequest(), sendMusicMsgEvt.getNextTrack(), scheduler);
                return true;
            }
        }

        if (event instanceof ModifyMusicMessageEvent modifyMusicMessageEvent) {
            if (modifyMusicMessageEvent.getSender() instanceof TrackScheduler scheduler) {
                var channel = modifyMusicMessageEvent.getAudioTrackRequest().channel;
                if (channel.getIdLong() != scheduler.getCurrentEmbedLocation().getIdLong()) {
                    scheduler.getCurrentEmbedLocation().deleteMessageById(scheduler.getCurrentEmbedMessageId()).queue();
                    sendEmbedPlayerMessage(modifyMusicMessageEvent.getAudioTrackRequest(), modifyMusicMessageEvent.getNextTrack(), scheduler);
                } else {
                    channel.retrieveMessageById(scheduler.getCurrentEmbedMessageId())
                            .queue(message -> message
                                    .editMessageEmbeds(constructEmbedPlayerMessage(modifyMusicMessageEvent.getAudioTrackRequest(), modifyMusicMessageEvent.getNextTrack()).build())
                                    .queue(), new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                }
                return true;
            }
        }

        if (event instanceof PlayerPauseEvent pauseEvent) {
            if (pauseEvent.getSender() instanceof TrackScheduler scheduler) {
                String pause = pauseEvent.isPaused() ? "Pause" : "Resume";
                pauseEvent
                        .getMessageChannel()
                        .retrieveMessageById(scheduler.getCurrentEmbedMessageId())
                        .queue(message -> message.editMessageEmbeds(message.getEmbeds().get(0)).setActionRows(ActionRow.of(Button.secondary("Pause", pause),
                                Button.secondary("Next", "Next"),
                                Button.secondary("Stop", "Stop"),
                                Button.secondary("Leave", "Leave"))).queue());
                return true;
            }
        }
        return false;
    }

    private EmbedBuilder constructEmbedPlayerMessage(AudioTrackRequest request, AudioTrackRequest nextTrack) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Now Playing:")
                .setTitle(request.audioTrack.getInfo().title)
                .setColor(Color.BLUE)
                .addField("Total length", SimpleTimeConverter.getTimeStringFromLong(request.audioTrack.getInfo().length), true)
                .addField("Author", request.audioTrack.getInfo().author, true)
                .addField("Requester", request.requester.getEffectiveName(), false)
                .setThumbnail(request.thumbnailUrl);
        if (nextTrack != null){
            builder.setFooter("Next track: " + nextTrack.audioTrack.getInfo().title);
        } else {
            builder.setFooter("No next track");
        }
        return builder;
    }

    private void sendEmbedPlayerMessage(AudioTrackRequest request, AudioTrackRequest nextTrack, TrackScheduler scheduler) {
        var builder = constructEmbedPlayerMessage(request, nextTrack);
        var channel = request.channel;
        channel.sendMessageEmbeds(builder.build())
                .setActionRows(
                        ActionRow.of(Button.secondary("Pause", "Pause"),
                                Button.secondary("Next", "Next"),
                                Button.secondary("Stop", "Stop"),
                                Button.secondary("Leave", "Leave"))
                )
                .queue(message -> scheduler.setCurrentEmbedMessageId(message.getIdLong()).setCurrentEmbedLocation(request.channel), new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }
}
