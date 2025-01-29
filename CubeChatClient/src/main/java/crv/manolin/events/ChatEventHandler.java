package crv.manolin.events;


import crv.manolin.debug.DebugCenter;
import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;
import crv.manolin.events.entities.EventHandlerCallback;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatEventHandler {
    private final ConcurrentHashMap<ChatEventType, Set<EventHandlerCallback>> handlers;
    private final ExecutorService eventExecutor;

    public ChatEventHandler() {
        this.handlers = new ConcurrentHashMap<>();
        this.eventExecutor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
    }

    public void addHandler(ChatEventType type, EventHandlerCallback handler) {
        handlers.computeIfAbsent(type, k -> ConcurrentHashMap.newKeySet())
                .add(handler);
    }
    /**
     * Processes a ChatEvent by submitting it to the event executor.
     * This method retrieves the appropriate event handlers for the event type
     * and submits each handler to the executor to handle the event asynchronously.
     *
     * @param event The ChatEvent to be processed. This event contains information
     *              about the type of chat event and any associated data.
     */
    public void processEvent(ChatEvent event) {
        eventExecutor.submit(() -> {
            Set<EventHandlerCallback> eventHandlers = handlers.get(event.getType());
            if (eventHandlers != null) {
                eventHandlers.forEach(handler -> {
                    try {
                        eventExecutor.submit(() -> handler.handleEvent(event));
                    } catch (Exception e) {
                        DebugCenter.error(e.getMessage());
                    }
                });
            }
        });
    }
}