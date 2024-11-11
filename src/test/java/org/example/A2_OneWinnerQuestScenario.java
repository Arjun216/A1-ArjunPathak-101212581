package org.example;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.*;

public class A2_OneWinnerQuestScenario {

    private Game game;
    private Player player1, player2, player3, player4;

    @Given("a new1 game is started with 4 players")
    public void a_new_game_is_started_with_4_players() {
        game = new Game();
        game.setupDecks();
        game.initializePlayers();
    }

    @Given("the decks1 are created")
    public void the_decks_are_created() {
        game.dealCardsToPlayers();

    }

    @Given("the players'1 hands are rigged with the specified initial cards")
    public void the_players_hands_are_rigged_with_the_specified_initial_cards() {
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        player3 = game.getPlayers().get(2);
        player4 = game.getPlayers().get(3);
        rigAdventureDeck(game);
        rigEventDeck(game);
        rigPlayersHands(game);

    }

    @When("P1 draws a 4-stage quest and decides to sponsor it")
    public void p1_draws_a_4_stage_quest_and_decides_to_sponsor_it() {
        game.offerSponsorship(new QuestCard(4), new Scanner(new ByteArrayInputStream("yes\n".getBytes())));
        Assertions.assertEquals(player1, game.getSponsor(), "Player 1 should be the sponsor.");

    }

    @When("P1 builds the 4 stages of the quest")
    public void p1_builds_the_4_stages_of_the_quest() {
        game.sponsorSetsUpQuest(4, new Scanner(new ByteArrayInputStream("2\nquit\n2\nquit\n1\n2\n2\nquit\n1\n1\nquit".getBytes())));
        game.determineEligibleParticipants();
        game.promptParticipantsForStage( new Scanner(new ByteArrayInputStream("yes\nyes\nyes".getBytes())));
        Assertions.assertTrue(game.getEligibleParticipants().contains(player2), "Player 2 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player3), "Player 3 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player4), "Player 4 should be participating.");
    }

    @When("P2, P3, and P4 participate and win all stages")
    public void p2_p3_and_p4_participate_and_win_all_stages() {
        game.playTurns(new Scanner(new ByteArrayInputStream("5\n5\n5\n7\nquit\n1\n1\nquit\n7\nquit\nyes\nyes\nyes\n1\n4\nquit\n2\n5\nquit\n2\nquit\nyes\nyes\nyes\n6\n3\nquit\n9\n1\nquit\n3\n6\nquit\nyes\nyes\nyes\n3\nquit\n3\n8\nquit\n4\nquit\nyes\nyes\nyes".getBytes())));
        Assertions.assertTrue(game.getStageParticipants().contains(player2), "Player 2 should have won stage 1.");
        Assertions.assertTrue(game.getStageParticipants().contains(player3), "Player 2 should have won stage 1.");
        Assertions.assertTrue(game.getStageParticipants().contains(player4), "Player 4 should have won stage 1.");
    }

    @Then("P2, P3, and P4 each earn 4 shields")
    public void p2_p3_and_p4_each_earn_4_shields() {
        Assertions.assertEquals(4, player2.getShields(), "Player 2 should have 4 shields.");
        Assertions.assertEquals(4, player3.getShields(), "Player 3 should have 4 shields.");
        Assertions.assertEquals(4, player4.getShields(), "Player 4 should have 4 shields.");
        game.endTurn();
    }

    @When("P2 draws the \"Plague\" event card")
    public void p2_plague() {
        //player2.addCardToHand(new EventCard("Plague"));
        game.playTurn(new Scanner("4"));
        game.endTurn();
    }

    @Then("P2 loses 2 shields")
    public void p2_loses_2_shields() {
        Assertions.assertEquals(2, player2.getShields(), "Player 2 should have 4 shields.");
    }

    @When("P3 draws the \"Prosperity\" event card")
    public void p3_plague() {
        //player3.addCardToHand(new EventCard("Prosperity"));
        game.playTurn(new Scanner("4"));
        game.endTurn();;

    }

    @Then("all players each receive 2 adventure cards")
    public void all_players_each_receive_2_adventure_cards() {
        assertEquals(player1.getHandSize(),13, "Player 1 should have a hand size of 14");
        assertEquals(player2.getHandSize(),11, "Player 2 should have a hand size of 14");
        assertEquals(player3.getHandSize(),9, "Player 3 should have a hand size of 14");

    }

    @When("P4 draws the \"Queen’s Favor\" event card")
    public void p4_queen() {
        //player4.addCardToHand(new EventCard("Queen's Favor"));
        game.playTurn(new Scanner("4"));
        game.endTurn();

    }

    @Then("P4 draws 2 adventure cards")
    public void p4_draws_2_adventure_cards() {
        assertEquals(12, player4.getHandSize(), "Player 4 should have a hand size of 14"); // Initial 12 + 2 cards
        game.endTurn();
    }

    @When("P1 draws a 3-stage quest and decides to sponsor it")
    public void p1_draws_a_3_stage_quest_and_decides_to_sponsor_it() {
        game.offerSponsorship(new QuestCard(4), new Scanner(new ByteArrayInputStream("yes\n".getBytes())));
        Assertions.assertEquals(player1, game.getSponsor(), "Player 1 should be the sponsor.");

    }

    @When("P1 builds the 3 stages of the quest")
    public void p1_builds_the_3_stages_of_the_quest() {
        player1.clearHand();
        rig_p1(game);
        game.sponsorSetsUpQuest(3, new Scanner(new ByteArrayInputStream("2\nquit\n2\nquit\n2\nquit".getBytes())));
        game.determineEligibleParticipants();

    }

    @When("P2, P3, and P4 participate in stage 1")
    public void p2_p3_and_p4_participate_in_stage_1() {
        game.promptParticipantsForStage( new Scanner(new ByteArrayInputStream("yes\nyes\nyes".getBytes())));
        game.playTurns(new Scanner(new ByteArrayInputStream("10\n10\n5\n5\nquit\n5\nquit\nquit\nyes\nyes\n3\n3\nquit\n4\nquit\nyes\nyes\n7\n4\nquit\n7\n4\nquit\nyes\nyes".getBytes())));


    }


    @When("P2 and P3 win Quest 1")
    public void p2_and_p3_participate_and_win_stages_2_and_3() {
        Assertions.assertTrue(game.getStageParticipants().contains(player3), "Player 3 should have won stage 1.");
        Assertions.assertTrue(game.getStageParticipants().contains(player2), "Player 2 should have won stage 1.");
    }

    @Then("P2 and P3 each earn 3 shields")
    public void p2_and_p3_each_earn_3_shields() {
        Assertions.assertEquals(7, player3.getShields(), "Player 3 should have 7 shields.");
        Assertions.assertEquals(5, player2.getShields(), "Player 4 should have 5 shields.");
        Assertions.assertEquals(4, player4.getShields(), "Player 4 should have 4 shields.");

        game.endTurn();
    }

    @Then("P3 is declared the winner")
    public void p3_is_declared_the_winner() {
        assertEquals(game.getWinners().get(0), player3,"Player 3 should be the winner");
    }

    private void rig_p1 (Game game) {
        List<Card> p1Hand = Arrays.asList(
                new FoeCard(5), new FoeCard( 5), new FoeCard( 10), new FoeCard( 15),
                new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10), new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Lance", 20)
        );
        game.getPlayers().get(0).setHand(new ArrayList<>(p1Hand)); // P1

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
