package ro.ubbcluj.map.gui.utils.observer;


import ro.ubbcluj.map.gui.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
