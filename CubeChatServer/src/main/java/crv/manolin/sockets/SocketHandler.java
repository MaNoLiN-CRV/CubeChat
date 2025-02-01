package crv.manolin.sockets;

import crv.manolin.debug.DebugCenter;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.events.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SocketHandler extends Thread {
    private final Socket clientSocket;
    private final ChatEventHandler eventHandler;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;
    private boolean running;

    public SocketHandler(Socket socket, ChatEventHandler handler) throws IOException {
        this.clientSocket = socket;
        this.eventHandler = handler;
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        this.running = true;
    }

    @Override
    public void run() {
        try {
            DebugCenter.log("Handler started for: " + clientSocket.getInetAddress());
            while (running) {
                Object received = input.readObject();
                if (received instanceof ChatEvent event) {
                    if (event instanceof JoinEvent joinEvent) {
                        joinEvent.setSocket(clientSocket);
                        joinEvent.addProp(this);
                    }
                    DebugCenter.log("Received event: " + event.toString());
                    eventHandler.processEvent(event);

                    if (event instanceof ConnectionFinishedEvent) {
                        running = false;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            DebugCenter.error("Error during event handling: " + e.getMessage());
            eventHandler.processEvent(new ConnectionLostEvent(clientSocket));
            e.printStackTrace();
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