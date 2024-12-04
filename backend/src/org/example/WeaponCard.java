package org.example;
public class WeaponCard extends Card {
    private final int value;

    public WeaponCard(String name, int value) {
        setType("Weapon");
        setName(name);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getName() + " (Weapon, Power: " + value + ")";
    }

}
