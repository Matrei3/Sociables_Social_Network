package ro.ubbcluj.map.gui.utils.events;

import ro.ubbcluj.map.gui.domain.PendingRequest;

public class PendingRequestChangeEvent implements Event{
    private final ChangeEventType type;
    private final PendingRequest data;
    private PendingRequest oldData;

    public PendingRequestChangeEvent(ChangeEventType type, PendingRequest data, PendingRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public PendingRequestChangeEvent(ChangeEventType type, PendingRequest data) {
        this.type = type;
        this.data = data;
    }

    public ChangeEventType getType() {
        return type;
    }

    public PendingRequest getData() {
        return data;
    }

    public PendingRequest getOldData() {
        return oldData;
    }
}
