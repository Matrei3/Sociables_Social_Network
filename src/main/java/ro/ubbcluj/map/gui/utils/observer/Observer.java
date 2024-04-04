package ro.ubbcluj.map.gui.utils.observer;


import ro.ubbcluj.map.gui.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}