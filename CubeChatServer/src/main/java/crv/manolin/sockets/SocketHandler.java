package crv.manolin.sockets;

import crv.manolin.entities.Message;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.events.entities.ConnectionLostEvent;
import crv.manolin.events.entities.MessageEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private final Socket clientSocket;
    private final ChatEventHandler eventHandler;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private boolean running;

    public SocketHandler(Socket socket, ChatEventHandler handler ) throws IOException {
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
                if (received instanceof Message message) {
                    MessageEvent event = new MessageEvent(
                            message,
                            message.getRoomId(),
                            message.getSender().getId()
                    );
                    eventHandler.processEvent(event);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            eventHandler.processEvent(new ConnectionLostEvent(clientSocket));
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {

        }
    }
}