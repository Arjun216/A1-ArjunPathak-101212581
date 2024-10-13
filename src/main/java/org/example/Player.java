package org.example;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private List<Card> hand;
    private int shields;

    public Player(String id) {
        this.id = id;
        this.hand = new ArrayList<>();
        this.shields = 0;
    }

    public String getId() {
        return id;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public int getHandSize() {
        return hand.size();
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getShields() {
        return shields;
    }

    public void addShields(int number) {
        shields += number;
    }
}
