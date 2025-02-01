package crv.manolin;
import crv.manolin.entities.Message;
import crv.manolin.entities.MessageType;
import crv.manolin.entities.User;
import crv.manolin.events.ChatEventHandler;
import crv.manolin.events.entities.ChatEvent;
import crv.manolin.events.entities.ChatEventType;
import crv.manolin.events.entities.events.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;


public class Main extends JFrame {
    private final ChatEventHandler eventHandler;
    private Socket connectionSocket;
    private Socket chatSocket;
    private User user;
    private String roomId;
    private final JTextArea chatArea;
    private final JTextField messageField;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean isConnected = false;
    private String IP ;

    private ObjectOutputStream chatOutputStream = null;
    private ObjectInputStream chatInputStream = null;

    public Main() {
        // Initialize event handler
        eventHandler = new ChatEventHandler();
        setupEventHandlers();
        // Set up the GUI
        setTitle("CubeChat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        // Set up dark theme colors
        Color backgroundColor = Color.BLACK;
        Color textColor = Color.GREEN;

        // Chat area
        chatArea = new JTextArea();
        chatArea.setBackground(backgroundColor);
        chatArea.setForeground(textColor);
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.getViewport().setBackground(backgroundColor);

        // Message input
        messageField = new JTextField();
        messageField.setBackground(backgroundColor);
        messageField.setForeground(textColor);
        messageField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(backgroundColor);
        sendButton.setForeground(textColor);
        sendButton.setFont(new Font("Monospaced", Font.PLAIN, 14));
        sendButton.addActionListener(e -> sendMessage());

        // Layout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setupConnectionPanel(backgroundColor, textColor);

    }

    private void setupConnectionPanel(Color backgroundColor, Color textColor) {
        JPanel connectionPanel = new JPanel();
        connectionPanel.setBackground(backgroundColor);

        JTextField serverField = new JTextField("localhost", 15);
        serverField.setBackground(backgroundColor);
        serverField.setForeground(textColor);
        serverField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JTextField portField = new JTextField("8888", 5);
        portField.setBackground(backgroundColor);
        portField.setForeground(textColor);
        portField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JTextField usernameField = new JTextField(10);
        usernameField.setBackground(backgroundColor);
        usernameField.setForeground(textColor);
        usernameField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JTextField roomIdField = new JTextField(10);
        roomIdField.setBackground(backgroundColor);
        roomIdField.setForeground(textColor);
        roomIdField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton connectButton = new JButton("Connect");
        connectButton.setBackground(backgroundColor);
        connectButton.setForeground(textColor);
        connectButton.setFont(new Font("Monospaced", Font.PLAIN, 14));

        connectionPanel.add(new JLabel("Server:"));
        connectionPanel.add(serverField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(portField);
        connectionPanel.add(new JLabel("Username:"));
        connectionPanel.add(usernameField);
        connectionPanel.add(new JLabel("Room ID:"));
        connectionPanel.add(roomIdField);
        connectionPanel.add(connectButton);

        add(connectionPanel, BorderLayout.NORTH);
        connectButton.addActionListener(e -> {
            roomId = roomIdField.getText();
            try {
                connect(
                        serverField.getText(),
                        Integer.parseInt(portField.getText()),
                        usernameField.getText(),
                        roomIdField.getText()
                );
            } catch (IOException ex) {
                showError("Connection error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
    }

    private void setupEventHandlers() {
        eventHandler.addHandler(ChatEventType.CONNECTION_SUCCESS, event -> {
            ConnectionSuccessEvent successEvent = (ConnectionSuccessEvent) event;
            try {
                System.out.println("Processing connection success event");
                System.out.println("Chat port received: " + successEvent.getPort());

                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (connectionSocket != null) connectionSocket.close();

                System.out.println("Connecting to chat socket...");
                Thread.sleep(1000);
                chatSocket = new Socket(IP, successEvent.getPort());

                System.out.println("/////////// Chat socket connected ////////////");
                System.out.println(chatSocket.isConnected());
                System.out.println(chatSocket);
                System.out.println("//////////////////////////////////////");

                System.out.println("Creating chat streams...");

                chatOutputStream = new ObjectOutputStream(chatSocket.getOutputStream());
                chatOutputStream.flush();
                chatInputStream = new ObjectInputStream(chatSocket.getInputStream());
                System.out.println("STREAMS CREATED");



                System.out.println("Sending join event...");
                JoinEvent joinEvent = new JoinEvent(
                        roomId,
                        user.getUsername(),
                        "",
                        null
                );
                chatOutputStream.writeObject(joinEvent);
                chatOutputStream.flush();
                System.out.println("Join event sent");




                isConnected = true;
                startMessageListener();

                SwingUtilities.invokeLater(() -> {
                    chatArea.append("Connected to chat room!\n");
                    messageField.setEnabled(true);
                });
            } catch (IOException e) {
                System.err.println("Error connecting to chat: " + e.getMessage());
                e.printStackTrace();
                showError("Error connecting to chat socket: " + e.getMessage());
                disconnect();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        eventHandler.addHandler(ChatEventType.MESSAGE_RECEIVED, event -> {
            MessageEvent messageEvent = (MessageEvent) event;
            SwingUtilities.invokeLater(() -> {
                chatArea.append(messageEvent.getSenderId() + ": " +
                        messageEvent.getMessage().getContent() + "\n");
            });
        });

        eventHandler.addHandler(ChatEventType.CONNECTION_LOST, event -> {
            isConnected = false;
            SwingUtilities.invokeLater(() -> {
                chatArea.append("Connection lost!\n");
                messageField.setEnabled(false);
            });
        });
    }

    private void connect(String server, int port, String username, String roomId) throws IOException {
        System.out.println("Connecting to " + server + " on port " + port);
        this.user = new User(username);
        IP = server;
        connectionSocket = new Socket(server, port);
        outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connectionSocket.getInputStream());
        System.out.println("Sending connection event...");
        NewConnectionEvent newConnectionEvent = new NewConnectionEvent(username, "");
        outputStream.writeObject(newConnectionEvent);
        outputStream.flush();

        new Thread(() -> {
            try {
                System.out.println("Waiting for server response...");
                ChatEvent event = (ChatEvent) inputStream.readObject();
                System.out.println("Received response: " + event.getClass().getSimpleName());
                eventHandler.processEvent(event);
            } catch (IOException e) {
                showError("Connection lost: " + e.getMessage());
                disconnect();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                showError("Protocol error: " + e.getMessage());
                disconnect();
                e.printStackTrace();
            }
        }).start();
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                while (isConnected) {
                    ChatEvent event = (ChatEvent) chatInputStream.readObject();
                    eventHandler.processEvent(event);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (isConnected) {
                    eventHandler.processEvent(new ConnectionLostEvent(chatSocket));
                }
            }
        }).start();
    }

    private void sendMessage() {
        if (!isConnected) return;

        String messageText = messageField.getText().trim();
        if (messageText.isEmpty()) return;

        try {
            Message message = new Message(roomId, messageText, user, LocalDateTime.now(), MessageType.CHAT);
            MessageEvent messageEvent = new MessageEvent(message, roomId, user.getUsername());
            chatOutputStream.writeObject(messageEvent);
            chatOutputStream.flush();
            messageField.setText("");
        } catch (IOException e) {
            showError("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void disconnect() {
        isConnected = false;
        try {
            if (chatOutputStream != null){
                chatOutputStream.writeObject(new ConnectionFinishedEvent(roomId));
                chatOutputStream.flush();
            }
            if (chatOutputStream != null) chatOutputStream.close();
            if (chatInputStream != null) chatInputStream.close();
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (chatSocket != null) chatSocket.close();
            if (connectionSocket != null) connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main client = new Main();
            client.setVisible(true);
        });
    }
}