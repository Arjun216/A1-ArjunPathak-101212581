package org.example;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    @Override
    public int compare(Card c1, Card c2) {
        // Foes should come before Weapons
        if (!c1.getType().equals(c2.getType())) {
            if (c1.getType().equals("Foe")) return -1;
            if (c2.getType().equals("Foe")) return 1;
        }

        // If both cards are of type Foe or Weapon, compare by value
        if (c1 instanceof FoeCard && c2 instanceof FoeCard) {
            return Integer.compare(c1.getValue(), c2.getValue());
        }

        if (c1 instanceof WeaponCard w1 && c2 instanceof WeaponCard w2) {

            // Swords should come before Horses
            if (w1.getName().equals("Sword") && w2.getName().equals("Horse")) return -1;
            if (w1.getName().equals("Horse") && w2.getName().equals("Sword")) return 1;

            // Compare by value if they are the same type of weapon
            return Integer.compare(w1.getValue(), w2.getValue());
        }

        return 0;
    }
}