package crv.manolin.sessions;

import crv.manolin.entities.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionManager {
    private ConcurrentHashMap<String, SocketSession> sessions = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void addSession(SocketSession session) {
        sessions.put(session.getSessionId(), session);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void broadcastToRoom(String roomId, Message message) {
        // TODO : broadcast to room
    }
}
