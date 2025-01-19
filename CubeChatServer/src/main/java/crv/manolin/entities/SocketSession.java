package crv.manolin.entities;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class SocketSession {
    private String sessionId;
    private User user;
    private Socket connection;
    private Set<String> subscribedRooms;
    private AtomicLong lastHeartbeat;

    public SocketSession(String sessionId, User user, Socket connection) {
        this.sessionId = sessionId;
        this.user = user;
        this.connection = connection;
        this.subscribedRooms = new HashSet<>();
        this.lastHeartbeat = new AtomicLong(System.currentTimeMillis());
    }

    public void sendMessage(Message message) {
        // TODO: SEND THE MESSAGE
    }

    public void handleHeartbeat() {
        lastHeartbeat.set(System.currentTimeMillis());
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public Set<String> getSubscribedRooms() {
        return subscribedRooms;
    }

    public void setSubscribedRooms(Set<String> subscribedRooms) {
        this.subscribedRooms = subscribedRooms;
    }

    public AtomicLong getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(AtomicLong lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public void suscribeRoom(String room) {
        subscribedRooms.add(room);
    }

    public void unsuscribeRoom(String room) {
        subscribedRooms.remove(room);
    }
}
