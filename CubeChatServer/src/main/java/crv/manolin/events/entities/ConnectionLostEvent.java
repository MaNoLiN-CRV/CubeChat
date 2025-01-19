package crv.manolin.events.entities;

import java.beans.EventHandler;
import java.net.Socket;

public class ConnectionLostEvent extends ChatEvent {
    private Socket socket;

    public ConnectionLostEvent(Socket socket) {
        super(ChatEventType.CONNECTION_LOST);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

}
