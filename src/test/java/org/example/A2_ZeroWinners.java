package org.example;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.*;

public class A2_ZeroWinners {

    private Game game;
    private Player player1, player2, player3, player4;

    @Given("a new game0 is started with 4 players")
    public void a_new_game_is_started_with_4_players() {
        game = new Game();
        game.setupDecks();
        game.initializePlayers();
    }

    @Given("the decks0 are created")
    public void the_decks_are_created() {
        game.dealCardsToPlayers();

    }

    @Given("the players'0 hands are rigged with the specified initial cards")
    public void the_players_hands_are_rigged_with_the_specified_initial_cards() {
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        player3 = game.getPlayers().get(2);
        player4 = game.getPlayers().get(3);
        rigAdventureDeck(game);
        rigEventDeck(game);
        rigPlayersHands(game);
    }

    @When("P1 draws a 2-stage quest and decides to sponsor it")
    public void p1_draws_a_2_stage_quest_and_decides_to_sponsor_it() {
        game.offerSponsorship(new QuestCard(2), new Scanner(new ByteArrayInputStream("yes\n".getBytes())));
        Assertions.assertEquals(player1, game.getSponsor(), "Player 1 should be the sponsor.");

    }

    @When("P1 builds the 2 stages of the quest")
    public void p1_builds_the_2_stages_of_the_quest() {
        game.sponsorSetsUpQuest(2, new Scanner(new ByteArrayInputStream("2\nquit\n2\nquit\n1\n2\n2\nquit".getBytes())));
        game.determineEligibleParticipants();
        game.promptParticipantsForStage( new Scanner(new ByteArrayInputStream("yes\nyes\nyes".getBytes())));
        Assertions.assertTrue(game.getEligibleParticipants().contains(player2), "Player 2 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player3), "Player 3 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player4), "Player 4 should be participating.");
    }

    @When("P2, P3, and P4 participate0 in stage 1")
    public void p2_p3_and_p4_participate0_in_stage_1() {
        game.playTurns(new Scanner(new ByteArrayInputStream("3\n3\n3\n1\nquit\n1\nquit\n1\nquit".getBytes())));
        Assertions.assertFalse(game.getStageParticipants().contains(player2), "Player 2 should have failed stage 1.");
        Assertions.assertFalse(game.getStageParticipants().contains(player3), "Player 2 should have failed stage 1.");
        Assertions.assertFalse(game.getStageParticipants().contains(player4), "Player 4 should have failed stage 1.");
    }

    @Then("P2, P3, and P4 all lose stage 1 and cannot proceed")
    public void p2_p3_and_p4_all_lose_stage_1_and_cannot_proceed() {
        // Assert that none of the participants have continued to the next stage
        assertTrue(game.getStageParticipants().isEmpty(), "All participants should have lost stage 1 and cannot proceed.");
    }

    @When("the quest ends")
    public void the_quest_ends() {
        assertTrue(game.getQuestStages().isEmpty() || game.getStageParticipants().isEmpty(), "The quest should end with no participants remaining.");
    }

    @Then("there is no winner")
    public void there_is_no_winner() {
        assertTrue(game.getWinners().isEmpty(), "No Winners");
        Assertions.assertEquals(0, player2.getShields(), "Player 2 should have 0 shields.");
        Assertions.assertEquals(0, player3.getShields(), "Player 2 should have 0 shields.");
        Assertions.assertEquals(0, player4.getShields(), "Player 4 should have 0 shields.");

    }

    @Then("P1 discards all cards used in the quest and draws new cards to refill their hand")
    public void p1_discards_all_cards_used_in_the_quest_and_draws_new_cards_to_refill_hand() {
        int initialHandSize = player1.getHandSize();

        game.sponsorDrawsReplacementCards(2, 2, new Scanner(new ByteArrayInputStream("4".getBytes()))); // Simulate P1 drawing to replenish hand

        assertEquals(12, player1.getHandSize(), "P1 should have refilled their hand to 12 cards after discarding and drawing.");
        assertTrue(player1.getHandSize() > initialHandSize, "P1's hand should increase after drawing new cards.");
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

        simulatedInput.append("2\nquit\n");

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


        eventDeck.getCards().add(0, new EventCard("Plague"));
        eventDeck.getCards().add(1, new EventCard("Prosperity"));
        eventDeck.getCards().add(2, new EventCard("Queen's Favor"));


    }
}
