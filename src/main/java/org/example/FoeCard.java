package org.example;

public class FoeCard extends Card {
    private int value;

    public FoeCard(int value) {
        setType("Foe");
        setName("F" + value);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return "Foe (Power: " + value + ")";
    }
}
