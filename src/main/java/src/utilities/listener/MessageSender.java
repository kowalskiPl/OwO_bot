package src.utilities.listener;

import src.events.Event;
import src.events.Listener;
import src.events.SendMessageEvent;

public class MessageSender implements Listener {
    @Override
    public void onEventReceived(Event event) {
        if (event instanceof SendMessageEvent){
            ((SendMessageEvent) event).getMessageChannel().sendMessage(((SendMessageEvent) event).getText()).queue();
        }
    }
}
