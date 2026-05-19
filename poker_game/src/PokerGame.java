import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PokerGame extends JFrame {
    private List<Player> players;
    private List<Card> communityCards;
    private Deck deck;
    private int pot;
    private int roundState = 0; // 0: Pre-flop, 1: Flop, 2: Turn, 3: River, 4: Showdown

    // UI Elements
    private JLabel potLabel, playerStatusLabel, computerStatusLabel, communityLabel;
    private JPanel playerHandPanel, computerHandPanel, communityPanel;
    private JButton actionButton, foldButton;

    public PokerGame() {
        // Set up Window Frame
        setTitle("Java Texas Hold'em Poker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(34, 112, 63)); // Classic felt green poker table
        setLayout(new BorderLayout(10, 10));

        initializeGameData();
        buildUI();
        startNewHand();

        setVisible(true);
    }

    private void initializeGameData() {
        players = new ArrayList<>();
        players.add(new Player("Player 1 (You)", 1000));
        players.add(new Player("Computer", 1000));
        communityCards = new ArrayList<>();
        deck = new Deck();
    }

    private void buildUI() {
        // --- Top Panel: Computer Component ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        computerStatusLabel = new JLabel("Computer: $1000", SwingConstants.CENTER);
        computerStatusLabel.setForeground(Color.WHITE);
        computerStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        computerHandPanel = new JPanel();
        computerHandPanel.setBackground(new Color(26, 82, 47));
        topPanel.add(computerStatusLabel, BorderLayout.NORTH);
        topPanel.add(computerHandPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Community Cards & Pot ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setOpaque(false);

        potLabel = new JLabel("POT: $0", SwingConstants.CENTER);
        potLabel.setFont(new Font("Arial", Font.BOLD, 24));
        potLabel.setForeground(Color.YELLOW);

        communityPanel = new JPanel();
        communityPanel.setBackground(new Color(20, 64, 36));

        centerPanel.add(potLabel);
        centerPanel.add(communityPanel);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel: User Cards & Controls ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        playerStatusLabel = new JLabel("You: $1000", SwingConstants.CENTER);
        playerStatusLabel.setForeground(Color.WHITE);
        playerStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        playerHandPanel = new JPanel();
        playerHandPanel.setBackground(new Color(26, 82, 47));

        // Interaction Buttons
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        actionButton = new JButton("Bet $50 / Deal Flop");
        foldButton = new JButton("Fold");

        actionButton.addActionListener(e -> handleGameAction());
        foldButton.addActionListener(e -> startNewHand());

        controls.add(actionButton);
        controls.add(foldButton);

        bottomPanel.add(playerStatusLabel, BorderLayout.NORTH);
        bottomPanel.add(playerHandPanel, BorderLayout.CENTER);
        bottomPanel.add(controls, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startNewHand() {
        pot = 0;
        roundState = 0;
        communityCards.clear();
        deck = new Deck();
        deck.shuffle();

        potLabel.setText("POT: $0");
        actionButton.setText("Bet $50 & Deal Flop");
        actionButton.setEnabled(true);

        // Reset hands
        for (Player p : players) {
            p.clearHand();
            p.receiveCard(deck.dealCard());
            p.receiveCard(deck.dealCard());
        }

        updateTableVisuals(false); // Hide computer's cards initially
    }

    private void handleGameAction() {
        roundState++;

        // Deduct betting chips for active play states
        if (roundState <= 3) {
            for (Player p : players)
                p.placeBet(50);
            pot += 100;
            potLabel.setText("POT: $" + pot);
        }

        switch (roundState) {
            case 1: // Deal Flop (3 cards)
                for (int i = 0; i < 3; i++)
                    communityCards.add(deck.dealCard());
                actionButton.setText("Bet $50 & Deal Turn");
                updateTableVisuals(false);
                break;
            case 2: // Deal Turn (1 card)
                communityCards.add(deck.dealCard());
                actionButton.setText("Bet $50 & Deal River");
                updateTableVisuals(false);
                break;
            case 3: // Deal River (1 card)
                communityCards.add(deck.dealCard());
                actionButton.setText("See Showdown!");
                updateTableVisuals(false);
                break;
            case 4: // Showdown Evaluation
                updateTableVisuals(true); // Flip over computer cards
                determineWinner();
                actionButton.setText("Play Next Hand");
                break;
            case 5:
                startNewHand();
                break;
        }
    }

    private void updateTableVisuals(boolean revealComputer) {
        playerStatusLabel.setText("You: $" + players.get(0).getChips());
        computerStatusLabel.setText("Computer: $" + players.get(1).getChips());

        // Render Player Cards
        playerHandPanel.removeAll();
        for (Card c : players.get(0).getHand()) {
            playerHandPanel.add(createVisualCard(c.toString(), Color.WHITE));
        }

        // Render Computer Cards
        computerHandPanel.removeAll();
        for (Card c : players.get(1).getHand()) {
            if (revealComputer) {
                computerHandPanel.add(createVisualCard(c.toString(), Color.LIGHT_GRAY));
            } else {
                computerHandPanel.add(createVisualCard("🂠 Hidden", Color.DARK_GRAY));
            }
        }

        // Render Community Cards
        communityPanel.removeAll();
        for (Card c : communityCards) {
            communityPanel.add(createVisualCard(c.toString(), Color.WHITE));
        }

        // Refresh layouts
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
        computerHandPanel.revalidate();
        computerHandPanel.repaint();
        communityPanel.revalidate();
        communityPanel.repaint();
    }

    // Helper method to dynamically generate visual cards using modern Swing labels
    private JLabel createVisualCard(String text, Color bg) {
        JLabel card = new JLabel(text, SwingConstants.CENTER);
        card.setPreferredSize(new Dimension(130, 80));
        card.setOpaque(true);
        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setFont(new Font("Arial", Font.BOLD, 12));
        return card;
    }

    private void determineWinner() {
        int score1 = players.get(0).getHand().get(0).getValue() + players.get(0).getHand().get(1).getValue();
        int score2 = players.get(1).getHand().get(0).getValue() + players.get(1).getHand().get(1).getValue();

        String message;
        if (score1 > score2) {
            message = "🎉 You win the Pot of $" + pot + "!";
        } else if (score2 > score1) {
            message = "🤖 Computer wins the Pot of $" + pot + ".";
        } else {
            message = "It's a tie! Pot split.";
        }

        JOptionPane.showMessageDialog(this, message, "Hand Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new PokerGame());
    }
}