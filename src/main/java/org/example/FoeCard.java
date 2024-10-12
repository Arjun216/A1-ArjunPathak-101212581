package org.example;

public class FoeCard extends Card {
    private int value;

    public FoeCard(int value) {
        setType("Foe");
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
