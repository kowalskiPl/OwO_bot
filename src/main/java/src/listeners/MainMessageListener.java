package src.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.model.YouTubeVideo;
import src.youtube.YouTubeSearch;

import java.awt.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MainMessageListener extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(MainMessageListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
//                event.getTextChannel().getName(), event.getMember().getEffectiveName(),
//                event.getMessage().getContentDisplay());
        Message message = event.getMessage();
        User author = message.getAuthor();
        String content = message.getContentRaw();
        Guild guild = event.getGuild();

        if (author.isBot())
            return;

        if (!event.isFromGuild())
            return;

        String[] arr = content.split(" ", 2);
        var channel = event.getChannel();
        if (arr[0].equals("!test")) {

        }
    }

}
