package org.example;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class A2_TwoWinnerQuestScenarioTest {

    private Game game;
    private Player player1, player2, player3, player4;

    @Given("a new game is started")
    public void a_new_game_is_started() {
        game = new Game();
        game.setupDecks();
        game.initializePlayers();
    }

    @And("each player is dealt 12 cards")
    public void each_player_is_dealt_12_cards() {
        game.dealCardsToPlayers();
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        player3 = game.getPlayers().get(2);
        player4 = game.getPlayers().get(3);
    }

    @And("the adventure deck is rigged")
    public void the_adventure_deck_is_rigged() {
        rigAdventureDeck(game);
    }

    @And("the event deck is rigged")
    public void the_event_deck_is_rigged() {
        rigEventDeck(game);
    }

    @And("the players' hands are rigged")
    public void the_players_hands_are_rigged() {
        rigPlayersHands(game);
    }



    @And("Player 1 is offered sponsorship and accepts")
    public void player1_is_offered_sponsorship_and_accepts() {
        game.offerSponsorship(new QuestCard(4), new Scanner(new ByteArrayInputStream("yes\n".getBytes())));
        Assertions.assertEquals(player1, game.getSponsor(), "Player 1 should be the sponsor.");
    }

    @And("Players 2, 3, and 4 decide to participate")
    public void players_2_3_and_4_decide_to_participate() {
        game.sponsorSetsUpQuest(4, new Scanner(new ByteArrayInputStream("2\nquit\n2\nquit\n1\n2\n2\nquit\n1\n1\nquit".getBytes())));
        game.determineEligibleParticipants();
        game.promptParticipantsForStage( new Scanner(new ByteArrayInputStream("yes\nyes\nyes".getBytes())));
        Assertions.assertTrue(game.getEligibleParticipants().contains(player2), "Player 2 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player3), "Player 3 should be participating.");
        Assertions.assertTrue(game.getEligibleParticipants().contains(player4), "Player 4 should be participating.");
    }

    @When("Players 2, 3, and 4 complete the first stage")
    public void players_2_3_and_4_complete_the_first_stage() {
        game.playTurns(new Scanner(new ByteArrayInputStream("5\n5\n5\n7\nquit\n1\nquit\n7\nquit\nyes\nyes\n1\n4\nquit\n2\n5\nquit\nyes\nyes\n6\n3\nquit\n9\n1\nquit\nyes\nyes\n3\nquit\n3\nquit\nyes\nyes".getBytes())));
    }

    @Then("Players 2 and 4 should have won stage 1")
    public void players_2_and_4_should_have_won_stage1() {
        Assertions.assertTrue(game.getStageParticipants().contains(player2), "Player 2 should have won stage 1.");
        Assertions.assertTrue(game.getStageParticipants().contains(player4), "Player 4 should have won stage 1.");
    }

    @Then("Player 3 should have lost stage 1")
    public void player3_should_have_lost_stage1() {
        Assertions.assertFalse(game.getStageParticipants().contains(player3), "Player 3 should have lost stage 1.");
    }


    @Then("Players 2 and 4 should each have 4 shields")
    public void players_2_and_4_should_each_have_4_shields() {
        Assertions.assertEquals(4, player2.getShields(), "Player 2 should have 4 shields.");
        Assertions.assertEquals(4, player4.getShields(), "Player 4 should have 4 shields.");
        game.endTurn();

    }

    @And("Player 2 draws a 3-stage quest and declines to sponsor it")
    public void player2_draws_a_3_stage_quest_and_declines_to_sponsor_it() {
        game.offerSponsorship(new QuestCard(4), new Scanner(new ByteArrayInputStream("no\nyes\n".getBytes())));
        Assertions.assertEquals(player3, game.getSponsor(), "Player 3 should be the sponsor.");
    }
    @When("Player 3 decides to sponsor the quest and builds its stages")
    public void player3_decides_to_sponsor_and_builds_stages() {
        game.sponsorSetsUpQuest(3, new Scanner(new ByteArrayInputStream("2\nquit\n2\nquit\n1\n2\n5\nquit".getBytes())));
        game.determineEligibleParticipants();
    }

    @When("Player 1 declines to participate and Players 2 and 4 participate")
    public void player1_declines_to_participate() {
        game.promptParticipantsForStage( new Scanner(new ByteArrayInputStream("no\nyes\nyes\n".getBytes())));
        Assertions.assertFalse(game.getStageParticipants().contains(player1), "Player 1 should not be participating.");
        Assertions.assertTrue(game.getStageParticipants().contains(player2), "Player 2 should be participating.");
        Assertions.assertTrue(game.getStageParticipants().contains(player4), "Player 4 should be participating.");
    }

    @When("Players 2 and 4 play and win stages 1, 2, and 3")
    public void players_2_and_4_play_and_win_stages_1_2_and_3() {
        game.playTurns(new Scanner(new ByteArrayInputStream("1\n3\nquit\n1\nquit\nyes\nyes\n1\n4\nquit\n1\nquit\nyes\nyes\n1\n7\nquit\n1\n8\nquit\nyes\nyes".getBytes())));
        Assertions.assertTrue(game.getStageParticipants().contains(player2), "Player 2 should have won stage 1.");
        Assertions.assertTrue(game.getStageParticipants().contains(player4), "Player 4 should have won stage 1.");
    }

    @Then("Players 2 and 4 should each earn 3 shields")
    public void players_2_and_4_should_each_earn_3_shields() {
        Assertions.assertEquals(7, player2.getShields(), "Player 2 should have earned an additional 3 shields.");
        Assertions.assertEquals(7, player4.getShields(), "Player 4 should have earned an additional 3 shields.");
    }

    @Then("Players 2 and 4 are declared winners")
    public void players_2_and_4_are_declared_winners() {
        Assertions.assertTrue(game.getWinners().contains(player2), "Player 2 should be declared a winner.");
        Assertions.assertTrue(game.getWinners().contains(player4), "Player 4 should be declared a winner.");
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
