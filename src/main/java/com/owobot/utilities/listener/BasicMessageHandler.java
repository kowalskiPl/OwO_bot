package com.owobot.utilities.listener;

import com.owobot.events.DeleteMessageEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import com.owobot.events.Event;
import com.owobot.events.Listener;
import com.owobot.events.SendMessageEvent;

import java.time.Duration;

public class BasicMessageHandler implements Listener {
    @Override
    public boolean onEventReceived(Event event) {
        if (event instanceof SendMessageEvent messageEvent) {
            if (messageEvent.getDelay() > 0) {
                messageEvent.getMessageChannel().sendMessage(((SendMessageEvent) event).getText())
                        .delay(Duration.ofSeconds(messageEvent.getDelay()))
                        .flatMap(Message::delete)
                        .queue();
            } else {
                messageEvent.getMessageChannel().sendMessage(((SendMessageEvent) event).getText()).queue();
            }
            return true;
        }

        if (event instanceof DeleteMessageEvent messageEvent) {
            if (messageEvent.getPreviousMessages() > 0) {
                //TODO: some serious stuff here to delete n messages backwards
                return false;
            } else {
                messageEvent.getMessageChannel().deleteMessageById(messageEvent.getMessageID()).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
                return true;
            }
        }
        return false;
    }
}
