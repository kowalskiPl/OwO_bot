package com.owobot.utilities.listener;

import com.owobot.events.Event;
import com.owobot.events.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventListenerStack {
    private final ExecutorService threadPool;
    private final List<Listener> eventListeners;

    public EventListenerStack(int poolSize) {
        threadPool = Executors.newFixedThreadPool(poolSize);
        eventListeners = new ArrayList<>();
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public void registerListener(Listener listener) {
        eventListeners.add(listener);
    }

    public void registerListener(Listener... listeners) {
        eventListeners.addAll(List.of(listeners));
    }

    public void onEvent(Event event) {
        Runnable task = () -> {
            for (Listener listener : eventListeners) {
                var result = listener.onEventReceived(event);
                if (result){
                    break;
                }
            }
        };
        threadPool.submit(task);
    }
}
