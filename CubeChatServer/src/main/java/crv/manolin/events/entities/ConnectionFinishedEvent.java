package crv.manolin.events.entities;

public class ConnectionFinishedEvent extends ChatEvent{
    public ConnectionFinishedEvent() {
        super(ChatEventType.USER_LEFT);
    }

}
