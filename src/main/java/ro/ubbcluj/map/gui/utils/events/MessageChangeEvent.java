package ro.ubbcluj.map.gui.utils.events;

import ro.ubbcluj.map.gui.domain.Friendship;
import ro.ubbcluj.map.gui.domain.Message;

public class MessageChangeEvent implements Event{
    private ChangeEventType type;
    private Message data,oldData;

    public MessageChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public MessageChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
