package org.example;

import io.cucumber.java.an.E;

import java.util.*;

public class Game {
    private static Game instance;
    //Game game = this.game;
    private Deck adventureDeck;
    private Deck eventDeck;

    private final List<Card> eventDiscardPile = new ArrayList<>();

    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver = false;
    private boolean sponsorshipOffered;
    private Player sponsor;

    private final List<Player> eligibleParticipants = new ArrayList<>();
    private final List<Player> stageParticipants = new ArrayList<>();
    private final List<Player> winners = new ArrayList<>();
    private final List<Stage> questStages = new ArrayList<>();
    private final List<String> logs = new ArrayList<>();
    private final Queue<String> userInputs = new LinkedList<>();
    private final List<String> input_string = new ArrayList<>();
    private boolean waitingForInput = false;

    public boolean isRigged() {
        return isRigged;
    }

    private boolean isRigged = false;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }


    public synchronized void processUserInput(String input) {
        userInputs.add(input);
        input_string.add(input);
        System.out.println(input_string);
        notifyAll();
    }

    private synchronized String getNextUserInput() {
        while (userInputs.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        String input = userInputs.poll();
        System.out.println(input);
        return input;
    }


    public void addLog(String message) {
        System.out.println(message);
        logs.add(message);
    }

    // Function to retrieve all logs
    public List<String> getLogs() {
        return logs;
    }

    public void setupDecks() {
        adventureDeck = new Deck();
        eventDeck = new Deck();

        adventureDeck.addCards(CardDealer.createAdventureCards());
        eventDeck.addCards(CardDealer.createEventCards());

        adventureDeck.shuffle();
        eventDeck.shuffle();
    }

    public Deck getAdventureDeck() {
        return adventureDeck;
    }


    // RESP-02
    public void initializePlayers() {
        players = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            players.add(new Player(("P" + i)));
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
        Card drawnCard = eventDeck.drawCard();
        if (drawnCard instanceof EventCard eventCard) {
            addLog(player.getId() + " drew an Event Card: " + eventCard.getEventName());
            handleEventCard(eventCard, player);
            endTurn();
            eventDiscardPile.add(eventCard);
        } else if (drawnCard instanceof QuestCard questCard) {
            addLog(player.getId() + " drew a Quest Card with " + questCard.getStages() + " stages.");
            handleQuestCard(questCard);
            sponsorSetsUpQuest(questCard.getStages());
            determineEligibleParticipants();
            promptParticipantsForStage();
            if (sponsor != null) {
                playTurns();
            }

            //endTurn();
        } else {
            addLog("Unknown card type drawn.");
            endTurn();
        }
    }

    public void checkForWinners() {
        winners.clear();
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
        addLog("Winner: ");
        for (int i = 0; i < winners.size(); i++) {
            addLog(winners.get(i).getId());
            if (i < winners.size() - 1) {
                addLog(", ");
            }
        }
    }

    public EventCard drawEventCard(Player player) {
        Card card = eventDeck.drawCard();
        if (card instanceof EventCard eventCard) {
            addLog("Player " + player.getId() + " drew event card: " + eventCard.getEventName());
            return eventCard;
        } else if (card instanceof QuestCard questCard) {
            addLog("Player " + player.getId() + " drew quest card with " + questCard.getStages() + " stages.");
            return null;
        } else {
            addLog("Unknown card");
            return null;
        }
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
                addLog(currentPlayer.getId() + " loses " + shieldsLost + " shields. Total shields: " + currentPlayer.getShields());
                break;
            case "Queen's Favor":
                for (int i = 0; i < 2; i++) {
                    Card card = adventureDeck.drawCard();
                    currentPlayer.addCardToHand(card);
                }
                addLog(currentPlayer.getId() + " draws 2 adventure cards.");
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
                    addLog(player.getId() + " draws 2 adventure cards.");
                    if (player.getHandSize() > 12) {
                        trimPlayerHand(player);
                    }
                }
                break;
            default:
                addLog("Unknown event card: " + eventName);
        }
        eventDiscardPile.add(eventCard); // Add to discard pile
    }

    public void trimPlayerHand(Player player) {
        int excessCards = player.getHandSize() - 12;
        addLog("You have " + player.getHandSize() + " cards. Please discard " + excessCards + " card(s).");

        for (int i = 0; i < excessCards; i++) {
            player.displayHand();
            addLog("Enter the position of the card to discard: ");
            waitingForInput = true;
            String input = getNextUserInput();
            waitingForInput = false;
            if (input == null || input.isEmpty()) {
                addLog("No input provided. Skipping discard.");
                break;
            }
            while (!input.matches("\\d+")) {
                addLog("Invalid input. Please enter a valid card position.");
                waitingForInput = true;
                input = getNextUserInput();
                waitingForInput = false;
                if (input == null) {
                    addLog("No input provided. Skipping discard.");
                    break;
                }
            }
            if (input == null || input.equalsIgnoreCase("quit") || input.isEmpty()) {
                addLog("No input provided. Skipping discard.");
                continue;
            }
            int position = Integer.parseInt(input);
            while (position < 1 || position > player.getHandSize()) {
                addLog("Invalid position. Please try again.");
                waitingForInput = true;
                input = getNextUserInput();
                waitingForInput = false;

                if (input == null) {
                    addLog("No input provided. Skipping discard.");
                    break;
                }
                if (input.isEmpty()) {
                    addLog("No input provided. Skipping discard.");
                    break;
                }
                position = Integer.parseInt(input);
            }
            if (position >= 1 && position <= player.getHandSize()) {
                player.discardCard(position - 1);
            } else {
                addLog("Discard skipped due to invalid input.");
            }
        }
    }

    public List<Card> getEventDiscardPile() {
        return eventDiscardPile;
    }

    public void endTurn() {
        advanceTurn();
    }

    public void handleQuestCard(QuestCard questCard) {
        addLog("Quest card drawn with " + questCard.getStages() + " stages.");
        offerSponsorship(questCard);
    }

    public void playTurns() {
        boolean questFailed = false;

        for (Stage stage : questStages) {
            if (stageParticipants.isEmpty()) {
                addLog("No participants remain. The quest ends.");
                questFailed = true;
                break;
            }

            handleParticipantsDrawingAndTrimming();

            for (Player participant : stageParticipants) {
                participantSetsUpAttack(participant);
            }

            resolveStage(stage.getTotalValue());

            if (stageParticipants.isEmpty()) {
                addLog("All participants have failed the quest.");
                questFailed = true;
                break;
            }

            Iterator<Player> iterator = stageParticipants.iterator();
            while (iterator.hasNext()) {
                Player participant = iterator.next();
                addLog(participant.getId() + ", do you want to continue to the next stage? (yes/no)");

                waitingForInput = true;
                String response = getNextUserInput();
                waitingForInput = false;


                if (response == null) {
                    addLog("No input provided. Assuming 'no'.");
                    iterator.remove();
                    eligibleParticipants.remove(participant);
                    continue;
                }

                response = response.trim().toLowerCase();
                while (!response.equals("yes") && !response.equals("no")) {
                    addLog("Invalid response. Please enter 'yes' or 'no'.");
                    waitingForInput = true;
                    response = getNextUserInput();
                    waitingForInput = false;


                    if (response == null) {
                        addLog("No input provided. Assuming 'no'.");
                        response = "no";
                        break;
                    }
                    response = response.trim().toLowerCase();
                }
                if (response.equals("no")) {
                    iterator.remove();
                    eligibleParticipants.remove(participant);
                    addLog(participant.getId() + " has opted out of the quest.");
                }
            }
        }

        if (!questFailed && !stageParticipants.isEmpty()) {
            awardShieldsToWinners(stageParticipants, questStages.size());
        }
        replenishSponsorHand(sponsor);

        questStages.clear();
        checkForWinners();
        advanceTurn();
    }


    public void offerSponsorship(QuestCard questCard) {
        sponsorshipOffered = true;
        int index = currentPlayerIndex;
        int attempts = 0;
        sponsor = null;


        while (attempts < players.size()) {
            Player player = players.get(index);
            if (askForSponsorship(player)) {
                sponsor = player;
                addLog(player.getId() + " is the sponsor for this quest.");
                sponsor.setInitialHandSize(sponsor.getHandSize());
                break;
            }
            index = (index + 1) % players.size();
            attempts++;
        }

        if (sponsor == null) {
            addLog("No sponsor found. The quest is discarded.");
            eventDiscardPile.add(questCard);
        }

    }


    private boolean askForSponsorship(Player player) {
        addLog(player.getId() + ", do you want to sponsor the quest? (yes/no)");
        waitingForInput = true;
        String response = getNextUserInput();
        waitingForInput = false;


        if (response == null) {
            addLog("No input provided. Assuming 'no'.");
            return false;
        }
        response = response.trim().toLowerCase();
        while (!response.equals("yes") && !response.equals("no")) {
            addLog("Invalid response. Please enter 'yes' or 'no'.");
            waitingForInput = true;
            response = getNextUserInput();
            waitingForInput = false;

            if (response == null) {
                addLog("No input provided. Assuming 'no'.");
                return false;
            }
            response = response.trim().toLowerCase();
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
        stageParticipants.clear();
        Iterator<Player> iterator = eligibleParticipants.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            addLog(player.getId() + ", do you want to participate in this stage? (yes/no)");
            waitingForInput = true;
            String input = getNextUserInput();
            waitingForInput = false;


            if (input == null) {
                addLog("No input provided. Assuming 'no'.");
                iterator.remove();
                continue;
            }
            input = input.trim().toLowerCase();
            while (!input.equals("yes") && !input.equals("no")) {
                addLog("Invalid response. Please enter 'yes' or 'no'.");
                waitingForInput = true;
                input = getNextUserInput();
                waitingForInput = false;


                if (input == null) {
                    addLog("No input provided. Assuming 'no'.");
                    continue;
                }
            }
            if (input.equals("yes")) {
                stageParticipants.add(player);
            } else {
                iterator.remove();
            }
        }
    }

    public List<Player> getStageParticipants() {
        return stageParticipants;
    }

    public void AddStageParticipants(Player player) {
        stageParticipants.add(player);
    }

    public void handleParticipantsDrawingAndTrimming() {
        for (Player participant : stageParticipants) {
            Card card = adventureDeck.drawCard();
            participant.addCardToHand(card);
            addLog(participant.getId() + " draws a card." + card.getName());

            if (participant.getHandSize() > 12) {
                addLog(participant.getId() + " has more than 12 cards and needs to trim their hand.");
                trimPlayerHand(participant);
            }
        }
    }

    public void resolveStage(int stageValue) {
        Iterator<Player> iterator = stageParticipants.iterator();
        while (iterator.hasNext()) {
            Player participant = iterator.next();
            int attackValue = participant.getAttackValue();
            if (attackValue >= stageValue) {
                addLog(participant.getId() + " has passed the stage.");
                // Participant remains eligible
            } else {
                addLog(participant.getId() + " has failed the stage." + participant.getAttackValue());
                iterator.remove(); // Remove from stage participants
                eligibleParticipants.remove(participant); // Remove from eligible participants
            }
        }
    }

    public void addWinner(Player participant) {
        winners.add(participant);
        stageParticipants.add(participant);
    }

    public void awardShieldsToWinners(List<Player> successfulParticipants, int questStages) {
        for (Player participant : successfulParticipants) {
            participant.addShields(questStages);
            addLog(participant.getId() + " receives " + questStages + " shields.");
        }
    }

    public List<Player> getWinners() {
        return winners;
    }

    public void sponsorDrawsReplacementCards(int cardsUsed, int questStages) {
        for (int i = 0; i < cardsUsed + questStages; i++) {
            Card card = adventureDeck.drawCard();
            sponsor.addCardToHand(card);
        }
        if (sponsor.getHandSize() > 12) {
            addLog(sponsor.getId() + " has more than 12 cards and needs to trim their hand.");
            trimPlayerHand(sponsor);
        }
    }

    public void sponsorSetsUpQuest(int numStages) {
        addLog("Sponsor " + sponsor.getId() + " is setting up the quest with " + numStages + " stages.\n");

        int previousStageValue = 0;

        for (int stageNumber = 1; stageNumber <= numStages; stageNumber++) {
            Stage stage = new Stage();
            addLog("Setting up Stage " + stageNumber + ":");
            boolean stageComplete = false;
            while (!stageComplete) {
                sponsor.displayHand();
                addLog("Enter the position of the card to add to this stage or 'quit' to finish the stage: ");

                waitingForInput = true;
                String input = getNextUserInput();
                waitingForInput = false;

                if (input == null) {
                    addLog("No input provided. Skipping to next stage.");
                    break;
                }

                if (input.equalsIgnoreCase("quit")) {
                    if (stage.isValid(previousStageValue)) {
                        previousStageValue = stage.getTotalValue();
                        questStages.add(stage);
                        addLog("Stage " + stageNumber + " set up with total value: " + previousStageValue);
                        stageComplete = true;
                    } else {
                        // Inform sponsor of specific issues
                        if (!stage.hasFoeCard()) {
                            addLog("Stage must contain exactly one Foe card.");
                        }
                        if (stage.getTotalValue() <= previousStageValue) {
                            addLog("Stage value must be strictly greater than the previous stage's value (" + previousStageValue + ").");
                        }
                        addLog("Please adjust the stage accordingly.");
                    }
                } else {
                    try {
                        int position = Integer.parseInt(input);
                        if (position < 1 || position > sponsor.getHandSize()) {
                            addLog("Invalid position. Please enter a value between 1 and " + sponsor.getHandSize() + ".");
                        } else {
                            Card selectedCard = sponsor.getHand().get(position - 1);
                            if (stage.canAddCard(selectedCard)) {
                                stage.addCard(selectedCard);
                                sponsor.removeCardFromHand(position - 1);
                                addLog("Card " + selectedCard.getName() + " added to stage.");
                            } else {
                                addLog("Invalid card selection. Please select a non-repeated weapon or a single foe.");
                            }
                        }
                    } catch (NumberFormatException e) {
                        addLog("Invalid input. Please enter a valid card position or 'quit'.");
                    }
                }
            }
        }

        // Ensure the correct number of stages were set up
        if (questStages.size() != numStages) {
            addLog("Warning: Only " + questStages.size() + " stages were successfully set up out of " + numStages + ".");
        }
    }


    public List<Stage> getQuestStages() {
        return questStages;
    }

    public void participantSetsUpAttack(Player participant) {
        int attackValue = 0;
        addLog(participant.getId() + ", set up your attack for this stage:");
        participant.displayHand();

        List<Card> attackCards = new ArrayList<>();
        String input;
        while (true) {
            addLog("Enter the position of the card to add to your attack or 'quit' to finish: ");

            waitingForInput = true;
            input = getNextUserInput();
            waitingForInput = false;

            if (input == null || input.equalsIgnoreCase("quit")) {
                break;
            }
            int position;
            try {
                position = Integer.parseInt(input);
                if (position >= 1 && position <= participant.getHandSize()) {
                    Card selectedCard = participant.getHand().get(position - 1);
                    attackCards.add(selectedCard);
                    participant.getHand().remove(selectedCard); // Remove card from hand
                    addLog("Added " + selectedCard.getName() + " to your attack.");
                    attackValue = attackValue + selectedCard.getValue();
                } else {
                    addLog("Invalid position.");
                }
            } catch (NumberFormatException e) {
                addLog("Invalid input.");
            }
        }
        participant.setAttackValue(attackValue);
        participant.setCurrentAttack(attackCards);
    }

    public void replenishSponsorHand(Player sponsor) {
        int cardsUsed = sponsor.getInitialHandSize() - sponsor.getHandSize();
        System.out.println("Cards Used: " + cardsUsed + "Init:" + sponsor.getInitialHandSize() + "getHand:" + sponsor.getHandSize());
        int cardsToDraw = cardsUsed + questStages.size();
        System.out.println("Cards to draw" + cardsToDraw);
//        for (int i = 1; i < sponsor.getHandSize(); i++) {
//            sponsor.discardCard(i);
//        }

        for (int i = 0; i < cardsToDraw; i++) {
            Card card = adventureDeck.drawCard();
            if (card != null) {
                sponsor.addCardToHand(card);
                addLog(sponsor.getId() + " draws a replacement card: " + card.getName());
            } else {
                addLog(sponsor.getId() + " could not draw a replacement card because the deck is empty.");
                break;
            }
        }
        if (sponsor.getHandSize() > 12) {
            trimPlayerHand(sponsor);
        }
    }


    public Boolean isWaitingForInput() {
        return waitingForInput;
    }

    public Deck getEventDeck() {
        return eventDeck;
    }

    //2 winner scenario
    void rigEventDeck2() {

        eventDeck.getCards().clear();
        eventDeck.getCards().add(0, new QuestCard(4));
        eventDeck.getCards().add(1, new QuestCard(3));
        eventDeck.getCards().add(2, new EventCard("Plague"));
        System.out.println(getEventDeck());
    }

    void rigAdventureDeck2() {// Clear existing cards
        adventureDeck.getCards().clear();

        List<Card> additionalCards = new ArrayList<>();

        // Add cards in the exact order they will be drawn
        List<Card> riggedCards = Arrays.asList(
                // Stage 1 Draws
                new FoeCard(5),                 // P1 draws
                new FoeCard(40),
                new FoeCard(10),

                // Stage 2 Draws
                new FoeCard(10),
                new FoeCard(30),


                // Stage 3 Draws
                new FoeCard(30),
                new FoeCard(15),


                // Stage 4 Draws
                new FoeCard(15),
                new FoeCard(20),

                //P1 replacement cards
                new FoeCard(5),
                new FoeCard(10),
                new FoeCard(15),
                new FoeCard(15),
                new FoeCard(20),
                new FoeCard(20),
                new FoeCard(20),
                new FoeCard(20),
                new FoeCard(25),
                new FoeCard(25),
                new FoeCard(30),
                new WeaponCard("Dagger", 5),
                new WeaponCard("Dagger", 5),
                new FoeCard(15),
                new FoeCard(15),
                new FoeCard(25),
                new FoeCard(25),
                new FoeCard(20),
                new FoeCard(20),
                new FoeCard(25),
                new FoeCard(30),
                new WeaponCard("Sword", 10),
                new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Lance", 20)

        );

        // Add all rigged cards to the deck in the order they will be drawn
        adventureDeck.getCards().addAll(riggedCards);
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                additionalCards.add(new WeaponCard("Sword", 10));
                additionalCards.add(new FoeCard(10));
            }
            additionalCards.add(new FoeCard(10));
            additionalCards.add(new WeaponCard("Sword", 10));
        }
        adventureDeck.getCards().addAll(additionalCards);
    }

    void rigPlayersHands2() {
        // Rig the hands for each player as specified
        List<Card> p1Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5),  // 2xF5
                new FoeCard(10), new FoeCard(10),  // 2xF10
                new FoeCard(15), new FoeCard(15),  // 2xF15
                new WeaponCard("Dagger", 5),       // 1 dagger
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),  // 2 horses
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15),  // 2 axes
                new WeaponCard("Lance", 20)       // 1 lance
        );

        // P2 Initial Hand: 1xF40, 1xF50, 2 horses, 3 swords, 2 axes, 2 lances, 1 excalibur
        List<Card> p2Hand = Arrays.asList(
                new FoeCard(40),  // 1xF40
                new FoeCard(50),  // 1xF50
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),  // 2 horses
                new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),  // 3 swords
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15),  // 2 axes
                new WeaponCard("Lance", 20), new WeaponCard("Lance", 20),  // 2 lances
                new WeaponCard("Excalibur", 30)  // 1 excalibur
        );

