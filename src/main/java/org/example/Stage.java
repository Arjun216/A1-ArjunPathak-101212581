package org.example;
import java.util.ArrayList;
import java.util.List;

public class Stage {
    private FoeCard foe;
    private List<Card> cards = new ArrayList<>();
    private List<String> weaponNames = new ArrayList<>();
    private boolean hasFoe = false;

    public void addCard(Card card) {
        if (card instanceof FoeCard && !hasFoe) {
            foe = (FoeCard) card;
            cards.add(card);
            hasFoe = true;
        } else if (card instanceof WeaponCard) {
            WeaponCard weapon = (WeaponCard) card;
            if (!weaponNames.contains(weapon.getName())) {
                cards.add(card);
                weaponNames.add(weapon.getName());
            }
        }
    }

    public boolean canAddCard(Card card) {
        if (card instanceof FoeCard) {
            return !hasFoe;
        } else if (card instanceof WeaponCard) {
            WeaponCard weapon = (WeaponCard) card;
            return !weaponNames.contains(weapon.getName());
        }
        return false;
    }

    public int getTotalValue() {
        int totalValue = 0;
        for (Card card : cards) {
            if (card instanceof FoeCard) {
                totalValue += ((FoeCard) card).getValue();
            } else if (card instanceof WeaponCard) {
                totalValue += ((WeaponCard) card).getValue();
            }
        }
        return totalValue;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean isValid(int previousStageValue) {
        if (foe == null) {
            System.out.println("Stage must contain exactly one Foe card.");
            return false;
        }
        if (getTotalValue() <= previousStageValue) {
            System.out.println("Stage value must be greater than the previous stage value (" + previousStageValue + ").");
            return false;
        }
        return true;
    }
    public boolean hasFoeCard() {
        for (Card card : cards) {
            if (card instanceof FoeCard) {
                return true;
            }
        }
        return false;
    }
}
