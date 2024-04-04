package ro.ubbcluj.map.gui.utils.events;

import ro.ubbcluj.map.gui.domain.Friendship;

public class FriendshipChangeEvent implements Event{
    private ChangeEventType type;
    private Friendship<Long> data,oldData;

    public FriendshipChangeEvent(ChangeEventType type, Friendship<Long> data, Friendship<Long> oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public FriendshipChangeEvent(ChangeEventType type, Friendship<Long> data) {
        this.type = type;
        this.data = data;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship<Long> getData() {
        return data;
    }

    public Friendship<Long> getOldData() {
        return oldData;
    }
}
