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


public class Main extends JFrame {
    private final ChatEventHandler eventHandler;
    private Socket connectionSocket;
    private Socket chatSocket;
    private User user;
    private final JTextArea chatArea;
    private final JTextField messageField;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean isConnected = false;

    public Main() {
        // Initialize event handler
        eventHandler = new ChatEventHandler();
        setupEventHandlers();

        // Set up the GUI
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Message input
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        // Layout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Connection panel
        JPanel connectionPanel = new JPanel();
        JTextField serverField = new JTextField("localhost", 15);
        JTextField portField = new JTextField("8080", 5);
        JTextField usernameField = new JTextField(10);
        JTextField roomIdField = new JTextField(10);
        JButton connectButton = new JButton("Connect");

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
            try {
                connect(
                        serverField.getText(),
                        Integer.parseInt(portField.getText()),
                        usernameField.getText(),
                        roomIdField.getText()
                );
            } catch (IOException ex) {
                showError("Connection error: " + ex.getMessage());
            }
        });

        // Handle window closing
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
                connectionSocket.close();
                chatSocket = new Socket(connectionSocket.getInetAddress(), successEvent.getPort());
                outputStream = new ObjectOutputStream(chatSocket.getOutputStream());
                inputStream = new ObjectInputStream(chatSocket.getInputStream());
                JoinEvent joinEvent = new JoinEvent(
                        user.getRoomId(),
                        user.getUsername(),
                        "",  // password not implemented
                        null  // server will stablish the socket
                );
                outputStream.writeObject(joinEvent);

                isConnected = true;
                startMessageListener();

                SwingUtilities.invokeLater(() -> {
                    chatArea.append("Connected to chat room!\n");
                    messageField.setEnabled(true);
                });
            } catch (IOException e) {
                showError("Error connecting to chat socket: " + e.getMessage());
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

    private void connect(String server, int port ,String username, String roomId) throws IOException {
        this.user = new User(username, roomId);
        connectionSocket = new Socket(server, port);

        // Send new connection event
        ObjectOutputStream initialOutput = new ObjectOutputStream(connectionSocket.getOutputStream());
        NewConnectionEvent newConnectionEvent = new NewConnectionEvent(username, "");
        initialOutput.writeObject(newConnectionEvent);

        // Start listening for the connection response
        new Thread(() -> {
            try {
                ObjectInputStream initialInput = new ObjectInputStream(connectionSocket.getInputStream());
                ChatEvent event = (ChatEvent) initialInput.readObject();
                eventHandler.processEvent(event);
            } catch (IOException | ClassNotFoundException e) {
                showError("Error in connection response: " + e.getMessage());
            }
        }).start();
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                while (isConnected) {
                    ChatEvent event = (ChatEvent) inputStream.readObject();
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
            Message message = new Message(user.getRoomId(), messageText, user, MessageType.CHAT);
            MessageEvent messageEvent = new MessageEvent(message, user.getRoomId(), user.getUsername());
            outputStream.writeObject(messageEvent);
            messageField.setText("");
        } catch (IOException e) {
            showError("Error sending message: " + e.getMessage());
        }
    }

    private void disconnect() {
        isConnected = false;
        try {
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