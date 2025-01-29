package crv.manolin.events.entities.events;

import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;

import java.util.ArrayList;

public class ConnectionSuccessEvent extends ChatEvent {
    private final int port;
    private final String jwt;
    private final ArrayList<String> roomsIds;
    public ConnectionSuccessEvent(int port, ArrayList<String> roomsIds, String jwt) {
        super(ChatEventType.CONNECTION_SUCCESS);
        this.port = port;
        this.roomsIds = roomsIds;
        this.jwt = jwt;
    }
    public int getPort() {
        return port;
    }
    public ArrayList<String> getRoomsIds() {
        return roomsIds;
    }
}
