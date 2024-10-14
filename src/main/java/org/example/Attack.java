package org.example;
import java.util.ArrayList;
import java.util.List;

public class Attack {
    private List<Card> cards = new ArrayList<>();
    private List<String> weaponNames = new ArrayList<>();

    public void addCard(Card card) {
        if (card instanceof WeaponCard) {
            WeaponCard weapon = (WeaponCard) card;
            if (!weaponNames.contains(weapon.getName())) {
                cards.add(card);
                weaponNames.add(weapon.getName());
            }
        }
    }

    public boolean canAddCard(Card card) {
        if (card instanceof WeaponCard) {
            WeaponCard weapon = (WeaponCard) card;
            return !weaponNames.contains(weapon.getName());
        }
        return false;
    }

    public int getTotalValue() {
        int totalValue = 0;
        for (Card card : cards) {
            totalValue += ((WeaponCard) card).getValue();
        }
        return totalValue;
    }

    public boolean isValid() {
        return !cards.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }
}
