package crv.manolin.events;

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

    public void processEvent(ChatEvent event) {
        eventExecutor.submit(() -> {
            Set<EventHandlerCallback> eventHandlers = handlers.get(event.getType());
            if (eventHandlers != null) {
                eventHandlers.forEach(handler -> {
                    try {
                        eventExecutor.submit(() -> handler.handleEvent(event));
                    } catch (Exception e) {
                        // TODO : CUSTOM EXCEPTION
                    }
                });
            }
        });
    }
}