package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;

import java.net.Socket;

public class JoinEvent extends ChatEvent {
    private String roomId;
    private String username;
    private String password;
    private Socket socket;
    public JoinEvent(String roomId, String username, String password, Socket socket) {
        super(ChatEventType.USER_JOINED);
        this.roomId = roomId;
        this.username = username;
        this.password = password;
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
}
