package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;
import crv.manolin.sockets.SocketHandler;

import java.net.Socket;

public class NewConnectionEvent extends ChatEvent {
    private final String username;
    private final String password;
    private final SocketHandler handler;
    public NewConnectionEvent(String username, String password , SocketHandler handler) {
        super(ChatEventType.NEW_CONNECTION);
        this.username = username;
        this.password = password;
        this.handler = handler;
    }
    public SocketHandler getSocketHandler() { return handler; }
    public String getUsername() { return username; }
}
