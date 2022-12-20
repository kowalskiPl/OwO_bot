package src.utilities.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import src.events.DeleteMessageEvent;
import src.events.Event;
import src.events.Listener;
import src.events.SendMessageEvent;

import java.time.Duration;

public class BasicMessageHandler implements Listener {
    @Override
    public void onEventReceived(Event event) {
        if (event instanceof SendMessageEvent messageEvent) {
            if (messageEvent.getDelay() > 0) {
                messageEvent.getMessageChannel().sendMessage(((SendMessageEvent) event).getText())
                        .delay(Duration.ofSeconds(messageEvent.getDelay()))
                        .flatMap(Message::delete)
                        .queue();
            } else
                messageEvent.getMessageChannel().sendMessage(((SendMessageEvent) event).getText()).queue();
        }

        if (event instanceof DeleteMessageEvent messageEvent) {
            if (messageEvent.getPreviousMessages() > 0) {
                //TODO: some serious stuff here to delete n messages backwards
            } else {
                messageEvent.getMessageChannel().deleteMessageById(messageEvent.getMessageID()).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            }
        }
    }
}
