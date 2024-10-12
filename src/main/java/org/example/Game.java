package org.example;

import java.util.List;

public class Game {
    private Deck adventureDeck;
    private Deck eventDeck;
    private List<Player> players;

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

    }

    public void dealCardsToPlayers(){

    }

    public List<Player> getPlayers(){
        return null;
    }
}
