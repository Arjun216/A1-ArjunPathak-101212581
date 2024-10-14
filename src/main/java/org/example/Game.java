package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Deck adventureDeck;
    private Deck eventDeck;

    private List<Card> eventDiscardPile = new ArrayList<>();


    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver = false;
    private boolean sponsorshipOffered;
    private Player sponsor;


    private List<Player> eligibleParticipants = new ArrayList<>();
    private List<Player> stageParticipants = new ArrayList<>();
    private List<Player> winners = new ArrayList<>();
    private List<Stage> questStages = new ArrayList<>();



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

    public void trimPlayerHand(Player player) {
        Scanner scanner = new Scanner(System.in);
        trimPlayerHand(player, scanner);
    }

    void trimPlayerHand(Player player, Scanner scanner) {
        int excessCards = player.getHandSize() - 12;
        System.out.println("You have " + player.getHandSize() + " cards. Please discard " + excessCards + " card(s).");

        for (int i = 0; i < excessCards; i++) {
            player.displayHand();

            System.out.print("Enter the position of the card to discard: ");
            int position = scanner.nextInt();
            while (position < 1 || position > player.getHandSize()) {
                System.out.println("Invalid position. Please try again.");
                position = scanner.nextInt();
            }
            player.discardCard(position - 1);
        }
    }

    public List<Card> getEventDiscardPile() {
        return eventDiscardPile;
    }

    public void endTurn() {
        advanceTurn();
    }

    public void handleQuestCard(QuestCard questCard) {
        System.out.println("Quest card drawn with " + questCard.getStages() + " stages.");
        Scanner scanner = new Scanner(System.in);
        offerSponsorship(questCard, scanner);
    }
    public void handleQuestCard(QuestCard questCard, Scanner scanner) {
        System.out.println("Quest card drawn with " + questCard.getStages() + " stages.");
        offerSponsorship(questCard, scanner);
    }


    public void offerSponsorship(QuestCard questCard, Scanner scanner) {
        sponsorshipOffered = true;
        int index = currentPlayerIndex;
        int attempts = 0;
        sponsor = null;

        while (attempts < players.size()) {
            Player player = players.get(index);
            if (askForSponsorship(player, scanner)) {
                sponsor = player;
                System.out.println(player.getId() + " is the sponsor for this quest.");
                break;
            }
            index = (index + 1) % players.size();
            attempts++;
        }

        if (sponsor == null) {
            System.out.println("No sponsor found. The quest is discarded.");
            eventDiscardPile.add(questCard);
            // End current player's turn
            endTurn();
        } else {
            // Proceed to UC-05: Sponsor sets up the quest
        }
    }

    private boolean askForSponsorship(Player player, Scanner scanner) {
        System.out.println(player.getId() + ", do you want to sponsor the quest? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();
        while (!response.equals("yes") && !response.equals("no")) {
            System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            response = scanner.nextLine().trim().toLowerCase();
        }
        return response.equals("yes");
    }

    public Player getSponsor() {
        return sponsor;
    }

    public boolean isSponsorshipOffered() {
        return sponsorshipOffered;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    public void determineEligibleParticipants() {
        eligibleParticipants.clear();
        for (Player player : players) {
            if (!player.equals(sponsor)) {
                eligibleParticipants.add(player);
            }
        }
    }

    public List<Player> getEligibleParticipants() {
        return eligibleParticipants;
    }

    public void setSponsor(Player sponsor) {
        this.sponsor = sponsor;
    }

    public void promptParticipantsForStage() {
        Scanner scanner = new Scanner(System.in);
        promptParticipantsForStage(scanner);
    }

    public void promptParticipantsForStage(Scanner scanner) {
        stageParticipants.clear();
        Iterator<Player> iterator = eligibleParticipants.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            System.out.println(player.getId() + ", do you want to participate in this stage? (yes/no)");
            String response = scanner.nextLine().trim().toLowerCase();
            while (!response.equals("yes") && !response.equals("no")) {
                System.out.println("Invalid response. Please enter 'yes' or 'no'.");
                response = scanner.nextLine().trim().toLowerCase();
            }
            if (response.equals("yes")) {
                stageParticipants.add(player);
            } else {
                iterator.remove();
            }
        }
    }

    public List<Player> getStageParticipants() {
        return stageParticipants;
    }
    public void AddStageParticipants(Player player){
        stageParticipants.add(player);
        
    }

    public void handleParticipantsDrawingAndTrimming(Scanner scanner) {

        for (Player participant : stageParticipants) {
            Card card = adventureDeck.drawCard();
            participant.addCardToHand(card);
            System.out.println(participant.getId() + " draws a card.");

            if (participant.getHandSize() > 12) {
                System.out.println(participant.getId() + " has more than 12 cards and needs to trim their hand.");
                trimPlayerHand(participant, scanner);
            }
        }
    }

    public void resolveStage(int stageValue) {
        Iterator<Player> iterator = stageParticipants.iterator();
        while (iterator.hasNext()) {
            Player participant = iterator.next();
            int attackValue = participant.getAttackValue();
            if (attackValue >= stageValue) {
                System.out.println(participant.getId() + " has passed the stage.");
                // Participant remains eligible
            } else {
                System.out.println(participant.getId() + " has failed the stage.");
                iterator.remove(); // Remove from stage participants
                eligibleParticipants.remove(participant); // Remove from eligible participants
            }
        }
    }

    public void addWinner(Player participant) {
        winners.add(participant);
    }

    public void awardShieldsToWinners(int questStages) {
        for (Player winner : winners) {
            winner.addShields(questStages);
            System.out.println(winner.getId() + " receives " + questStages + " shields.");
        }
    }

    public List<Player> getWinners() {
        return winners;
    }
    public void sponsorDrawsReplacementCards(int cardsUsed, int questStages) {
        Scanner scanner = new Scanner(System.in);
        sponsorDrawsReplacementCards(cardsUsed, questStages, scanner);
    }

    public void sponsorDrawsReplacementCards(int cardsUsed, int questStages, Scanner scanner) {
        for (int i = 0; i < cardsUsed + questStages; i++) {
            Card card = adventureDeck.drawCard();
            sponsor.addCardToHand(card);
        }
        if (sponsor.getHandSize() > 12) {
            System.out.println(sponsor.getId() + " has more than 12 cards and needs to trim their hand.");
            trimPlayerHand(sponsor, scanner);
        }

    }

    public void sponsorSetsUpQuest(int numStages) {
        Scanner scanner = new Scanner(System.in);
        sponsorSetsUpQuest(numStages, scanner);
    }

    public void sponsorSetsUpQuest(int numStages, Scanner scanner) {
        System.out.println("Sponsor " + sponsor.getId() + " is setting up the quest with " + numStages + " stages.\n");

        int previousStageValue = 0;

        for (int stageNumber = 1; stageNumber <= numStages; stageNumber++) {
            Stage stage = new Stage();
            System.out.println("Setting up Stage " + stageNumber + ":");

            boolean stageComplete = false;
            while (!stageComplete) {
                sponsor.displayHand();
                System.out.print("Enter the position of the card to add to this stage or 'quit' to finish the stage: \n");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("quit")) {
                    // Validation before completing the stage
                    if (stage.isEmpty()) {
                        System.out.println("A stage cannot be empty. Please add at least one Foe card and any desired Weapon cards.\n");
                    } else if (stage.getTotalValue() <= previousStageValue) {
                        System.out.println("Stage value must be greater than the value of the previous stage (" + previousStageValue + "). Add more cards or modify the stage.");
                    } else {
                        // Successfully complete the current stage
                        previousStageValue = stage.getTotalValue();
                        System.out.println("Stage " + stageNumber + " set up with total value: " + previousStageValue);
                        questStages.add(stage);
                        stageComplete = true;
                    }
                } else {
                    try {
                        int position = Integer.parseInt(input);
                        if (position < 1 || position > sponsor.getHandSize()) {
                            System.out.println("Invalid position. Please enter a value between 1 and " + sponsor.getHandSize() + ".");
                        } else {
                            Card selectedCard = sponsor.getHand().get(position - 1);
                            if (stage.canAddCard(selectedCard)) {
                                stage.addCard(selectedCard);
                                sponsor.removeCardFromHand(position - 1);
                                System.out.println("Card added to stage.");
                            } else {
                                System.out.println("Invalid card selection. Please select a non-repeated weapon or a single foe.");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid card position or 'quit'.");
                    }
                }
            }
        }

        // Ensure the correct number of stages were set up
        if (questStages.size() != numStages) {
            System.out.println("Warning: Only " + questStages.size() + " stages were successfully set up out of " + numStages + ".");
        }
    }



    public List<Stage> getQuestStages() {
        return questStages;
    }

    public void participantSetsUpAttack(Player participant, Scanner scanner) {
    }
}
