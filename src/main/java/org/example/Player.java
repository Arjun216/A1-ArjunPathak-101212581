package org.example;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private List<Card> hand;
    private int shields;
    private int attackValue;

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
    public void discardExcessCards(int number) {
        for (int i = 0; i < number; i++) {
            Card discardedCard = hand.remove(hand.size() - 1); // Remove last card
        }
    }

    public void displayHand() {
        hand.sort(new CardComparator());
        System.out.println("Your Hand:");
        int index = 1;
        for (Card card : hand) {
            System.out.println(index + ": " + card.getType());
            index++;
        }
    }

    public Card discardCard(int index) {
        return hand.remove(index);
    }

    public void setAttackValue(int value) {
        this.attackValue = value;
    }

    public int getAttackValue() {
        return attackValue;
    }

    public void clearHand() {
        hand.clear();
    }

    public void removeCardFromHand(int index) {
        hand.remove(index);
    }
}
