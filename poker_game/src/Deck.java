import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = { "Spades", "Hearts", "Diamonds", "Clubs" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace" };
        int[] values = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };

        // Populate the standard 52-card deck
        for (String suit : suits) {
            for (int i = 0; i < ranks.length; i++) {
                cards.add(new Card(suit, ranks[i], values[i]));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is completely empty!");
        }
        return cards.remove(cards.size() - 1);
    }
}