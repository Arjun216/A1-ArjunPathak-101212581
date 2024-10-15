package org.example;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class gameATest {

    @Test
    @DisplayName("A-TEST JP-Scenario")
    public void testJPScenario() {
        // Initialize the game and players
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        // Rig the players' hands
        rigPlayersHands(game);

        // Prepare simulated inputs
        String simulatedInputs = prepareSimulatedInputs();

        // Create a Scanner with the simulated inputs
        Scanner simulatedScanner = new Scanner(new ByteArrayInputStream(simulatedInputs.getBytes()));

        // Rig the adventure deck for specific card draws
        rigAdventureDeck(game);

        rigEventDeck(game);

        // Start the game flow
        // P1's turn
        game.playTurn(simulatedScanner);

        // P1's turn ends, advance to P2
        game.advanceTurn();

        // P2's turn (P2 sponsors the quest)
        game.playTurn(simulatedScanner);

        // The quest proceeds within the game logic, so we don't need to call advanceTurn here

        // Assertions after Stage 2
        // Assert P1's shields and hand
        Player p1 = game.getPlayers().get(0); // P1
        assertEquals(0, p1.getShields(), "P1 should have 0 shields after Stage 2.");

        List<String> expectedP1Hand = Arrays.asList("F5", "F10", "F15", "F15", "F30", "Horse", "Battle-Axe", "Battle-Axe", "Lance");
        List<String> actualP1Hand = p1.getHand().stream()
                .map(Card::getName)
                .collect(Collectors.toList());
        assertEquals(expectedP1Hand, actualP1Hand, "P1's hand does not match expected after Stage 2.");

        // Assertions after Stage 4
        // Assert P3's shields and hand
        Player p3 = game.getPlayers().get(2); // P3
        assertEquals(0, p3.getShields(), "P3 should have 0 shields after Stage 4.");

        List<String> expectedP3Hand = Arrays.asList("F5", "F5", "F15", "F30", "Sword");
        List<String> actualP3Hand = p3.getHand().stream()
                .map(Card::getName)
                .collect(Collectors.toList());
        assertEquals(expectedP3Hand, actualP3Hand, "P3's hand does not match expected after Stage 4.");

        // Assert P4's shields and hand
        Player p4 = game.getPlayers().get(3); // P4
        assertEquals(4, p4.getShields(), "P4 should have 4 shields after Stage 4.");

        List<String> expectedP4Hand = Arrays.asList("F15", "F15", "F40", "Lance");
        List<String> actualP4Hand = p4.getHand().stream()
                .map(Card::getName)
                .collect(Collectors.toList());
        assertEquals(expectedP4Hand, actualP4Hand, "P4's hand does not match expected after Stage 4.");

        // Assert P2's hand size after drawing replacement cards and trimming
        Player p2 = game.getPlayers().get(1); // P2
        System.out.println(p2.getHand());
        assertEquals(12, p2.getHandSize(), "P2 should have 12 cards in hand after drawing replacement cards and trimming.");
    }

    private void rigPlayersHands(Game game) {
        // Rig the hands for each player as specified
        List<Card> p1Hand = Arrays.asList(
                new FoeCard( 5), new FoeCard( 5), new FoeCard( 15), new FoeCard( 15),
                new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Lance", 20)
        );
        List<Card> p2Hand = Arrays.asList(
                new FoeCard( 5), new FoeCard( 5), new FoeCard( 15), new FoeCard( 15),
                new FoeCard( 40), new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Excalibur", 30)
        );
        List<Card> p3Hand = Arrays.asList(
                new FoeCard(5), new FoeCard( 5), new FoeCard( 5), new FoeCard( 15),
                new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10), new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Lance", 20)
        );
        List<Card> p4Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(15), new FoeCard( 15), new FoeCard( 40),
                new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Lance", 20), new WeaponCard("Excalibur", 30)
        );

        // Assign the rigged hands to the players
        game.getPlayers().get(0).setHand(new ArrayList<>(p1Hand)); // P1
        game.getPlayers().get(1).setHand(new ArrayList<>(p2Hand)); // P2
        game.getPlayers().get(2).setHand(new ArrayList<>(p3Hand)); // P3
        game.getPlayers().get(3).setHand(new ArrayList<>(p4Hand)); // P4
    }

    private String prepareSimulatedInputs() {
        StringBuilder simulatedInput = new StringBuilder();

        // P1 is asked to sponsor and declines
        simulatedInput.append("no\n");

        // P2 is asked to sponsor and accepts
        simulatedInput.append("yes\n");

        // P2 sets up the 4 stages (simulate inputs if necessary)
        // Stage 1 Setup:
        // P2 selects F5 (position 1) and Horse +10 (position 8)
        simulatedInput.append("1\n8\nquit\n");

        // Stage 2 Setup:
        // P2 selects F15 (position 2) and Sword +10 (position 5)
        simulatedInput.append("2\n5\nquit\n");

        // Stage 3 Setup:
        // P2 selects F15 (position 2 or updated position after previous discards), Dagger +5 (position 6), Battle-Axe +15 (position 10)
        simulatedInput.append("2\n3\n4\nquit\n");

        // Stage 4 Setup:
        // P2 selects F40 (position 4 or updated position), Battle-Axe +15 (position 10 or updated position)
        simulatedInput.append("2\n3\nquit\n");


        // Stage 1: Participants decide to join
        // P1 decides to participate
        simulatedInput.append("yes\n");
        // P3 decides to participate
        simulatedInput.append("yes\n");
        // P4 decides to participate
        simulatedInput.append("yes\n");

        // P1 discards F5 to trim down to 12 cards
        simulatedInput.append("1\n");


        // P3 discards F5 to trim down to 12 cards
        simulatedInput.append("1\n");


        // P4 discards F5 to trim down to 12 cards
        simulatedInput.append("1\n");

        // P1 builds attack: Dagger + Sword
        simulatedInput.append("5\n5\nquit\n");

        // P3 builds attack: Sword + Dagger
        simulatedInput.append("5\n4\nquit\n");

        // P4 builds attack: Dagger + Horse
        simulatedInput.append("5\n7\nquit\n");

        // Stage 2:
        // P1 decides to participate
        simulatedInput.append("yes\n");
        // P1 discards F5 to trim down to 12 cards (if necessary)
        //simulatedInput.append("1\n");

        // P3 decides to participate
        simulatedInput.append("yes\n");
        // P3 discards F5 to trim down to 12 cards (if necessary)
        //simulatedInput.append("1\n");

        // P4 decides to participate
        simulatedInput.append("yes\n");
        // P4 discards F5 to trim down to 12 cards (if necessary)
        //simulatedInput.append("1\n");

        // P1 builds attack: Horse + Sword
        simulatedInput.append("7\n6\nquit\n");

        // P3 builds attack: Battle-Axe + Sword
        simulatedInput.append("9\n4\nquit\n");

        // P4 builds attack: Horse + Battle-Axe
        simulatedInput.append("6\n7\nquit\n");

        // Stage 3:
        // P3 decides to participate
        simulatedInput.append("yes\n");

        // P4 decides to participate
        simulatedInput.append("yes\n");

        // P3 builds attack: Lance + Horse + Sword
        simulatedInput.append("9\n6\n4\nquit\n");

        // P4 builds attack: Battle-Axe + Sword + Lance
        simulatedInput.append("7\n5\n7\nquit\n");

        // Stage 4:
        // P3 decides to participate
        simulatedInput.append("yes\n");

        // P4 decides to participate
        simulatedInput.append("yes\n");

        // P3 builds attack: Battle-Axe + Horse + Lance
        simulatedInput.append("7\n6\n6\nquit\n");

        // P4 builds attack: Dagger + Sword + Lance + Excalibur
        simulatedInput.append("4\n4\n5\n5\nquit\n");
        simulatedInput.append("yes\n");

        simulatedInput.append("10\n");


        // End of inputs
        return simulatedInput.toString();
    }

    private void rigAdventureDeck(Game game) {
        Deck adventureDeck = game.getAdventureDeck();
        // Clear existing cards
        adventureDeck.getCards().clear();

        List<Card> additionalCards = new ArrayList<>();



        // Add cards in the exact order they will be drawn
        List<Card> riggedCards = Arrays.asList(
                // Stage 1 Draws
                new FoeCard(30),                 // P1 draws
                new WeaponCard("Sword", 10),     // P3 draws
                new WeaponCard("Battle-Axe",15), // P4 draws

                // Stage 2 Draws
                new FoeCard(10),                 // P1 draws (F10)
                new WeaponCard("Lance", 20),     // P3 draws
                new WeaponCard("Lance", 20),     // P4 draws

                // Stage 3 Draws
                new WeaponCard("Battle-Axe", 15),// P3 draws
                new WeaponCard("Sword", 10),     // P4 draws

                // Stage 4 Draws
                new FoeCard(30),                 // P3 draws
                new WeaponCard("Lance", 20)      // P4 draws
        );

        // Add all rigged cards to the deck in the order they will be drawn
        adventureDeck.getCards().addAll(riggedCards);
        for (int i = 0; i < 20; i++) {
            additionalCards.add(new WeaponCard("Extra Sword", 10));
        }
        adventureDeck.getCards().addAll(additionalCards);
    }

    private void rigEventDeck(Game game) {
        Deck eventDeck = game.getEventDeck();

        eventDeck.getCards().clear();

        QuestCard questCard = new QuestCard(4);

        eventDeck.getCards().add(0, questCard);

        eventDeck.getCards().add(1, new EventCard("Plague"));
        eventDeck.getCards().add(2, new QuestCard(3));
    }




}