// P3 Initial Hand: 4xF5, 3 daggers, 5 horses
        List<Card> p3Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5), new FoeCard(5), new FoeCard(5),  // 4xF5
                new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5),  // 3 daggers
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10)  // 5 horses
        );

// P4 Initial Hand: 1xF50, 1xF70, 2 horses, 3 swords, 2 axes, 2 lances, 1 excalibur
        List<Card> p4Hand = Arrays.asList(
                new FoeCard(50),  // 1xF50
                new FoeCard(70),  // 1xF70
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),  // 2 horses
                new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),  // 3 swords
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15),  // 2 axes
                new WeaponCard("Lance", 20), new WeaponCard("Lance", 20),  // 2 lances
                new WeaponCard("Excalibur", 30)  // 1 excalibur
        );


        // Assign the rigged hands to the players
        getPlayers().get(0).setHand(new ArrayList<>(p1Hand)); // P1
        getPlayers().get(1).setHand(new ArrayList<>(p2Hand)); // P2
        getPlayers().get(2).setHand(new ArrayList<>(p3Hand)); // P3
        getPlayers().get(3).setHand(new ArrayList<>(p4Hand)); // P4
        isRigged = true;
    }

    //1 winner scenario
    void rigEventDeck1() {

        eventDeck.getCards().clear();
        eventDeck.getCards().add(0, new QuestCard(4));
        eventDeck.getCards().add(1, new EventCard("Plague"));
        eventDeck.getCards().add(2, new EventCard("Prosperity"));
        eventDeck.getCards().add(3, new EventCard("Queen's Favor"));
        eventDeck.getCards().add(4, new QuestCard(3));


        System.out.println(getEventDeck());
    }

    void rigAdventureDeck1() {// Clear existing cards
        adventureDeck.getCards().clear();

        List<Card> additionalCards = new ArrayList<>();

        // Add cards in the exact order they will be drawn
        List<Card> riggedCards = Arrays.asList(
                // Stage 1 Draws
                new FoeCard(5),
                new FoeCard(10),
                new FoeCard(20),

                // Stage 2 Draws
                new FoeCard(15),
                new FoeCard(5),
                new FoeCard(25),

                // Stage 3 Draws
                new FoeCard(5),
                new FoeCard(10),
                new FoeCard(20),

                // Stage 4 Draws
                new FoeCard(5),
                new FoeCard(10),
                new FoeCard(20),

                //P1 replacement cards
                new FoeCard(5),
                new FoeCard(5),
                new FoeCard(10),
                new FoeCard(10),
                new FoeCard(15),
                new FoeCard(15),
                new FoeCard(15),
                new FoeCard(15),

                //after prosperity
                new FoeCard(25),
                new FoeCard(25),
                new WeaponCard("Horse", 10),
                new WeaponCard("Sword", 10),
                new WeaponCard("Battle-Axe", 15),
                new FoeCard(40),
                new WeaponCard("Dagger", 5),
                new WeaponCard("Dagger", 5),

                //Queens favor
                new FoeCard(30),
                new FoeCard(25),

                //Stage 1
                new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Horse", 10),
                new FoeCard(50),

                //Stage 2
                new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10),

                //Stage 3
                new FoeCard(40),
                new FoeCard(50),

                //P1 replenish
                new WeaponCard("Horse", 10),
                new WeaponCard("Horse", 10),
                new WeaponCard("Horse", 10),

                new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10),

                new FoeCard(35)


        );

        // Add all rigged cards to the deck in the order they will be drawn
        adventureDeck.getCards().addAll(riggedCards);
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                additionalCards.add(new WeaponCard("Sword", 10));
                additionalCards.add(new FoeCard(10));
            }
            additionalCards.add(new FoeCard(10));
            additionalCards.add(new WeaponCard("Sword", 10));
        }
        adventureDeck.getCards().addAll(additionalCards);
    }

    void rigPlayersHands1() {
        // P1 initial hand: 2xF5, 2xF10, 2xF15, 2xF20, 4 daggers
        List<Card> p1Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5),        // 2xF5
                new FoeCard(10), new FoeCard(10),      // 2xF10
                new FoeCard(15), new FoeCard(15),      // 2xF15
                new FoeCard(20), new FoeCard(20),      // 2xF20
                new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5),
                new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5)  // 4 daggers
        );

        // P2 initial hand: 1xF25, 1xF30, 2 horses, 3 swords, 2 axes, 2 lances, 1 excalibur
        List<Card> p2Hand = Arrays.asList(
                new FoeCard(25),          // 1xF25
                new FoeCard(30),          // 1xF30
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), // 2 horses
                new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), // 3 swords
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15), // 2 axes
                new WeaponCard("Lance", 20), new WeaponCard("Lance", 20), // 2 lances
                new WeaponCard("Excalibur", 30)  // 1 excalibur
        );

        // P3 initial hand: Same as P2
        List<Card> p3Hand = Arrays.asList(
                new FoeCard(25),          // 1xF25
                new FoeCard(30),          // 1xF30
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), // 2 horses
                new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), // 3 swords
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15), // 2 axes
                new WeaponCard("Lance", 20), new WeaponCard("Lance", 20), // 2 lances
                new WeaponCard("Excalibur", 30)  // 1 excalibur
        );

        // P4 initial hand: 1xF25, 1xF30, 1xF70, 2 horses, 3 swords, 2 axes, 2 lances
        List<Card> p4Hand = Arrays.asList(
                new FoeCard(25),          // 1xF25
                new FoeCard(30),          // 1xF30
                new FoeCard(70),          // 1xF70
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), // 2 horses
                new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), // 3 swords
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15), // 2 axes
                new WeaponCard("Lance", 20), new WeaponCard("Lance", 20)  // 2 lances
        );

        // Assign the rigged hands to the players
        getPlayers().get(0).setHand(new ArrayList<>(p1Hand)); // P1
        getPlayers().get(1).setHand(new ArrayList<>(p2Hand)); // P2
        getPlayers().get(2).setHand(new ArrayList<>(p3Hand)); // P3
        getPlayers().get(3).setHand(new ArrayList<>(p4Hand)); // P4
        isRigged = true;
    }

    //0 winner scenario
    void rigEventDeck0() {

        eventDeck.getCards().clear();
        eventDeck.getCards().add(0, new QuestCard(2));
        System.out.println(getEventDeck());
    }

    void rigAdventureDeck0() {// Clear existing cards
        adventureDeck.getCards().clear();

        List<Card> additionalCards = new ArrayList<>();

        // Add cards in the exact order they will be drawn
        List<Card> riggedCards = Arrays.asList(
                // Stage 1 Draws
                new FoeCard(5),
                new FoeCard(15),
                new FoeCard(10),

                //P1 draws
                new FoeCard(5),
                new FoeCard(10),
                new FoeCard(15),
                new WeaponCard("Dagger", 5),
                new WeaponCard("Dagger", 5),
                new WeaponCard("Dagger", 5),
                new WeaponCard("Dagger", 5),
                new WeaponCard("Horse", 10),
                new WeaponCard("Horse", 10),
                new WeaponCard("Horse", 10),
                new WeaponCard("Horse", 10),
                new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10)

        );

        // Add all rigged cards to the deck in the order they will be drawn
        adventureDeck.getCards().addAll(riggedCards);
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                additionalCards.add(new WeaponCard("Sword", 10));
                additionalCards.add(new FoeCard(10));
            }
            additionalCards.add(new FoeCard(10));
            additionalCards.add(new WeaponCard("Sword", 10));
        }
        adventureDeck.getCards().addAll(additionalCards);
    }

    void rigPlayersHands0() {
        // P1: 1xF50, 1xF70, 2 daggers, 2 horses, 2 swords, 2 axes, 2 lances
        List<Card> p1Hand = Arrays.asList(
                new FoeCard(50),                     // 1xF50
                new FoeCard(70),                     // 1xF70
                new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5), // 2 daggers
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), // 2 horses
                new WeaponCard("Sword", 10), new WeaponCard("Sword", 10), // 2 swords
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Battle-Axe", 15), // 2 axes
                new WeaponCard("Lance", 20), new WeaponCard("Lance", 20)  // 2 lances
        );

        // P2: 2xF5, 1xF10, 2xF15, 2xF20, 1xF25, 2xF30, 1xF40, 1 excalibur
        List<Card> p2Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5),      // 2xF5
                new FoeCard(10),                     // 1xF10
                new FoeCard(15), new FoeCard(15),    // 2xF15
                new FoeCard(20), new FoeCard(20),    // 2xF20
                new FoeCard(25),                     // 1xF25
                new FoeCard(30), new FoeCard(30),    // 2xF30
                new FoeCard(40),                     // 1xF40
                new WeaponCard("Excalibur", 30)      // 1 excalibur
        );

        // P3: 2xF5, 1xF10, 2xF15, 2xF20, 2xF25, 1xF30, 1xF40, 1 lance
        List<Card> p3Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5),      // 2xF5
                new FoeCard(10),                     // 1xF10
                new FoeCard(15), new FoeCard(15),    // 2xF15
                new FoeCard(20), new FoeCard(20),    // 2xF20
                new FoeCard(25), new FoeCard(25),    // 2xF25
                new FoeCard(30),                     // 1xF30
                new FoeCard(40),                     // 1xF40
                new WeaponCard("Lance", 20)          // 1 lance
        );

        // P4: 2xF5, 1xF10, 2xF15, 2xF20, 2xF25, 1xF30, 1xF50, 1 excalibur
        List<Card> p4Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5),      // 2xF5
                new FoeCard(10),                     // 1xF10
                new FoeCard(15), new FoeCard(15),    // 2xF15
                new FoeCard(20), new FoeCard(20),    // 2xF20
                new FoeCard(25), new FoeCard(25),    // 2xF25
                new FoeCard(30),                     // 1xF30
                new FoeCard(50),                     // 1xF50
                new WeaponCard("Excalibur", 30)      // 1 excalibur
        );

        // Assign the rigged hands to the players
        getPlayers().get(0).setHand(new ArrayList<>(p1Hand)); // P1
        getPlayers().get(1).setHand(new ArrayList<>(p2Hand)); // P2
        getPlayers().get(2).setHand(new ArrayList<>(p3Hand)); // P3
        getPlayers().get(3).setHand(new ArrayList<>(p4Hand)); // P4
        isRigged = true;
    }

    //A1 Scenario
    void rigEventDeck3() {

        eventDeck.getCards().clear();
        eventDeck.getCards().add(0, new QuestCard(4));
        eventDeck.getCards().add(1, new EventCard("Plague"));
        eventDeck.getCards().add(2, new QuestCard(3));
    }

    void rigAdventureDeck3() {// Clear existing cards
        adventureDeck.getCards().clear();

        List<Card> additionalCards = new ArrayList<>();

        // Add cards in the exact order they will be drawn
        List<Card> riggedCards = Arrays.asList(
                new FoeCard(30),                 // P1 draws
                new WeaponCard("Sword", 10),     // P3 draws
                new WeaponCard("Battle-Axe", 15), // P4 draws

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
            if (i % 2 == 0) {
                additionalCards.add(new WeaponCard("Sword", 10));
                additionalCards.add(new FoeCard(10));
            }
            additionalCards.add(new FoeCard(10));
            additionalCards.add(new WeaponCard("Sword", 10));
        }
        adventureDeck.getCards().addAll(additionalCards);
    }

    void rigPlayersHands3() {
        List<Card> p1Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5), new FoeCard(15), new FoeCard(15),
                new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Lance", 20)
        );
        List<Card> p2Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5), new FoeCard(15), new FoeCard(15),
                new FoeCard(40), new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Excalibur", 30)
        );
        List<Card> p3Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(5), new FoeCard(5), new FoeCard(15),
                new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10), new WeaponCard("Sword", 10),
                new WeaponCard("Sword", 10), new WeaponCard("Horse", 10), new WeaponCard("Horse", 10),
                new WeaponCard("Battle-Axe", 15), new WeaponCard("Lance", 20)
        );
        List<Card> p4Hand = Arrays.asList(
                new FoeCard(5), new FoeCard(15), new FoeCard(15), new FoeCard(40),
                new WeaponCard("Dagger", 5), new WeaponCard("Dagger", 5), new WeaponCard("Sword", 10),
                new WeaponCard("Horse", 10), new WeaponCard("Horse", 10), new WeaponCard("Battle-Axe", 15),
                new WeaponCard("Lance", 20), new WeaponCard("Excalibur", 30)
        );

        // Assign the rigged hands to the players
        getPlayers().get(0).setHand(new ArrayList<>(p1Hand)); // P1
        getPlayers().get(1).setHand(new ArrayList<>(p2Hand)); // P2
        getPlayers().get(2).setHand(new ArrayList<>(p3Hand)); // P3
        getPlayers().get(3).setHand(new ArrayList<>(p4Hand)); // P4
    }
}
