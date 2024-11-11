package org.example;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.*;

public class A2_A1_scenario {

    private Game game;
    private Player player1, player2, player3, player4;

    @Given("a new game is started with 4 players")
    public void a_new_game_is_started_with_players() {
        game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

    }
    @Given("the decks are created")
    public void the_decks_are_created() {
        game.dealCardsToPlayers();
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        player3 = game.getPlayers().get(2);
        player4 = game.getPlayers().get(3);


    }

    @Given("the players' hands are rigged with the specified initial cards")
    public void the_players_hands_are_rigged_with_the_specified_initial_cards() {
        rigAdventureDeck(game);
        rigEventDeck(game);
        rigPlayersHands(game);
    }

    @When("P1 draws a quest card of 4 stages")
    public void p1_draws_a_quest_card_of_stages() {
        game.offerSponsorship(new QuestCard(4), new Scanner(new ByteArrayInputStream("no\nyes\n".getBytes())));

    }

    @When("P1 declines to sponsor the quest")
    public void p1_declines_to_sponsor_the_quest() {
        // Already handled in the previous step by simulating "no" input
    }

    @When("P2 accepts to sponsor the quest and builds the 4 stages as specified")
    public void p2_accepts_to_sponsor_the_quest_and_builds_the_stages_as_specified() {
        Assertions.assertEquals(player2, game.getSponsor(), "Player 2 should be the sponsor.");
        StringBuilder inputBuilder = new StringBuilder();
        // Simulate building 4 stages
        // For simplicity, assuming specific card positions for each stage
        // Stage 1: F5 (position 1), Horse (position 8)
        inputBuilder.append("1\n8\nquit\n");
        // Stage 2: F15 (position 3), Sword (position 7)
        inputBuilder.append("2\n5\nquit\n");
        // Stage 3: F15 (position 3), Dagger (position 6), Battle-Axe (position 10)
        inputBuilder.append("2\n3\n4\nquit\n");
        // Stage 4: F40 (position 5), Battle-Axe (position 9)
        inputBuilder.append("2\n3\nquit\n");

        game.sponsorSetsUpQuest(4, new Scanner(new ByteArrayInputStream(inputBuilder.toString().getBytes())));
        game.determineEligibleParticipants();

    }

    @When("Quest 1 begins with P1, P3, and P4 participating")
    public void stage_begins_with_p_participating() {
        game.promptParticipantsForStage( new Scanner(new ByteArrayInputStream("yes\nyes\nyes".getBytes())));
        Assertions.assertTrue(game.getEligibleParticipants().contains(player1), "Player 1 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player3), "Player 3 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player4), "Player 4 should be participating.");
        String input = prepareSimulatedInputs();
        game.playTurns(new Scanner(new ByteArrayInputStream(input.getBytes())));

    }

    @And("Player 1 and 3 got out in Quest1")
    public void p1_out() {
        assertFalse(game.getStageParticipants().contains(player1), "Player 1 should be out");
        assertFalse(game.getStageParticipants().contains(player3), "Player 3 should be out");
        assertTrue(game.getStageParticipants().contains(player4), "Player 4 should still be in the game");

    }



    @Then("P3 has 0 shields and P4 has 4 shields")
    public void p3_and_p4_each_have_shields() {
        assertEquals(0, player3.getShields(), "P3 should have 0 shields.");
        assertEquals(4, player4.getShields(), "P4 should have 4 shields.");
    }

    @And("P2 has 12 cards in hand")
    public void p2_12_cards(){
        assertEquals(12, player2.getHandSize());
    }

    private void rigPlayersHands(Game game) {
        // Rig the hands for each player as specified
        List<Card> p1Hand = Arrays.asList(
                new FoeCard( 5), new FoeCard( 10), new FoeCard( 15), new FoeCard( 15),
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
        for (int i = 0; i < 10; i++) {
            if (i %2 == 0) {
                additionalCards.add(new WeaponCard("Sword", 10));
                additionalCards.add(new FoeCard(10));
            }
            additionalCards.add(new FoeCard(10));
            additionalCards.add(new WeaponCard("Sword", 10));
        }
        adventureDeck.getCards().addAll(additionalCards);
    }

    private void rigEventDeck(Game game) {
        Deck eventDeck = game.getEventDeck();

        eventDeck.getCards().clear();

        QuestCard questCard = new QuestCard(3);

        eventDeck.getCards().add(0, questCard);
        eventDeck.getCards().add(1, new QuestCard(3));
        eventDeck.getCards().add(2, new EventCard("Plague"));
    }
}

