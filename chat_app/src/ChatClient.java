import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private BufferedReader in;
    private PrintWriter out;
    private JTextArea messageArea;
    private JTextField textField;
    private JButton sendButton; // <-- Added the button variable
    private String clientName;

    public ChatClient() {
        // Request a screen name from the user
        clientName = JOptionPane.showInputDialog(
                this, "Choose a screen name:", "Screen Name Selection", JOptionPane.PLAIN_MESSAGE);

        if (clientName == null || clientName.trim().isEmpty()) {
            clientName = "Anonymous_" + (int) (Math.random() * 1000);
        }

        // Build the Frame Layout
        setTitle("Chat Room - " + clientName);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. The Main Chat Display Area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // 2. Creating the Bottom Input Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5)); // 5px gaps between components
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding around edges

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.addActionListener(e -> sendMessage()); // Still works when you press 'Enter'
        bottomPanel.add(textField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.addActionListener(e -> sendMessage()); // Works when you click the button!
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Add the combined panel to the bottom of the window
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 12345);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                messageArea.append("Connected to chat server successfully!\n");

                new Thread(() -> {
                    try {
                        String line;
                        while ((line = in.readLine()) != null) {
                            messageArea.append(line + "\n");
                        }
                    } catch (IOException e) {
                        messageArea.append("Connection to server lost.\n");
                    }
                }).start();

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Could not connect to server.\n\nMake sure you run 'ChatServer' FIRST before opening the client!",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                });
            }
        }).start();
    }

    private void sendMessage() {
        String text = textField.getText().trim();
        if (!text.isEmpty()) {
            out.println(clientName + ": " + text);
            textField.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new ChatClient());
    }
}