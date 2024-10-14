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
    public void discardExcessCards(Scanner scanner) {
        int excessCards = hand.size() - 12;
        System.out.println("You have " + hand.size() + " cards. Please discard " + excessCards + " card(s).");
        for (int i = 0; i < excessCards; i++) {
            displayHand();
            System.out.print("Enter the position of the card to discard: ");
            String input = scanner.nextLine();
            try {
                int index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= hand.size()) {
                    System.out.println("Invalid position. Please try again.");
                    i--; // Repeat this iteration
                } else {
                    Card discardedCard = hand.remove(index);
                    System.out.println("Discarded: " + discardedCard);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                i--; // Repeat this iteration
            }
        }
    }


    public void displayHand() {
        hand.sort(new CardComparator());

        // Display the sorted hand
        System.out.println("Your Hand:");
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
}
