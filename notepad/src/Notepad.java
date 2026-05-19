import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Notepad extends JFrame implements ActionListener {
    // UI Components
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu;
    private JMenuItem newItem, openItem, saveItem, exitItem;
    private JMenuItem cutItem, copyItem, pasteItem;

    // File Management
    private JFileChooser fileChooser;
    private File currentFile = null;

    public Notepad() {
        // 1. Set up the main window (JFrame)
        setTitle("Untitled - Java Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // 2. Initialize Text Area and Wrap it in a Scroll Pane
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Initialize File Chooser for Open/Save dialogs
        fileChooser = new JFileChooser();

        // 4. Create the Menu Bar
        createMenuBar();

        // 5. Make the UI visible
        setVisible(true);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // --- File Menu ---
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open...");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        // Add Listeners
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        // Add to File Menu
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // --- Edit Menu ---
        editMenu = new JMenu("Edit");
        cutItem = new JMenuItem("Cut");
        copyItem = new JMenuItem("Copy");
        pasteItem = new JMenuItem("Paste");

        // Add Listeners
        cutItem.addActionListener(this);
        copyItem.addActionListener(this);
        pasteItem.addActionListener(this);

        // Add to Edit Menu
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        // Assemble Menu Bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // --- File Operations ---
        if (source == newItem) {
            textArea.setText("");
            setTitle("Untitled - Java Notepad");
            currentFile = null;
        } else if (source == openItem) {
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                loadFile(currentFile);
            }
        } else if (source == saveItem) {
            if (currentFile == null) {
                int returnValue = fileChooser.showSaveDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    currentFile = fileChooser.getSelectedFile();
                } else {
                    return; // User cancelled save
                }
            }
            saveFile(currentFile);
        } else if (source == exitItem) {
            System.exit(0);
        }
        // --- Edit Operations ---
        else if (source == cutItem) {
            textArea.cut();
        } else if (source == copyItem) {
            textArea.copy();
        } else if (source == pasteItem) {
            textArea.paste();
        }
    }

    // Helper: Read file content into JTextArea
    private void loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textArea.read(reader, null);
            setTitle(file.getName() + " - Java Notepad");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error opening file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper: Write JTextArea content to file
    private void saveFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            textArea.write(writer);
            setTitle(file.getName() + " - Java Notepad");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Application Entry Point
    public static void main(String[] args) {
        // Run UI creation on the Event Dispatch Thread for thread-safety
        SwingUtilities.invokeLater(() -> new Notepad());
    }
}