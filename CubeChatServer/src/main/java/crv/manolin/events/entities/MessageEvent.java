package crv.manolin.events.entities;

import crv.manolin.entities.Message;

public class MessageEvent extends ChatEvent {
    private final Message message;
    private final String roomId;
    private final String senderId;

    public MessageEvent(Message message, String roomId, String senderId) {
        super(ChatEventType.MESSAGE_RECEIVED);
        this.message = message;
        this.roomId = roomId;
        this.senderId = senderId;
    }

    public Message getMessage() {
        return message;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getSenderId() {
        return senderId;
    }
}
