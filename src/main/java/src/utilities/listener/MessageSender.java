package src.utilities.listener;

import net.dv8tion.jda.api.entities.Message;
import src.events.Event;
import src.events.Listener;
import src.events.SendMessageEvent;

import java.time.Duration;

public class MessageSender implements Listener {
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
    }
}
