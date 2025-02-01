package crv.manolin.managers;

import crv.manolin.entities.ChatRoom;
import crv.manolin.entities.SocketSession;
import crv.manolin.entities.User;
import crv.manolin.events.entities.events.MessageEvent;
import crv.manolin.sockets.SocketHandler;

import java.net.Socket;
import java.util.ArrayList;
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

    public ArrayList<String> getRoomsIds() {
        return new ArrayList<>(rooms.keySet());
    }

    public void addUserToRoom(String roomId, User user , Socket socket , SocketHandler socketHandler) {
        ChatRoom room = rooms.get(roomId);
        if (room == null) {
            rooms.put(roomId, room = new ChatRoom(roomId));
        }
        room.getParticipants().add(user);
        sessionManager.addSession(new SocketSession(user.getId(), user, socket , socketHandler) , room);
        // TODO: Notify other users
    }

    public void processMessage(MessageEvent message) {
        ChatRoom room = rooms.get(message.getRoomId());
        room.getMessageBuffer().offer(message.getMessage());
        sessionManager.broadcastToRoom(message);
    }
}