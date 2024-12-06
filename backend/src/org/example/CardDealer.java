package org.example;
import java.util.ArrayList;
import java.util.List;

public class CardDealer {

    public static List<Card> createAdventureCards() {
        List<Card> adventureCards = new ArrayList<>();

        for (int i = 0; i < 8; i++) adventureCards.add(new FoeCard(5));
        for (int i = 0; i < 7; i++) adventureCards.add(new FoeCard(10));
        for (int i = 0; i < 8; i++) adventureCards.add(new FoeCard(15));
        for (int i = 0; i < 7; i++) adventureCards.add(new FoeCard(20));
        for (int i = 0; i < 7; i++) adventureCards.add(new FoeCard(25));
        for (int i = 0; i < 4; i++) adventureCards.add(new FoeCard(30));
        for (int i = 0; i < 4; i++) adventureCards.add(new FoeCard(35));
        for (int i = 0; i < 2; i++) adventureCards.add(new FoeCard(40));
        for (int i = 0; i < 2; i++) adventureCards.add(new FoeCard(50));
        for (int i = 0; i < 1; i++) adventureCards.add(new FoeCard(70));

        for (int i = 0; i < 6; i++) adventureCards.add(new WeaponCard("Dagger", 5));
        for (int i = 0; i < 16; i++) adventureCards.add(new WeaponCard("Sword", 10));
        for (int i = 0; i < 12; i++) adventureCards.add(new WeaponCard("Horse", 10));
        for (int i = 0; i < 8; i++) adventureCards.add(new WeaponCard("Battle-Axe", 15));
        for (int i = 0; i < 6; i++) adventureCards.add(new WeaponCard("Lance", 20));
        for (int i = 0; i < 2; i++) adventureCards.add(new WeaponCard("Excalibur", 30));

        return adventureCards;
    }

    public static List<Card> createEventCards() {
        List<Card> eventCards = new ArrayList<>();
        for (int i = 0; i < 3; i++) eventCards.add(new QuestCard(2));
        for (int i = 0; i < 4; i++) eventCards.add(new QuestCard(3));
        for (int i = 0; i < 3; i++) eventCards.add(new QuestCard(4));
        for (int i = 0; i < 2; i++) eventCards.add(new QuestCard(5));

        eventCards.add(new EventCard("Plague"));
        eventCards.add(new EventCard("Queen's Favor"));
        eventCards.add(new EventCard("Queen's Favor"));
        eventCards.add(new EventCard("Prosperity"));
        eventCards.add(new EventCard("Prosperity"));

        return eventCards;
    }
}
