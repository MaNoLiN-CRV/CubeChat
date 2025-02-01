package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;

public class ConnectionFinishedEvent extends ChatEvent {
    private String roomId;
    public ConnectionFinishedEvent(String roomId) {
        super(ChatEventType.USER_LEFT);
        this.roomId = roomId;
    }

}
