package org.example;
import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();

    public void addCards(List<Card> newCards) {
        cards.addAll(newCards);
    }

    public int getTotalCards() {
        return cards.size();
    }

    public List<Card> getCardsOfType(String type) {
        List<Card> result = new ArrayList<>();
        for (Card card : cards) {
            if (card.getType().equals(type)) {
                result.add(card);
            }
        }
        return result;
    }
    public Card drawCard() {
        return cards.remove(0);
    }

}
