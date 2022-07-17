package src.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.exception.UserNotInVoiceChannelException;
import src.model.YouTubeVideo;
import src.music.GuildMusicManager;
import src.youtube.YouTubeSearch;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MusicListenerAdapter extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MusicListenerAdapter.class);
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> guildMusicManagers;
    private final Map<String, String> trackButtonsIds;

    public MusicListenerAdapter() {
        guildMusicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        trackButtonsIds = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            trackButtonsIds.put("select_" + i, "" + i);
        }
        trackButtonsIds.put("cancel_10", "Cancel");
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = guild.getIdLong();
        var musicManager = guildMusicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            guildMusicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        Message message = event.getMessage();
        User author = message.getAuthor();
        String content = message.getContentRaw();
        Guild guild = event.getGuild();

        if (author.isBot())
            return;

        if (!event.isFromGuild())
            return;

        String[] arr = content.split(" ", 2);


        switch (arr[0]) {
            case "!play" -> handlePlayCommand(event, arr[1]);
            case "!pause" -> handlePauseCommand(event);
            case "!skip" -> handleSkipCommand(event);
            case "!stop" -> handleStopCommand(event);
            case "!disconnect", "!leave" -> handleDisconnectCommand(event);
        }

        super.onMessageReceived(event);
    }

    private void handlePlayCommand(MessageReceivedEvent event, String songName) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        var channel = event.getChannel();

        if (UrlValidator.getInstance().isValid(songName)) {
            processUrlPlayRequest(event.getMember(), event.getGuild(), songName, musicManager, channel);
        } else {
            processSearchPlayRequest(event, songName, musicManager, channel);
        }
    }

    private void processUrlPlayRequest(Member member, Guild guild, String songName, GuildMusicManager musicManager, MessageChannel channel) {
        playerManager.loadItemOrdered(musicManager, songName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                try {
                    musicManager.setTextChannel(channel);
                    play(member, guild, musicManager, track);
                } catch (UserNotInVoiceChannelException e) {
                    log.warn("Failed to acquire user channel");
                    channel.sendMessage("Are you in the voice channel?").queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                musicManager.setTextChannel(channel);
                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                try {
                    play(member, guild, musicManager, firstTrack);
                } catch (UserNotInVoiceChannelException e) {
                    log.warn("Failed to acquire user channel");
                    channel.sendMessage("Are you in the voice channel?").queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + songName).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        log.info("Pressed button Id: " + event.getButton().getId());
        String buttonId = event.getButton().getId();
        handleTrackSelectionButtonPress(event);
        event.getMessage().delete().queue();
        super.onButtonInteraction(event);
    }

    private void processSearchPlayRequest(MessageReceivedEvent event, String songName, GuildMusicManager musicManager, MessageChannel channel) {
        var results = YouTubeSearch.performVideoQuery(songName);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Search results");
        builder.setColor(Color.BLUE);
        builder.setTimestamp(LocalDateTime.now());

        for (int i = 0; i < results.size(); i++) {
            YouTubeVideo result = results.get(i);
            builder.addField(i + " " + result.title, "**Length**: " + result.length + " **Author**: " + result.author, false);
        }

        var entrySet = trackButtonsIds.entrySet().stream().toList();
        channel.sendMessageEmbeds(builder.build())
                .setActionRows(
                        ActionRow.of(Button.secondary(entrySet.get(0).getKey(), entrySet.get(0).getValue()),
                                Button.secondary(entrySet.get(1).getKey(), entrySet.get(1).getValue()),
                                Button.secondary(entrySet.get(2).getKey(), entrySet.get(2).getValue()),
                                Button.secondary(entrySet.get(3).getKey(), entrySet.get(3).getValue()),
                                Button.secondary(entrySet.get(4).getKey(), entrySet.get(4).getValue())),
                        ActionRow.of(Button.secondary(entrySet.get(5).getKey(), entrySet.get(5).getValue()),
                                Button.secondary(entrySet.get(6).getKey(), entrySet.get(6).getValue()),
                                Button.secondary(entrySet.get(7).getKey(), entrySet.get(7).getValue()),
                                Button.secondary(entrySet.get(8).getKey(), entrySet.get(8).getValue()),
                                Button.secondary(entrySet.get(9).getKey(), entrySet.get(9).getValue())),
                        ActionRow.of(Button.danger(entrySet.get(10).getKey(), entrySet.get(10).getValue())))
                .delay(Duration.ofSeconds(30))
                .flatMap(Message::delete)
                .queue(e -> channel.sendMessage("Request timed out!").queue(), new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
        musicManager.addSearchResults(results);
    }

    private void handleTrackSelectionButtonPress(ButtonInteractionEvent event) {
        var id = event.getButton().getId();
        var button = trackButtonsIds.get(id);
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        if (!button.equals("Cancel")) {
            var video = musicManager.getVideoAndClearSearchResults(Integer.parseInt(button));
            var url = "https://www.youtube.com" + video.watchUrl;
            processUrlPlayRequest(event.getMember(), event.getGuild(), url, musicManager, event.getChannel());
        }
    }

    private void play(Member member, Guild guild, GuildMusicManager musicManager, AudioTrack track) throws UserNotInVoiceChannelException {
        connectToVoiceChannel(member, guild.getAudioManager());

        musicManager.scheduler.enqueue(track);
    }

    private static void connectToVoiceChannel(Member member, AudioManager audioManager) throws UserNotInVoiceChannelException {
        if (member != null) {
            GuildVoiceState state = member.getVoiceState();
            if (state != null) {
                AudioChannel channel = state.getChannel();
                if (!audioManager.isConnected()) {
                    if (channel != null) {
                        audioManager.openAudioConnection(channel);
                    } else {
                        throw new UserNotInVoiceChannelException();
                    }
                }
            }
        }
    }

    private void handlePauseCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.pause();
        }
    }

    private void handleStopCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.stop();
            var channel = event.getChannel();
            channel.sendMessage("Stopped playing, queue cleared").queue();
        }
    }

    private void handleSkipCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.nextTrack();
        }
    }

    private void handleDisconnectCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
        }
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.stop();
        var channel = event.getChannel();
        channel.sendMessage("Disconnected, cleared queue").queue();
    }
}
