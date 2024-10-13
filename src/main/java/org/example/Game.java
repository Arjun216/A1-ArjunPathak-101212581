package org.example;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Deck adventureDeck;
    private Deck eventDeck;

    private List<Card> adventureDiscardPile = new ArrayList<>();
    private List<EventCard> eventDiscardPile = new ArrayList<>();


    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver;
    private boolean sponsorshipOffered;

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

    public void setEventDeck(Deck deck) {
        this.eventDeck = deck;
    }

    public void handleEventCard(EventCard eventCard, Player currentPlayer) {
        String eventName = eventCard.getEventName();
        switch (eventName) {
            case "Plague":
                int shieldsLost = Math.min(2, currentPlayer.getShields());
                currentPlayer.addShields(-shieldsLost);
                break;
            case "Queen's Favor":
                for (int i = 0; i < 2; i++) {
                    Card card = adventureDeck.drawCard();
                    currentPlayer.addCardToHand(card);
                }
                if (currentPlayer.getHandSize() > 12) {
                    trimPlayerHand(currentPlayer);
                }
                break;
            case "Prosperity":
                for (Player player : players) {
                    for (int i = 0; i < 2; i++) {
                        Card card = adventureDeck.drawCard();
                        player.addCardToHand(card);
                    }
                    if (player.getHandSize() > 12) {
                        trimPlayerHand(player);
                    }
                }
                break;
            default:

        }
        eventDiscardPile.add(eventCard); // Add to discard pile
    }

    private void trimPlayerHand(Player player) {

    }

    public List<EventCard> getEventDiscardPile() {
        return eventDiscardPile;
    }

    public void endTurn() {
        advanceTurn();
    }

    public void handleQuestCard(QuestCard questCard) {
        System.out.println("Quest card drawn with " + questCard.getStages() + " stages.");
        offerSponsorship();
    }

    public void offerSponsorship() {
        sponsorshipOffered = true;
    }

    public boolean isSponsorshipOffered() {
        return sponsorshipOffered;
    }

    public boolean isGameOver() {
        return false;
    }
}
