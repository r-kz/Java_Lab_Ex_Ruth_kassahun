import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Notepad extends JFrame implements ActionListener {
    // UI Components
    private JTextPane textPane; // Upgraded from JTextArea for rich text formatting
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu;
    private JMenuItem newItem, openItem, saveItem, exitItem;
    private JMenuItem cutItem, copyItem, pasteItem;

    // Formatting Toolbar Components
    private JToolBar toolBar;
    private JButton boldButton, italicButton;
    private JComboBox<String> sizeComboBox;

    // File Management
    private JFileChooser fileChooser;
    private File currentFile = null;

    public Notepad() {
        // 1. Set up the main window (JFrame)
        setTitle("Untitled - Java Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // 2. Initialize Text Pane (Allows rich formatting)
        textPane = new JTextPane();
        textPane.setFont(new Font("Consolas", Font.PLAIN, 16));

        scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Initialize File Chooser
        fileChooser = new JFileChooser();

        // 4. Create Menu Bar and Formatting Toolbar
        createMenuBar();
        createToolBar();

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

        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

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

        cutItem.addActionListener(this);
        copyItem.addActionListener(this);
        pasteItem.addActionListener(this);

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false); // Keeps the toolbar locked in place

        // Bold Button
        boldButton = new JButton("B");
        boldButton.setFont(new Font("Arial", Font.BOLD, 14));
        boldButton.setToolTipText("Bold Selection");
        boldButton.addActionListener(e -> toggleBold());

        // Italic Button
        italicButton = new JButton("I");
        italicButton.setFont(new Font("Arial", Font.ITALIC, 14));
        italicButton.setToolTipText("Italic Selection");
        italicButton.addActionListener(e -> toggleItalic());

        // Font Size Selector
        JLabel sizeLabel = new JLabel(" Size: ");
        String[] sizes = { "12", "14", "16", "18", "20", "24", "28", "32" };
        sizeComboBox = new JComboBox<>(sizes);
        sizeComboBox.setSelectedItem("16"); // Match initial look
        sizeComboBox.setMaximumSize(new Dimension(60, 25));
        sizeComboBox.addActionListener(e -> changeFontSize());

        // Assembly
        toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.addSeparator();
        toolBar.add(sizeLabel);
        toolBar.add(sizeComboBox);

        // Place toolbar at the top edge of the window
        add(toolBar, BorderLayout.NORTH);
    }

    // --- Formatting Logic Engine ---

    private void toggleBold() {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start == end)
            return; // Nothing selected

        // Read attributes of the first selected character to determine state
        AttributeSet attr = doc.getCharacterElement(start).getAttributes();
        boolean isBold = StyleConstants.isBold(attr);

        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setBold(sas, !isBold);
        doc.setCharacterAttributes(start, end - start, sas, false);
    }

    private void toggleItalic() {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start == end)
            return;

        AttributeSet attr = doc.getCharacterElement(start).getAttributes();
        boolean isItalic = StyleConstants.isItalic(attr);

        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.set脅talic(sas, !isItalic); // Toggles state cleanly
        StyleConstants.setItalic(sas, !isItalic);
        doc.setCharacterAttributes(start, end - start, sas, false);
    }

    private void changeFontSize() {
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        if (selectedSize == null)
            return;

        int size = Integer.parseInt(selectedSize);
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setFontSize(sas, size);

        if (start != end) {
            // Apply to highlight block
            doc.setCharacterAttributes(start, end - start, sas, false);
        } else {
            // Apply to typing cursor position going forward
            textPane.setCharacterAttributes(sas, false);
        }
    }

    // --- Action Routing ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == newItem) {
            textPane.setText("");
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
                    return;
                }
            }
            saveFile(currentFile);
        } else if (source == exitItem) {
            System.exit(0);
        } else if (source == cutItem) {
            textPane.cut();
        } else if (source == copyItem) {
            textPane.copy();
        } else if (source == pasteItem) {
            textPane.paste();
        }
    }

    private void loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textPane.read(reader, null);
            setTitle(file.getName() + " - Java Notepad");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error opening file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            textPane.write(writer);
            setTitle(file.getName() + " - Java Notepad");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Notepad());
    }
}