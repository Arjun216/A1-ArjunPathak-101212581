package org.example;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    @DisplayName("Check cards are set up properly")
    public void RESP_1_test_01() {
        Game game = new Game();
        game.setupDecks();

        // Test Adventure Deck
        Deck adventureDeck = game.getAdventureDeck();
        assertEquals(100, adventureDeck.getTotalCards(), "Adventure deck should have 100 cards.");

        int foeCardsCount = adventureDeck.getCardsOfType("Foe").size();
        int weaponCardsCount = adventureDeck.getCardsOfType("Weapon").size();

        assertEquals(50, foeCardsCount, "Adventure deck should have 50 Foe cards.");
        assertEquals(50, weaponCardsCount, "Adventure deck should have 50 Weapon cards.");

        // Test Event Deck
        Deck eventDeck = game.getEventDeck();
        assertEquals(17, eventDeck.getTotalCards(), "Event deck should have 17 cards.");

        int questCardsCount = eventDeck.getCardsOfType("Quest").size();
        int eventCardsCount = eventDeck.getCardsOfType("Event").size();

        assertEquals(12, questCardsCount, "Event deck should have 12 Quest cards.");
        assertEquals(5, eventCardsCount, "Event deck should have 5 Event cards.");
    }

    @Test
    @DisplayName("Test to see if game distributes 12 cards to each player")
    public void RESP_2_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();

        for (Player player : game.getPlayers()) {
            assertEquals(12, player.getHandSize(), "Each player should have 12 cards.");
        }

        assertEquals(52, game.getAdventureDeck().getTotalCards(),
                "Adventure deck should have 52 cards remaining after dealing.");
    }

    @Test
    @DisplayName("Check if the game is initialized with player 1")
    public void RESP_3_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();
        Player currentPlayer = game.getCurrentPlayer();
        assertEquals("P1", currentPlayer.getId(), "The current player should be P1.");

    }

    @Test
    @DisplayName("Check if the game advances properly in terms of player switching")
    public void RESP_4_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();
        game.playTurn(); // P1's turn
        game.advanceTurn();
        game.playTurn(); // P2's turn
        game.advanceTurn();
        Player currentPlayer = game.getCurrentPlayer();
        assertEquals("P3", currentPlayer.getId(), "The current player should be P3.");
    }

    @Test
    @DisplayName("Display the winning name and check if the game terminates correctly")
    public void RESP_5_test_01() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        Player player1 = game.getPlayers().get(0);
        player1.addShields(7);

        game.checkForWinners();

        System.setOut(originalOut);
        String output = outputStream.toString().trim();
        assertEquals("Winner: P1", output, "Game should display the winner's ID and terminate.");
    }

    @Test
    public void RESP_6_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.initializeTurnOrder();

        // Create a known EventCard
        EventCard testEventCard = new EventCard("Test Event");

        // Create a deck with only the test EventCard
        List<Card> testEventDeckCards = new ArrayList<>();
        testEventDeckCards.add(testEventCard);
        Deck testEventDeck = new Deck(testEventDeckCards);

        game.setEventDeck(testEventDeck);

        Player currentPlayer = game.getCurrentPlayer();
        Card drawnCard = game.drawEventCard(currentPlayer);

        // Ensure an event card is drawn
        assertNotNull(drawnCard, "An event card should be drawn.");
        assertTrue(drawnCard instanceof EventCard, "The drawn card should be an EventCard.");
        EventCard eventCard = (EventCard) drawnCard;
        assertEquals("Test Event", eventCard.getEventName(), "The event card drawn should be 'Test Event'.");
    }

    @Test
    @DisplayName("Handle Plague event when the current player loses 2 shields")
    public void RESP_7_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.addShields(1);

        EventCard plagueCard = new EventCard("Plague");
        game.handleEventCard(plagueCard, currentPlayer);

        assertEquals(0, currentPlayer.getShields(), "Player's shields should drop to zero.");
    }

    @Test
    @DisplayName("Handle Queen's Favor event where the current player draws 2 adventure cards")
    public void RESP_7_test_02() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();

        Player currentPlayer = game.getPlayers().get(0);

        // Simulate a smaller initial hand size
        for (int i = 0; i < 9; i++) {
            currentPlayer.addCardToHand(game.getAdventureDeck().drawCard());
        }
        int initialHandSize = currentPlayer.getHandSize();
        assertEquals(9, initialHandSize, "Initial hand size should be 9.");

        EventCard queensFavorCard = new EventCard("Queen's Favor");
        game.handleEventCard(queensFavorCard, currentPlayer);

        assertEquals(11, currentPlayer.getHandSize(), "Current player should have drawn 2 adventure cards.");
    }

    @Test
    @DisplayName(" Handle Prosperity event where all players draw 2 adventure cards")
    public void RESP_7_test_03() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();

        for (Player player : game.getPlayers()) {
            for (int i = 0; i < 9; i++) {
                Card card = game.getAdventureDeck().drawCard();
                player.addCardToHand(card);
            }
            int initialHandSize = player.getHandSize();
            assertEquals(9, initialHandSize, "Player " + player.getId() + " should have an initial hand size of 9.");
        }

        EventCard prosperityCard = new EventCard("Prosperity");
        game.handleEventCard(prosperityCard, null);

        // After drawing 2 cards, each player's hand size should now be 11 (9 + 2)
        for (Player player : game.getPlayers()) {
            int expectedHandSize = 11;
            int actualHandSize = player.getHandSize();
            assertEquals(expectedHandSize, actualHandSize, "Player " + player.getId() + " should have a hand size of 11 after the Prosperity event.");
        }
    }


    @Test
    @DisplayName("Check that the event card is in the discard pile")
    public void RESP_8_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        Player currentPlayer = game.getCurrentPlayer();
        EventCard eventCard = new EventCard("Plague");
        game.handleEventCard(eventCard, currentPlayer);

        assertTrue(game.getEventDiscardPile().contains(eventCard), "Event card should be in the discard pile.");
        game.endTurn(); // End the current Players turn

        Player nextPlayer = game.getCurrentPlayer();  // Verify that the next player is now the current player
        assertNotEquals(currentPlayer.getId(), nextPlayer.getId(), "It should be the next player's turn.");
    }

    @Test
    @DisplayName("Game handles drawing a quest card")
    public void RESP_9_test_DrawQuestCard() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        String input = "no\nyes\n";
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        QuestCard questCard = new QuestCard(3); // Quest with 3 stages
        game.handleQuestCard(questCard, scanner);

        assertTrue(game.isSponsorshipOffered(), "Sponsorship should be offered to players.");
    }

    @Test
    @DisplayName("Check for Winners after each turn")
    public void RESP_10_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        player1.addShields(6);
        player2.addShields(7);

        game.checkForWinners();

        // Since we already have a test for displaying winners (RESP-5), we can assume it works
        // For this test, we can check if the game recognizes that the game should end
        assertTrue(game.isGameOver(), "Game should be over if a player has 7 or more shields.");
    }

    @Test
    @DisplayName("Test if hand is being trimmed properly")
    public void RESP_11_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();

        Player player = game.getPlayers().get(0);

        // Simulate player having more than 12 cards
        for (int i = 0; i < 14; i++) {
            Card card = game.getAdventureDeck().drawCard();
            player.addCardToHand(card);
        }

        assertTrue(player.getHandSize() > 12, "Player should have more than 12 cards.");

        // Simulate player input to discard cards (e.g., discard positions 13,12,11)
        String input = "13\n12\n11\n";
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        game.trimPlayerHand(player, scanner);

        // Restore original System.in
        System.setIn(stdin);

        assertEquals(12, player.getHandSize(), "Player's hand should be trimmed to 12 cards.");
    }
    @Test
    @DisplayName("Game offers quest sponsorship to players in order")
    public void RESP_12_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.initializeTurnOrder();

        // Simulate players' responses: P1 declines, P2 accepts
        String input = "no\nyes\n";
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        QuestCard questCard = new QuestCard(3);
        game.handleQuestCard(questCard, scanner);


        // Restore original System.in
        System.setIn(stdin);

        // Verify that P2 is the sponsor
        assertEquals("P2", game.getSponsor().getId(), "Player P2 should be the sponsor.");
    }

    @Test
    public void RESP_13_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.initializeTurnOrder();

        Player sponsor = game.getPlayers().get(0);
        game.setSponsor(sponsor);

        game.determineEligibleParticipants();

        List<Player> participants = game.getEligibleParticipants();
        assertEquals(3, participants.size(), "There should be 3 eligible participants.");
        for (Player participant : participants) {
            assertNotEquals(sponsor.getId(), participant.getId(), "Sponsor should not be in the participants list.");
        }
    }

    @Test
    @DisplayName("Participants wanting to withdraw")
    public void RESP_17_test_ParticipantDecision() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();

        // Assume sponsor is P1
        Player sponsor = game.getPlayers().getFirst();
        game.setSponsor(sponsor);
        game.determineEligibleParticipants();

        // Simulate participants' decisions: P2 participates, P3 withdraws, P4 participates
        String input = "yes\nno\nyes\n";
        InputStream stdin = System.in;
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        game.promptParticipantsForStage(scanner);

        System.setIn(stdin);

        List<Player> participants = game.getStageParticipants();
        assertEquals(2, participants.size(), "There should be 2 participants for the stage.");
        assertTrue(participants.contains(game.getPlayers().get(1)), "P2 should be participating.");
        assertTrue(participants.contains(game.getPlayers().get(3)), "P4 should be participating.");
    }

    @Test
    @DisplayName("Participants Draw Cards and Trim Hand if Necessary")
    public void RESP_18_test_01() {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();

        Player participant1 = game.getPlayers().get(1);
        Player participant2 = game.getPlayers().get(3);

        game.AddStageParticipants(participant1);
        game.AddStageParticipants(participant2);

        assertEquals(12, participant1.getHandSize(), "Participant 1 should start with 12 cards.");
        assertEquals(12, participant2.getHandSize(), "Participant 2 should start with 12 cards.");

        participant1.addCardToHand(game.getAdventureDeck().drawCard());
        participant2.addCardToHand(game.getAdventureDeck().drawCard());

        assertEquals(13, participant1.getHandSize(), "Participant 1 should have 13 cards after drawing.");
        assertEquals(13, participant2.getHandSize(), "Participant 2 should have 13 cards after drawing.");

        // Use mock input for trimming cards
        String input = "13\n12\n13\n12\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // Call handleParticipantsDrawingAndTrimming with mock scanner
        game.handleParticipantsDrawingAndTrimming(scanner);

        assertEquals(12, participant1.getHandSize(), "Participant 1 should have 12 cards after trimming.");
        assertEquals(12, participant2.getHandSize(), "Participant 2 should have 12 cards after trimming.");
    }



}
