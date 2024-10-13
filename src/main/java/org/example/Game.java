package org.example;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Deck adventureDeck;
    private Deck eventDeck;

    private List<Card> adventureDiscardPile = new ArrayList<>();
    private List<Card> eventDiscardPile = new ArrayList<>();


    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver;

    //RESP-01
    public void setupDecks() {
        adventureDeck = new Deck();
        eventDeck = new Deck();

        adventureDeck.addCards(CardDealer.createAdventureCards());
        eventDeck.addCards(CardDealer.createEventCards());
    }

    public Deck getAdventureDeck() {
        return adventureDeck;
    }

    public Deck getEventDeck() {
        return eventDeck;
    }

    // RESP-02
    public void initializePlayers() {
        players = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            players.add(new Player("P" + i));
        }
    }

    public void dealCardsToPlayers() {
        for (Player player : players) {
            for (int i = 0; i < 12; i++) {
                Card card = adventureDeck.drawCard();
                player.addCardToHand(card);
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void initializeTurnOrder() {
        currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void playTurn() {
        Player player = getCurrentPlayer();
        drawEventCard(player);

    }
    public void checkForWinners() {
        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.getShields() >= 7) {
                winners.add(player);
            }
        }
        if (!winners.isEmpty()) {
            displayWinners(winners);
            gameOver = true;
        }
    }

    private void displayWinners(List<Player> winners) {
        System.out.print("Winner: ");
        for (int i = 0; i < winners.size(); i++) {
            System.out.print(winners.get(i).getId());
            if (i < winners.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    public EventCard drawEventCard(Player player) {
        Card card = eventDeck.drawCard();
        if (card instanceof EventCard eventCard) {
            System.out.println("Player " + player.getId() + " drew event card: " + eventCard.getEventName());
            return eventCard;
        } else if (card instanceof QuestCard questCard) {
            System.out.println("Player " + player.getId() + " drew quest card with " + questCard.getStages() + " stages.");
            return null; // to return something in RESP9
        } else {
            return null;
        }
    }

    // Main game loop
    public void startGame() {
        initializeTurnOrder();
        while (!gameOver) {
            playTurn();
            if (!gameOver) {
                advanceTurn();
            }
        }
        System.out.println("Game over!");
    }
}
