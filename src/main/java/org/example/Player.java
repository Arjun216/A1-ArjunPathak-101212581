package org.example;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Player {
    private String id;
    private List<Card> hand;
    private int shields;
    private int attackValue;
    private List<Card> currentAttack;
    private int initialHandSize;


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
    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public int getShields() {
        return shields;
    }
    public void setCurrentAttack(List<Card> attackCards) {
        this.currentAttack = attackCards;
    }
    public List<Card> getCurrentAttack() {
        return this.currentAttack;
    }

    public void addShields(int number) {
        shields += number;
    }

    public void displayHand() {
        hand.sort(new CardComparator());

        // Display the sorted hand
        System.out.println("Your Hand:" + getId());
        int index = 1;
        for (Card card : hand) {
            if (card instanceof FoeCard) {
                FoeCard foe = (FoeCard) card;
                System.out.println(index++ + ": " + foe.getName() + " (Foe, Power: " + foe.getValue() + ")");
            } else if (card instanceof WeaponCard) {
                WeaponCard weapon = (WeaponCard) card;
                System.out.println(index++ + ": " + weapon.getName() + " (Weapon, Power: " + weapon.getValue() + ")");
            }
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
    public void setInitialHandSize(int size) {
        this.initialHandSize = size;
    }

    public int getInitialHandSize() {
        return this.initialHandSize;
    }
}
