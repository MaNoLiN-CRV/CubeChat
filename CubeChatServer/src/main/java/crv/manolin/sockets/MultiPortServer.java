package crv.manolin.sockets;

import crv.manolin.debug.DebugCenter;
import crv.manolin.entities.User;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.events.entities.ChatEventType;
import crv.manolin.events.entities.events.ConnectionSuccessEvent;
import crv.manolin.events.entities.events.JoinEvent;
import crv.manolin.events.entities.events.NewConnectionEvent;
import crv.manolin.managers.RoomManager;
import crv.manolin.managers.SessionManager;
import crv.manolin.processor.MessageProcessor;

import java.io.IOException;
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

    /////////// SETUP VARIABLES //////////
    private ChatEventHandler eventHandler;
    private MessageProcessor messageProcessor;
    private RoomManager roomManager;
    private SessionManager sessionManager;
    //////////////////////////////////////

    public MultiPortServer() {
        connectionThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * Starts the socket server with efficient configuration and connection handling
     */
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
                configureSocketParameters(clientSocket);
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
                roomManager.addUserToRoom(joinEvent.getRoomId(), new User(joinEvent.getUsername()), joinEvent.getSocket());

            }
        });

        this.eventHandler.addHandler(ChatEventType.NEW_CONNECTION , event -> {
            if (event instanceof NewConnectionEvent newConnectionEvent) {
                //TODO: Validate connection and send ConnectionSuccessEvent (auth service)
                SocketHandler handler = newConnectionEvent.getSocketHandler();
                handler.sendEvent(new ConnectionSuccessEvent(CHAT_PORT, roomManager.getRoomsIds(),""));
                handler.close(); // Close connection after successful login
            }
        });

    }

    private void serverSetup() {
        eventHandler = new ChatEventHandler();
        sessionManager = new SessionManager();
        roomManager = RoomManager.getInstance(sessionManager);
        messageProcessor = new MessageProcessor(roomManager);
    }

    /**
     * Configures individual socket with performance and timeout settings
     */
    private void configureSocketParameters(Socket socket) throws IOException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        socket.setKeepAlive(true);
    }

    /**
     * Handles a new connection on the connection port (asynchronous)
     */
    private void handleNewConnection(Socket clientSocket) {
        try {
            DebugCenter.log("NEW CONNECTION from: " + clientSocket.getInetAddress());
            SocketHandler socketHandler = new SocketHandler(clientSocket, this.eventHandler);
            socketHandler.start(); // Starts socket handler

        } catch (Exception e) {
            DebugCenter.error("New connection handling error: " + e.getMessage());
        }
    }

    /**
     * Handles a new connection on the chat port
     */
    private void handleChatConnection(Socket clientSocket) {
        try {
            DebugCenter.log("New chat connection from: " + clientSocket.getInetAddress());
            SocketHandler socketHandler = new SocketHandler(clientSocket, this.eventHandler);
            socketHandler.start();
        } catch (Exception e) {
            DebugCenter.error("Chat connection handling error: " + e.getMessage());
        }
    }

    /**
     * Gracefully stops the server and releases resources
     */
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