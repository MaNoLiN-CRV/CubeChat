package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;

public class ConnectionFinishedEvent extends ChatEvent {
    public ConnectionFinishedEvent() {
        super(ChatEventType.USER_LEFT);
    }

}
