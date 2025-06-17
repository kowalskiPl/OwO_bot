package com.owobot.modules.music.listener;

import com.google.common.collect.Lists;
import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.exception.UserNotInVoiceChannelException;
import com.owobot.modules.music.GuildMusicManager;
import com.owobot.modules.music.MusicParameterNames;
import com.owobot.modules.music.SongRequestProcessingException;
import com.owobot.modules.music.commands.*;
import com.owobot.modules.music.model.AudioTrackRequest;
import com.owobot.modules.music.model.YouTubeVideo;
import com.owobot.modules.music.youtube.HttpYouTubeRequester;
import com.owobot.modules.music.youtube.YouTubeRequestResultParser;
import com.owobot.modules.music.youtube.YouTubeSearch;
import com.owobot.utilities.Reflectional;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MusicCommandListener extends Reflectional implements CommandListener {
    private static final Logger log = LoggerFactory.getLogger(MusicCommandListener.class);
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> guildMusicManagers;
    public static final Map<String, String> trackButtonsIds = Collections.unmodifiableMap(createSelectTrackButtonData());

    public MusicCommandListener(OwoBot owoBot) {
        super(owoBot);
        guildMusicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();

        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(true);

        // get the youtube refresh token so it kinda actually works
        youtubeAudioSourceManager.useOauth2(owoBot.getConfig().getYoutubeRefreshToken(), false);

        playerManager.registerSourceManager(youtubeAudioSourceManager);

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private static Map<String, String> createSelectTrackButtonData() {
        Map<String, String> buttons = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            buttons.put("select_" + i, "" + i);
        }
        buttons.put("cancel_10", "Cancel");
        return buttons;
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = guild.getIdLong();
        var musicManager = guildMusicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(owoBot, playerManager);
            guildMusicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public boolean onCommand(Command command) {
        boolean result = false;
        if (command instanceof PlayCommand playCommand) {
            result = handlePlayCommand(playCommand);
            playCommand.getCommandMessage().getMessage().delete().queue();
        }

        if (command instanceof StopCommand stopCommand) {
            result = handleStopCommand(stopCommand);
            stopCommand.getCommandMessage().getMessage().delete().queue();
        }

        if (command instanceof LeaveCommand leaveCommand) {
            result = handleLeaveCommand(leaveCommand);
            leaveCommand.getCommandMessage().getMessage().delete().queue();
        }

        if (command instanceof PauseCommand pauseCommand) {
            result = handlePauseCommand(pauseCommand);
            pauseCommand.getCommandMessage().getMessage().delete().queue();
        }

        if (command instanceof SkipCommand skipCommand) {
            result = handleSkipCommand(skipCommand);
            skipCommand.getCommandMessage().getMessage().delete().queue();
        }

        if (command instanceof SearchResultButtonPressedCommand buttonPressedCommand) {
            result = handleTrackSelectionButtonPress(buttonPressedCommand);
        }

        if (command instanceof ControlPanelButtonPressCommand buttonPressCommand) {
            result = handleEmbedPlayerButtonPress(buttonPressCommand);
        }

        return result;
    }

    @Override
    public void shutdown() {
        guildMusicManagers.forEach((k, player) -> player.scheduler.leave());
        playerManager.shutdown();
    }

    private boolean handlePlayCommand(PlayCommand command) {
        if (!isUserInVoiceChat(command)) {
            command.getCommandMessage().getMessage().reply("You have to be in a voice channel!")
                    .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS), new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            return true;
        }

        GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
        var channel = command.getCommandMessage().getMessageChannel();

        var songName = command.getParameterMap().get(MusicParameterNames.MUSIC_PARAMETER_SONG_NAME.getName());
        if (Boolean.parseBoolean(command.getParameterMap().get(MusicParameterNames.MUSIC_PARAMETER_IS_SONG_URL.getName()))) {
            var result = HttpYouTubeRequester.queryYoutubeVideo(songName);
            String thumbnailUrl = "";
            if (result.isPresent()) {
                thumbnailUrl = YouTubeRequestResultParser.getThumbnailUrlFromYouTubeUrl(result.get());
            } else {
                command.getCommandMessage().getMessage().reply("Failed to acquire video: " + songName).queue();
                return false;
            }
            processUrlPlayRequest(command.getCommandMessage().getMember(), command.getCommandMessage().getGuild(), songName, musicManager, command.getCommandMessage().getTextChannel(), thumbnailUrl, false);
        } else {
            processSearchPlayRequest(songName, musicManager, channel, command);
        }
        log.info("Processed new music request");
        return true;
    }

    private boolean isUserInVoiceChat(Command command) {
        var member = command.getCommandMessage().getMember();
        if (member != null) {
            GuildVoiceState state = member.getVoiceState();
            if (state != null) {
                AudioChannel channel = state.getChannel();
                return channel != null;
            }
        }
        return false;
    }

    private void processSearchPlayRequest(String songName, GuildMusicManager musicManager, MessageChannel channel, PlayCommand command) {
        List<YouTubeVideo> results;

        try {
            results = YouTubeSearch.performVideoQuery(songName);
        } catch (SongRequestProcessingException e) {
            log.warn("Failed to acquire songs from search for: " + songName);
            channel.sendMessage("Something went wrong with song query, please provide song url").delay(Duration.ofSeconds(30)).flatMap(Message::delete).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Search results");
        builder.setColor(Color.BLUE);
        builder.setTimestamp(LocalDateTime.now());

        for (int i = 0; i < results.size(); i++) {
            YouTubeVideo result = results.get(i);
            builder.addField(i + " " + result.title, "**Length**: " + result.length + " **Author**: " + result.author, false);
        }

        var msg = channel.sendMessageEmbeds(builder.build());
                buildButtons(results.size(), msg)
                .delay(Duration.ofSeconds(30))
                .flatMap(Message::delete)
                .queue(e -> channel
                        .sendMessage("Request timed out!")
                        .delay(Duration.ofSeconds(15))
                        .flatMap(Message::delete)
                        .flatMap(message -> command
                                .getCommandMessage()
                                .getMessage()
                                .delete()).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)), new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
        musicManager.addSearchResults(results);
    }

    private void processUrlPlayRequest(Member member, Guild guild, String songName, GuildMusicManager musicManager, TextChannel channel, String thumbnailUrl, boolean fromSearch) {
        playerManager.loadItemOrdered(musicManager, songName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                try {
                    play(member, guild, musicManager, track, channel, thumbnailUrl);
                } catch (UserNotInVoiceChannelException e) {
                    log.warn("Failed to acquire user channel");
                    channel.sendMessage("Are you in the voice channel?").delay(Duration.ofSeconds(20)).flatMap(Message::delete).queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (fromSearch){
                    try {
                        play(member, guild, musicManager, playlist.getTracks().get(0), channel, thumbnailUrl);
                        log.debug("Loaded a a song from search from a playlist, stripped other songs");
                    } catch (UserNotInVoiceChannelException e) {
                        log.warn("Failed to acquire user channel");
                        channel.sendMessage("Are you in the voice channel?").delay(Duration.ofSeconds(20)).flatMap(Message::delete).queue();
                    }
                } else {
                    String name = playlist.getName();
                    try {
                        play(member, guild, musicManager, playlist.getTracks(), name, channel);
                    } catch (UserNotInVoiceChannelException e) {
                        log.warn("Failed to acquire user channel");
                        channel.sendMessage("Are you in the voice channel?").delay(Duration.ofSeconds(20)).flatMap(Message::delete).queue();
                    }
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + songName).delay(Duration.ofSeconds(20)).flatMap(Message::delete).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).delay(Duration.ofSeconds(20)).flatMap(Message::delete).queue();
            }
        });
    }

    private boolean handleTrackSelectionButtonPress(SearchResultButtonPressedCommand command) {
        var id = command.getParameterMap().get(MusicParameterNames.MUSIC_PARAMETER_BUTTON_ID.getName());
        var button = trackButtonsIds.get(id);
        GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
        if (!button.equals("Cancel")) {
            var video = musicManager.getVideoAndClearSearchResults(Integer.parseInt(button));
            var url = "https://www.youtube.com" + video.watchUrl;
            processUrlPlayRequest(command.getCommandMessage().getMember(), command.getCommandMessage().getGuild(), url, musicManager, command.getCommandMessage().getTextChannel(), video.thumbnailUrl, true);
        }
        command.getCommandMessage().getMessage().delete().queue();
        return true;
    }

    private boolean handleEmbedPlayerButtonPress(ControlPanelButtonPressCommand command) {
        var id = command.getParameterMap().get(MusicParameterNames.MUSIC_PARAMETER_BUTTON_ID.getName());
        boolean handled = false;
        AudioManager audioManager = command.getCommandMessage().getGuild().getAudioManager();

        if ("suspend".equals(id)) {
            if (audioManager.isConnected()) {
                GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
                musicManager.scheduler.pause();
            }
            handled = true;
        }

        if ("halt".equals(id)) {
            if (audioManager.isConnected()) {
                GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
                musicManager.scheduler.stop();
            }
            handled = true;
        }

        if ("exit".equals(id)) {
            if (audioManager.isConnected()) {
                audioManager.closeAudioConnection();
            }
            GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
            musicManager.scheduler.leave();
            handled = true;
        }

        if ("shuffle".equals(id)){
            if (audioManager.isConnected()) {
                GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
                var msgChannel = command.getCommandMessage().getMessageChannel();
                musicManager.scheduler.shuffle(msgChannel);
            }
            handled = true;
        }

        if ("next".equals(id)) {
            if (audioManager.isConnected()) {
                GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
                musicManager.scheduler.nextTrack();
            }
            handled = true;
        }
        return handled;
    }

    private void play(Member member, Guild guild, GuildMusicManager musicManager, AudioTrack track, TextChannel channel, String thumbnailUrl) throws UserNotInVoiceChannelException {
        connectToVoiceChannel(member, guild.getAudioManager());

        musicManager.scheduler.enqueue(new AudioTrackRequest(track, channel, member, thumbnailUrl));
    }

    private void play(Member member, Guild guild, GuildMusicManager musicManager, List<AudioTrack> tracks, String name, TextChannel channel) throws UserNotInVoiceChannelException {
        connectToVoiceChannel(member, guild.getAudioManager());

        musicManager.scheduler.enqueue(tracks, name, channel, member);
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

    private boolean handlePauseCommand(PauseCommand command) {
        AudioManager audioManager = command.getCommandMessage().getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
            musicManager.scheduler.pause();
        }
        return true;
    }

    private boolean handleStopCommand(StopCommand command) {
        AudioManager audioManager = command.getCommandMessage().getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
            musicManager.scheduler.stop();
            var channel = command.getCommandMessage().getMessageChannel();
            channel.sendMessage("Stopped playing, queue cleared").queue();
        }
        return true;
    }

    private boolean handleSkipCommand(SkipCommand command) {
        AudioManager audioManager = command.getCommandMessage().getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
            musicManager.scheduler.nextTrack();
        }
        return true;
    }

    private boolean handleLeaveCommand(LeaveCommand command) {
        AudioManager audioManager = command.getCommandMessage().getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
        }
        GuildMusicManager musicManager = getGuildAudioPlayer(command.getCommandMessage().getGuild());
        musicManager.scheduler.leave();
        var channel = command.getCommandMessage().getMessageChannel();
        channel.sendMessage("Disconnected, cleared queue").queue();
        return true;
    }

    public MessageCreateAction buildButtons(int size, MessageCreateAction builder) {
        var entrySet = trackButtonsIds.entrySet().stream().toList();

        List<Button> buttons = new ArrayList<>();
        List<ActionRow> actionRows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            buttons.add(Button.secondary(entrySet.get(i).getKey(), entrySet.get(i).getValue()));
        }

        List<List<Button>> splitButtons = Lists.partition(buttons, 5);
        for (int i = 0; i < splitButtons.size(); i++) {
            if (splitButtons.get(i).size() < 5){
                splitButtons.get(i).add(Button.danger(entrySet.get(10).getKey(), entrySet.get(10).getValue()));
                actionRows.add(ActionRow.of(splitButtons.get(i)));
            } else {
                actionRows.add(ActionRow.of(splitButtons.get(i)));
            }
        }
        builder.setComponents(actionRows);
        return builder;
    }
}
