package src.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MainMessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
//                event.getTextChannel().getName(), event.getMember().getEffectiveName(),
//                event.getMessage().getContentDisplay());
    }
}
