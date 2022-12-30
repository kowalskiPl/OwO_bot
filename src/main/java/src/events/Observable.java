package src.events;

public interface Observable {
    void notifyListeners(Event event);
}
