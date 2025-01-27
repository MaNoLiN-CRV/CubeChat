package crv.manolin.processor;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.events.MessageEvent;
import crv.manolin.managers.RoomManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessor {
    private final ExecutorService messageExecutor = Executors.newCachedThreadPool();
    private final RoomManager roomManager;

    public MessageProcessor(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public void processMessage(ChatEvent messageEvent) {
        if (messageEvent instanceof MessageEvent messageEventCasted) {
            validateMessage(messageEventCasted);
            roomManager.processMessage(messageEventCasted);
            persistMessage(messageEventCasted);
        }

    }

    private void persistMessage(MessageEvent message) {
        // TODO : Save the message into the database
    }

    private void validateMessage(MessageEvent message) {
        // TODO : Validate the message
    }
}