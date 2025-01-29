package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;
import java.net.Socket;

public class NewConnectionEvent extends ChatEvent {
    private final String username;
    private final String password;
    private Socket socket;

    public NewConnectionEvent(String username, String password) {
        super(ChatEventType.NEW_CONNECTION);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}