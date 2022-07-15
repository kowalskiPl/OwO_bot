package src.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.music.GuildMusicManager;

import java.util.HashMap;
import java.util.Map;

public class MusicListenerAdapter extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MusicListenerAdapter.class);
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> guildMusicManagers;

    public MusicListenerAdapter() {
        guildMusicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
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

        playerManager.loadItemOrdered(musicManager, songName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(event, musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue: " + firstTrack.getInfo().title + ", playlist size: " + playlist.getTracks().size()).queue();

                play(event, musicManager, firstTrack);
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

    private void play(MessageReceivedEvent event, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(event, event.getGuild().getAudioManager());

        musicManager.scheduler.enqueue(track);
    }

    private static void connectToVoiceChannel(MessageReceivedEvent event, AudioManager audioManager) {
        Member member = event.getMember();
        if (member != null) {
            GuildVoiceState state = member.getVoiceState();
            if (state != null) {
                AudioChannel channel = state.getChannel();
                if (!audioManager.isConnected()){
                    audioManager.openAudioConnection(channel);
                }
            }
        } else {
            log.warn("Failed to acquire user channel");
            event.getChannel().sendMessage("Are you int the voice channel?").queue();
        }
    }

    private void handlePauseCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()){
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.pause();
        }
    }

    private void handleStopCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()){
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.stop();
            var channel = event.getChannel();
            channel.sendMessage("Stopped playing, queue cleared").queue();
        }
    }

    private void handleSkipCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()){
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.nextTrack();
            var channel = event.getChannel();
            channel.sendMessage("Now playing: " + musicManager.scheduler.getCurrentTrack().getInfo().title).queue();
        }
    }

    private void handleDisconnectCommand(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()){
            audioManager.closeAudioConnection();
        }
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.stop();
        var channel = event.getChannel();
        channel.sendMessage("Disconnected, cleared queue").queue();
    }
}
