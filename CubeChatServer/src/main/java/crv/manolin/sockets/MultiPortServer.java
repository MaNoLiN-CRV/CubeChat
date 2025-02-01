package crv.manolin.sockets;

import crv.manolin.debug.DebugCenter;
import crv.manolin.entities.User;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.events.entities.ChatEventType;
import crv.manolin.events.entities.events.*;
import crv.manolin.managers.RoomManager;
import crv.manolin.managers.SessionManager;
import crv.manolin.processor.MessageProcessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiPortServer {
    private static final int CONNECTION_PORT = 8888;
    private static final int CHAT_PORT = 8889;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int SOCKET_TIMEOUT = 30000;
    private static final int SHUTDOWN_TIMEOUT = 10;

    private final ExecutorService connectionThreadPool;
    private ServerSocket connectionSocket;
    private ServerSocket chatSocket;
    private volatile boolean isRunning = false;

    private ChatEventHandler eventHandler;
    private MessageProcessor messageProcessor;
    private RoomManager roomManager;
    private SessionManager sessionManager;

    public MultiPortServer() {
        connectionThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void startServer() {
        try {
            serverSetup();
            eventsSetup();
            connectionSocket = new ServerSocket(CONNECTION_PORT);
            chatSocket = new ServerSocket(CHAT_PORT);

            connectionSocket.setReuseAddress(true);
            chatSocket.setReuseAddress(true);

            isRunning = true;

            Thread connectionThread = new Thread(this::handleConnectionPort);
            Thread chatThread = new Thread(this::handleChatPort);

            connectionThread.start();
            chatThread.start();

            DebugCenter.log("Server started on ports " + CONNECTION_PORT + " (connections) and " + CHAT_PORT + " (chat)");

        } catch (IOException e) {
            DebugCenter.error("Server startup error: " + e.getMessage());
            stopServer();
        }
    }

    private void handleConnectionPort() {
        while (isRunning) {
            try {
                Socket clientSocket = connectionSocket.accept();
                configureSocketParameters(clientSocket);
                connectionThreadPool.submit(() -> handleNewConnection(clientSocket));
            } catch (IOException e) {
                if (isRunning) {
                    DebugCenter.error("Connection port error: " + e.getMessage());
                }
            }
        }
    }

    private void handleChatPort() {
        while (isRunning) {
            try {
                Socket clientSocket = chatSocket.accept();
                // configureSocketParameters(clientSocket);
                connectionThreadPool.submit(() -> handleChatConnection(clientSocket));
            } catch (IOException e) {
                if (isRunning) {
                    DebugCenter.error("Chat port error: " + e.getMessage());
                }
            }
        }
    }

    private void eventsSetup() {
        this.eventHandler.addHandler(ChatEventType.MESSAGE_RECEIVED,
                event -> this.messageProcessor.processMessage(event));

        this.eventHandler.addHandler(ChatEventType.USER_JOINED, event -> {
            if (event instanceof JoinEvent joinEvent) {
                SocketHandler socketHandler = joinEvent.getFirstPropOfType(SocketHandler.class);
                if (socketHandler == null) return;

                roomManager.addUserToRoom(
                        joinEvent.getRoomId(),
                        new User(joinEvent.getUsername()),
                        joinEvent.getSocket(),
                        socketHandler
                );
            }
        });
    }

    private void serverSetup() {
        eventHandler = new ChatEventHandler();
        sessionManager = new SessionManager();
        roomManager = RoomManager.getInstance(sessionManager);
        messageProcessor = new MessageProcessor(roomManager);
    }

    private void configureSocketParameters(Socket socket) throws IOException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        socket.setKeepAlive(true);
    }

    private void handleNewConnection(Socket clientSocket) {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;

        try {
            DebugCenter.log("NEW CONNECTION from: " + clientSocket.getInetAddress());
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();

            DebugCenter.log("Streams established, waiting for connection event...");
            Object received = input.readObject();

            if (received instanceof NewConnectionEvent newConnectionEvent) {
                DebugCenter.log("Received NewConnectionEvent");
                newConnectionEvent.setSocket(clientSocket);

                ConnectionSuccessEvent successEvent = new ConnectionSuccessEvent(
                        CHAT_PORT,
                        roomManager.getRoomsIds(),
                        ""
                );

                output.writeObject(successEvent);
                output.flush();
                DebugCenter.log("Sent connection success response");
            } else {
                DebugCenter.error("Unexpected event type: " + received.getClass().getName());
            }
        } catch (Exception e) {
            DebugCenter.error("New connection handling error: " + e.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null) input.close();
                clientSocket.close();
                DebugCenter.log("Connection resources closed");
            } catch (IOException e) {
                DebugCenter.error("Error closing connection resources: " + e.getMessage());
            }
        }
    }

    private void handleChatConnection(Socket clientSocket) {
        try {
            SocketHandler socketHandler = new SocketHandler(clientSocket, this.eventHandler);
            socketHandler.start();
        } catch (Exception e) {
            DebugCenter.error("Chat connection handling error: " + e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException ex) {
                DebugCenter.error("Error closing chat socket: " + ex.getMessage());
            }
        }
    }

    public void stopServer() {
        isRunning = false;

        try {
            if (connectionSocket != null) connectionSocket.close();
            if (chatSocket != null) chatSocket.close();

            connectionThreadPool.shutdown();
            if (!connectionThreadPool.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                connectionThreadPool.shutdownNow();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Server shutdown error: " + e.getMessage());
            connectionThreadPool.shutdownNow();
        }
    }
}