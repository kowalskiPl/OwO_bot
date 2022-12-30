package src.utilities.listener;

import src.events.Event;
import src.events.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EventListenerStack {
    private final ExecutorService threadPool;
    private final List<Listener> eventListeners;
    private final Lock lock;

    public EventListenerStack(int poolSize) {
        threadPool = Executors.newFixedThreadPool(poolSize);
        eventListeners = new ArrayList<>();
        lock = new ReentrantLock();
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
        Runnable task = () -> eventListeners.forEach(listener -> listener.onEventReceived(event));
        lock.lock();
        threadPool.submit(task);
        lock.unlock();
    }
}
