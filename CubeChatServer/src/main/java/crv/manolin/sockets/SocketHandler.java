package crv.manolin.sockets;

import crv.manolin.debug.DebugCenter;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.events.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketHandler extends Thread {
    private final Socket clientSocket;
    private final ChatEventHandler eventHandler;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private boolean running;

    public SocketHandler(Socket socket, ChatEventHandler handler) throws IOException {
        this.clientSocket = socket;
        this.eventHandler = handler;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Object received = input.readObject();
                if (received instanceof ChatEvent event) {
                    if (event instanceof NewConnectionEvent) {
                        ((NewConnectionEvent) event).setSocket(clientSocket);
                    }
                    eventHandler.processEvent(event);

                    if (event instanceof ConnectionFinishedEvent) {
                        running = false;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            eventHandler.processEvent(new ConnectionLostEvent(clientSocket));
        } finally {
            cleanup();
        }
    }

    public void sendEvent(ChatEvent event) {
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            DebugCenter.error("Error sending event: " + e.getMessage());
        }
    }

    public void close() {
        running = false;
        cleanup();
    }

    private void cleanup() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            DebugCenter.error("Error during cleanup: " + e.getMessage());
        }
    }
}