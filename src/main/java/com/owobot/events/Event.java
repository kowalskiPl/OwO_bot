package com.owobot.events;

public abstract class Event {
    protected final Observable sender;

    protected Event(Observable sender) {
        this.sender = sender;
    }

    public Observable getSender() {
        return sender;
    }

    public static String getName(){
        return Event.class.getName();
    }
}
