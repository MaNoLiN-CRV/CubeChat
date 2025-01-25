package crv.manolin.processor;

import crv.manolin.entities.Message;
import crv.manolin.events.entities.MessageEvent;
import crv.manolin.managers.RoomManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessor {
    private final ExecutorService messageExecutor = Executors.newCachedThreadPool();
    private final RoomManager roomManager;

    public MessageProcessor(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public void processMessage(MessageEvent messageEvent) {
        validateMessage(messageEvent);
        roomManager.processMessage(messageEvent);
        persistMessage(messageEvent);
    }

    private void persistMessage(MessageEvent message) {
        // TODO : Save the message into the database
    }

    private void validateMessage(MessageEvent message) {
        // TODO : Validate the message
    }
}