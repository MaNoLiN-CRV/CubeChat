package crv.manolin.events.entities;


import java.time.LocalDateTime;

public abstract class ChatEvent {
    private final ChatEventType type;
    private final LocalDateTime timestamp;

    protected ChatEvent(ChatEventType type) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public ChatEventType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
