import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private int chips;
    private int currentBet;
    private final List<Card> hand;

    public Player(String name, int startingChips) {
        this.name = name;
        this.chips = startingChips;
        this.hand = new ArrayList<>();
        this.currentBet = 0;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
        currentBet = 0;
    }

    public void placeBet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips!");
        }
        chips -= amount;
        currentBet += amount;
    }

    public List<Card> getHand() {
        return hand;
    }

    @Override
    public String toString() {
        return name + " (Chips: $" + chips + ") - Cards: " + hand;
    }
}