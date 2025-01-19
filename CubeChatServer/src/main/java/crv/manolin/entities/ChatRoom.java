package crv.manolin.entities;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatRoom {
    private String id;
    private String name;
    private Set<User> participants;
    private LinkedBlockingQueue<Message> messageBuffer;

    public ChatRoom(String id, String name) {
        this.id = id;
        this.name = name;
        this.participants = new LinkedHashSet<>();
        this.messageBuffer = new LinkedBlockingQueue<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public LinkedBlockingQueue<Message> getMessageBuffer() {
        return messageBuffer;
    }

    public void setMessageBuffer(LinkedBlockingQueue<Message> messageBuffer) {
        this.messageBuffer = messageBuffer;
    }
    public void addToBuffer(Message message) {
        messageBuffer.add(message);
    }
    public boolean removeFromBuffer(Message message) {
        return messageBuffer.remove(message);
    }
    public void addParticipant(User user) {
        participants.add(user);
    }
    public void removeParticipant(User user) {
        participants.remove(user);
    }
    public boolean isParticipant(User user) {
        return participants.contains(user);
    }


}
