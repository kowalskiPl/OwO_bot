package com.owobot.messagelisteners;

import com.owobot.utilities.ServiceContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.owobot.events.Event;
import com.owobot.events.Observable;
import com.owobot.events.SendMessageEvent;

public class MainMessageListener extends ListenerAdapter implements Observable {
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

//        String[] arr = content.split(" ", 2);
//        var channel = event.getChannel();
//        if (arr[0].equals("!test")) {
//
//        }
        var myEvent = new SendMessageEvent(message.getChannel().asTextChannel(), "This is a test of event stack", this);
//        notifyListeners(myEvent);
    }

    @Override
    public void notifyListeners(Event event) {
        ServiceContext.getListenerStack().onEvent(event);
    }
}
