package crv.manolin.managers;

import crv.manolin.entities.ChatRoom;
import crv.manolin.entities.Message;
import crv.manolin.entities.User;

import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    private static RoomManager instance;
    private final ConcurrentHashMap<String, ChatRoom> rooms = new ConcurrentHashMap<>();
    private final SessionManager sessionManager;

    private RoomManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public static RoomManager getInstance(SessionManager sessionManager) {
        if (instance == null) {
            synchronized (RoomManager.class) {
                if (instance == null) {
                    instance = new RoomManager(sessionManager);
                }
            }
        }
        return instance;
    }

    public void createRoom(ChatRoom room) {
        rooms.put(room.getId(), room);
    }

    public void addUserToRoom(String roomId, User user) {
        ChatRoom room = rooms.get(roomId);
        room.getParticipants().add(user);
        // TODO: Notify other users
    }

    public void processMessage(String roomId, Message message) {
        ChatRoom room = rooms.get(roomId);
        room.getMessageBuffer().offer(message);
        sessionManager.broadcastToRoom(roomId, message);
    }
}