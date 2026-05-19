import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private BufferedReader in;
    private PrintWriter out;
    private JTextArea messageArea;
    private JTextField textField;
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

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.addActionListener(e -> sendMessage());
        add(textField, BorderLayout.SOUTH);

        setVisible(true);
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Connect to local computer running the ChatServer on port 12345
            Socket socket = new Socket("127.0.0.1", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Start a separate background thread to constantly listen for incoming
            // broadcast messages
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
            JOptionPane.showMessageDialog(this, "Could not connect to server. Ensure ChatServer is running!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
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