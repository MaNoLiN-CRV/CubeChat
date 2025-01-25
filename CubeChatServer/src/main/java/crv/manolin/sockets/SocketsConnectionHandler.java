package crv.manolin.sockets;

import crv.manolin.debug.DebugCenter;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.managers.RoomManager;
import crv.manolin.managers.SessionManager;
import crv.manolin.processor.MessageProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketsConnectionHandler {
    private static final int PORT = 8888;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int SOCKET_TIMEOUT = 30000; // 30 seconds timeout
    private static final int SHUTDOWN_TIMEOUT = 10; // 10 seconds maximum wait for threads to terminate
    
    private final ExecutorService connectionThreadPool;
    private ServerSocket serverSocket;
    private volatile boolean isRunning = false;


    /////////// SETUP VARIABLES //////////

    private ChatEventHandler eventHandler;
    private MessageProcessor messageProcessor;
    private RoomManager roomManager;
    private SessionManager sessionManager;


    //////////////////////////////////////



    public SocketsConnectionHandler() {
        connectionThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * Starts the socket server with efficient configuration and connection handling
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true); 
            serverSetup(); 
            DebugCenter.log("Starting server");

            isRunning = true;

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                configureSocketParameters(clientSocket);
                connectionThreadPool.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server startup error: " + e.getMessage());
        } finally {
            stopServer();
        }
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
     * Handles a client connection after it has been accepted by the server.
     * This method logs the client's connection and manages any potential errors.
     *
     * @param clientSocket The Socket object representing the client's connection
     */
    private void handleClientConnection(Socket clientSocket) {
        try {
            DebugCenter.log("Client connected: " + clientSocket.getInetAddress());
            SocketHandler socketHandler = new SocketHandler(clientSocket, new ChatEventHandler());
        } catch (Exception e) {
            DebugCenter.error("Client connection error: " + e.getMessage());
        }
    }

    /**
     * Gracefully stops the server and releases resources
     */
    public void stopServer() {
        isRunning = false;

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
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