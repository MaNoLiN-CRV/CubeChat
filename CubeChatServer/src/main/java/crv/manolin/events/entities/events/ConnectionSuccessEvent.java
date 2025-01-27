package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;

import java.util.ArrayList;

public class ConnectionSuccessEvent extends ChatEvent {
    private int port;
    private ArrayList<String> messages;
    public ConnectionSuccessEvent( int port, ArrayList<String> messages) {
        super(ChatEventType.CONNECTION_SUCCESS);
        this.port = port;
        this.messages = messages;
    }
    public int getPort() {
        return port;
    }
    public ArrayList<String> getMessages() {
        return messages;
    }
}
