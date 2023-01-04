package com.owobot.modules.music;

import com.owobot.modules.music.model.AudioTrackRequest;
import com.owobot.utilities.SimpleTimeConverter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PlayerControlPanel {
    private TrackScheduler trackScheduler;
    private MessageChannel currentPlayerLocation;
    private long currentPlayerMessageID;

    private boolean notPlayingExists = false;
    public static final List<String> controlPanelButtonsIds = Collections.unmodifiableList(prepareButtonIds());

    public PlayerControlPanel(TrackScheduler trackScheduler) {
        this.trackScheduler = trackScheduler;
    }

    public void startNewControlPanel(AudioTrackRequest currentTrack, AudioTrackRequest nextTrack) {
        if (!notPlayingExists){
            sendEmbedPlayerMessage(currentTrack, nextTrack);
            notPlayingExists = false;
        } else {
            updateControlPanel(currentTrack, nextTrack);
        }
    }

    private static List<String> prepareButtonIds() {
        List<String> ids = new LinkedList<>();
        ids.add("suspend");
        ids.add("next");
        ids.add("halt");
        ids.add("exit");
        return ids;
    }

    public void updateControlPanel(AudioTrackRequest newTrack, AudioTrackRequest nextTrack) {
        var channel = newTrack.channel;
        if (channel.getIdLong() != currentPlayerLocation.getIdLong()) {
            currentPlayerLocation.deleteMessageById(currentPlayerMessageID).queue();
            currentPlayerMessageID = 0L;
            sendEmbedPlayerMessage(newTrack, nextTrack);
        } else {
            channel.retrieveMessageById(currentPlayerMessageID)
                    .queue(message -> message
                            .editMessageEmbeds(constructEmbedPlayerMessage(newTrack, nextTrack).build())
                            .setComponents(
                                    ActionRow.of(Button.secondary(controlPanelButtonsIds.get(0), "Pause"),
                                            Button.secondary(controlPanelButtonsIds.get(1), "Next"),
                                            Button.secondary(controlPanelButtonsIds.get(2), "Stop"),
                                            Button.danger(controlPanelButtonsIds.get(3), "Leave"))
                            )
                            .queue(), new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        }
    }

    public void pauseControlPanel(boolean isPaused) {
        String pause = isPaused ? "Pause" : "Resume";
        currentPlayerLocation
                .retrieveMessageById(currentPlayerMessageID)
                .queue(message -> message.editMessageEmbeds(message.getEmbeds().get(0)).setComponents(ActionRow.of(Button.secondary(controlPanelButtonsIds.get(0), pause),
                        Button.secondary(controlPanelButtonsIds.get(1), "Next"),
                        Button.secondary(controlPanelButtonsIds.get(2), "Stop"),
                        Button.danger(controlPanelButtonsIds.get(3), "Leave"))).queue());
    }

    public void setPlayingNothing() {
        var builder = constructEmbedPlayerMessage(null, null);
        currentPlayerLocation.retrieveMessageById(currentPlayerMessageID)
                .queue(message -> message.editMessageEmbeds(builder.build()).setComponents(ActionRow.of(
                        Button.danger(controlPanelButtonsIds.get(3), "Leave")
                )).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));
        notPlayingExists = true;
    }

    public void leave() {
        currentPlayerLocation.deleteMessageById(currentPlayerMessageID).queue();
        notPlayingExists = false;
    }

    private EmbedBuilder constructEmbedPlayerMessage(AudioTrackRequest request, AudioTrackRequest nextTrack) {
        EmbedBuilder builder = new EmbedBuilder();
        if (request != null) {
            builder.setAuthor("Now Playing:")
                    .setTitle(request.audioTrack.getInfo().title)
                    .setColor(Color.BLUE)
                    .addField("Total length", SimpleTimeConverter.getTimeStringFromLong(request.audioTrack.getInfo().length), true)
                    .addField("Author", request.audioTrack.getInfo().author, true)
                    .addField("Requester", request.requester.getEffectiveName(), false)
                    .setThumbnail(request.thumbnailUrl);
        } else {
            builder.setAuthor("Currently not playing anything:")
                    .setTitle("No song")
                    .setColor(Color.BLUE);
        }
        if (nextTrack != null) {
            builder.setFooter("Next track: " + nextTrack.audioTrack.getInfo().title);
        } else {
            builder.setFooter("No next track");
        }
        return builder;
    }

    private void sendEmbedPlayerMessage(AudioTrackRequest request, AudioTrackRequest nextTrack) {
        var builder = constructEmbedPlayerMessage(request, nextTrack);
        var channel = request.channel;
        channel.sendMessageEmbeds(builder.build())
                .setComponents(
                        ActionRow.of(Button.secondary(controlPanelButtonsIds.get(0), "Pause"),
                                Button.secondary(controlPanelButtonsIds.get(1), "Next"),
                                Button.secondary(controlPanelButtonsIds.get(2), "Stop"),
                                Button.danger(controlPanelButtonsIds.get(3), "Leave"))
                )
                .queue(message -> {
                    setCurrentPlayerMessageID(message.getIdLong());
                    setCurrentPlayerLocation(request.channel);
                }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public MessageChannel getCurrentPlayerLocation() {
        return currentPlayerLocation;
    }

    public void setCurrentPlayerLocation(MessageChannel currentPlayerLocation) {
        this.currentPlayerLocation = currentPlayerLocation;
    }

    public long getCurrentPlayerMessageID() {
        return currentPlayerMessageID;
    }

    public void setCurrentPlayerMessageID(long currentPlayerMessageID) {
        this.currentPlayerMessageID = currentPlayerMessageID;
    }
}
