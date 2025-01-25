package crv.manolin.managers;

import crv.manolin.entities.ChatRoom;
import crv.manolin.entities.Message;
import crv.manolin.entities.SocketSession;
import crv.manolin.events.entities.MessageEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionManager {
    private ConcurrentHashMap<ChatRoom, ArrayList<SocketSession>> sessions = new ConcurrentHashMap<>();

    public void addSession(SocketSession session , ChatRoom room) {
        if ( sessions.containsKey(room)) {
            sessions.get(room).add(session);
        }
        else {
            sessions.put(room, new ArrayList<>());
            sessions.get(room).add(session);
        }
    }

    private ChatRoom getChatRoom(String room) {
        return null; // TODO: Implement room retrieval logic
    }

    public void removeSession(SocketSession session , ChatRoom room) {
        sessions.get(room).remove(session);
    }

    public void broadcastToRoom(MessageEvent event) {
        for (ChatRoom chatRoom : sessions.keySet()) {
            if (Objects.equals(chatRoom.getId(), event.getRoomId())){
                for (SocketSession socketSession : sessions.get(chatRoom)) {
                    socketSession.sendMessage(event.getMessage());
                }
            }
        }

    }
}
