package org.example;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
}
