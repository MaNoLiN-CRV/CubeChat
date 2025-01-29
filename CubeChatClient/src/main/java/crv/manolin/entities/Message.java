package crv.manolin.entities;

import java.time.LocalDateTime;


public class Message {
    private String roomId;
    private String content;
    private User sender;
    private LocalDateTime timestamp;
    private MessageType type;
    public Message(String roomId, String content, User sender, MessageType type) {
        this.roomId = roomId;
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
