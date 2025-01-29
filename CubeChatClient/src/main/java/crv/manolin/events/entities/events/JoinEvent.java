package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;

import java.net.Socket;

public class JoinEvent extends ChatEvent {
    private final String roomId;
    private final String username;
    private Socket socket;
    public JoinEvent(String roomId, String username, String password, Socket socket) {
        super(ChatEventType.USER_JOINED);
        this.roomId = roomId;
        this.username = username;
        this.socket = socket;
    }
    public Socket getSocket() {
        return socket;
    }
    public String getRoomId() {
        return roomId;
    }
    public String getUsername() {
        return username;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
