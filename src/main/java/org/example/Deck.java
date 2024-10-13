package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();

    // Default constructor
    public Deck() {
        this.cards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    // Constructor that accepts a list of cards
    public Deck(List<Card> cards) {
        this.cards = cards;
        this.discardPile = new ArrayList<>();
    }

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
        if (cards.isEmpty()) {
            reshuffleDiscardPile();
        }
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    private void reshuffleDiscardPile() {
        if (!discardPile.isEmpty()) {
            cards.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(cards);
            System.out.println("Deck reshuffled from discard pile.");
        }
    }

}
