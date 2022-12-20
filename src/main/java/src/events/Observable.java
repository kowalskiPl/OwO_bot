package src.events;

public interface Observable {
    void addListener(Listener listener);
    void removeListener (Listener listener);
    void notifyListeners(Event event);
}
