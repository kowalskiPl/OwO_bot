package com.owobot.events;

public interface Observable {
    void notifyListeners(Event event);
}
