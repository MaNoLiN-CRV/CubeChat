package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;


public class NewConnectionEvent extends ChatEvent {
    private final String username;
    private final String password;
    public NewConnectionEvent(String username, String password) {
        super(ChatEventType.NEW_CONNECTION);
        this.username = username;
        this.password = password;

    }
    public String getUsername() { return username; }
}
