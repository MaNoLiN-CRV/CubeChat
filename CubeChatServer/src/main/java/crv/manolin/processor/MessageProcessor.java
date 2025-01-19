package crv.manolin.processor;

import crv.manolin.entities.Message;
import crv.manolin.managers.RoomManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessor {
    private final ExecutorService messageExecutor = Executors.newCachedThreadPool();
    private final RoomManager roomManager;

    public MessageProcessor(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public void processMessage(Message message, String roomId) {
        messageExecutor.submit(() -> {
            validateMessage(message);
            roomManager.processMessage(roomId, message);
            persistMessage(message, roomId);
        });
    }

    private void persistMessage(Message message, String roomId) {
        // TODO : Save the message into the database
    }

    private void validateMessage(Message message) {
        // TODO : Validate the message
    }
}