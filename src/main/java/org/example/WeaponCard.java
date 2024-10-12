package org.example;
public class WeaponCard extends Card {
    private String name;
    private int value;

    public WeaponCard(String name, int value) {
        setType("Weapon");
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
