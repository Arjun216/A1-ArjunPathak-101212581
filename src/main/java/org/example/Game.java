package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Deck adventureDeck;
    private Deck eventDeck;

    private List<Card> eventDiscardPile = new ArrayList<>();
    private List<Card> adventureDiscardPile = new ArrayList<>();

    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver = false;
    private boolean sponsorshipOffered;
    private Player sponsor;

    private List<Player> eligibleParticipants = new ArrayList<>();
    private List<Player> stageParticipants = new ArrayList<>();
    private List<Player> winners = new ArrayList<>();
    private List<Stage> questStages = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);

    // RESP-01
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

    public void playTurn(Scanner scanner) {
        Player player = getCurrentPlayer();
        Card drawnCard = eventDeck.drawCard();
        if (drawnCard instanceof EventCard eventCard) {
            System.out.println(player.getId() + " drew an Event Card: " + eventCard.getEventName());
            handleEventCard(eventCard, player, scanner);
            endTurn();
            eventDiscardPile.add(eventCard);
        } else if (drawnCard instanceof QuestCard questCard) {
            System.out.println(player.getId() + " drew a Quest Card with " + questCard.getStages() + " stages.");
            handleQuestCard(questCard, scanner);
            endTurn();
        } else {
            System.out.println("Unknown card type drawn.");
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
        System.out.print("Winner: ");
        for (int i = 0; i < winners.size(); i++) {
            System.out.print(winners.get(i).getId());
            if (i < winners.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public EventCard drawEventCard(Player player) {
        Card card = eventDeck.drawCard();
        if (card instanceof EventCard eventCard) {
            System.out.println("Player " + player.getId() + " drew event card: " + eventCard.getEventName());
            return eventCard;
        } else if (card instanceof QuestCard questCard) {
            System.out.println("Player " + player.getId() + " drew quest card with " + questCard.getStages() + " stages.");
            return null;
        } else {
            System.out.println("Unknown card");
            return null;
        }
    }

    public void setEventDeck(Deck deck) {
        this.eventDeck = deck;
    }

    public void handleEventCard(EventCard eventCard, Player currentPlayer, Scanner scanner) {
        String eventName = eventCard.getEventName();
        switch (eventName) {
            case "Plague":
                int shieldsLost = Math.min(2, currentPlayer.getShields());
                currentPlayer.addShields(-shieldsLost);
                System.out.println(currentPlayer.getId() + " loses " + shieldsLost + " shields. Total shields: " + currentPlayer.getShields());
                break;
            case "Queen's Favor":
                for (int i = 0; i < 2; i++) {
                    Card card = adventureDeck.drawCard();
                    currentPlayer.addCardToHand(card);
                }
                System.out.println(currentPlayer.getId() + " draws 2 adventure cards.");
                if (currentPlayer.getHandSize() > 12) {
                    trimPlayerHand(currentPlayer, scanner);
                }
                break;
            case "Prosperity":
                for (Player player : players) {
                    for (int i = 0; i < 2; i++) {
                        Card card = adventureDeck.drawCard();
                        player.addCardToHand(card);
                    }
                    System.out.println(player.getId() + " draws 2 adventure cards.");
                    if (player.getHandSize() > 12) {
                        trimPlayerHand(player, scanner);
                    }
                }
                break;
            default:
                System.out.println("Unknown event card: " + eventName);
        }
        eventDiscardPile.add(eventCard); // Add to discard pile
    }

    public void trimPlayerHand(Player player, Scanner scanner) {
        int excessCards = player.getHandSize() - 12;
        System.out.println("You have " + player.getHandSize() + " cards. Please discard " + excessCards + " card(s).");

        for (int i = 0; i < excessCards; i++) {
            player.displayHand();
            System.out.println("Enter the position of the card to discard: ");
            if (!scanner.hasNextLine()) {
                System.out.println("No input provided. Skipping discard.");
                break;
            }
            String input = scanner.nextLine().trim();
            while (!input.matches("\\d+")) {
                System.out.println("Invalid input. Please enter a valid card position.");
                if (!scanner.hasNextLine()) {
                    System.out.println("No input provided. Skipping discard.");
                    break;
                }
                input = scanner.nextLine().trim();
            }
            if (input.equals("")) {
                System.out.println("No input provided. Skipping discard.");
                continue;
            }
            int position = Integer.parseInt(input);
            while (position < 1 || position > player.getHandSize()) {
                System.out.println("Invalid position. Please try again.");
                if (!scanner.hasNextLine()) {
                    System.out.println("No input provided. Skipping discard.");
                    break;
                }
                input = scanner.nextLine().trim();
                if (input.equals("")) {
                    System.out.println("No input provided. Skipping discard.");
                    break;
                }
                position = Integer.parseInt(input);
            }
            if (position >= 1 && position <= player.getHandSize()) {
                player.discardCard(position - 1);
            } else {
                System.out.println("Discard skipped due to invalid input.");
            }
        }
    }

    public List<Card> getEventDiscardPile() {
        return eventDiscardPile;
    }

    public void endTurn() {
        advanceTurn();
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
                sponsor.setInitialHandSize(sponsor.getHandSize());
                break;
            }
            index = (index + 1) % players.size();
            attempts++;
        }
        if (sponsor == null) {
            System.out.println("No sponsor found. The quest is discarded.");
            eventDiscardPile.add(questCard);
        } else {
            sponsorSetsUpQuest(questCard.getStages(), scanner);
            determineEligibleParticipants();
            promptParticipantsForStage(scanner);

            boolean questFailed = false;

            for (Stage stage : questStages) {
                if (stageParticipants.isEmpty()) {
                    System.out.println("No participants remain. The quest ends.");
                    questFailed = true;
                    break;
                }

                handleParticipantsDrawingAndTrimming(scanner);

                for (Player participant : stageParticipants) {
                    participantSetsUpAttack(participant, scanner);
                }

                resolveStage(stage.getTotalValue());

                if (stageParticipants.isEmpty()) {
                    System.out.println("All participants have failed the quest.");
                    questFailed = true;
                    break;
                }
                Iterator<Player> iterator = stageParticipants.iterator();
                while (iterator.hasNext()) {
                    Player participant = iterator.next();
                    System.out.println(participant.getId() + ", do you want to continue to the next stage? (yes/no)");
                    if (!scanner.hasNextLine()) {
                        System.out.println("No input provided. Assuming 'no'.");
                        iterator.remove();
                        eligibleParticipants.remove(participant);
                        continue;
                    }
                    String response = scanner.nextLine().trim().toLowerCase();
                    while (!response.equals("yes") && !response.equals("no")) {
                        System.out.println("Invalid response. Please enter 'yes' or 'no'.");
                        if (!scanner.hasNextLine()) {
                            System.out.println("No input provided. Assuming 'no'.");
                            response = "no";
                            break;
                        }
                        response = scanner.nextLine().trim().toLowerCase();
                    }
                    if (response.equals("no")) {
                        iterator.remove();
                        eligibleParticipants.remove(participant);
                        System.out.println(participant.getId() + " has opted out of the quest.");
                    }
                }
            }

            if (!questFailed && !stageParticipants.isEmpty()) {
                awardShieldsToWinners(stageParticipants, questStages.size());
            }
            replenishSponsorHand(sponsor, scanner);

            questStages.clear();
            checkForWinners();
        }
    }


    private boolean askForSponsorship(Player player, Scanner scanner) {
        System.out.println(player.getId() + ", do you want to sponsor the quest? (yes/no)");
        if (!scanner.hasNextLine()) {
            System.out.println("No input provided. Assuming 'no'.");
            return false;
        }
        String response = scanner.nextLine().trim().toLowerCase();
        while (!response.equals("yes") && !response.equals("no")) {
            System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            if (!scanner.hasNextLine()) {
                System.out.println("No input provided. Assuming 'no'.");
                return false;
            }
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

    public void promptParticipantsForStage(Scanner scanner) {
        stageParticipants.clear();
        Iterator<Player> iterator = eligibleParticipants.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            System.out.println(player.getId() + ", do you want to participate in this stage? (yes/no)");
            if (!scanner.hasNextLine()) {
                System.out.println("No input provided. Assuming 'no'.");
                continue;
            }
            String response = scanner.nextLine().trim().toLowerCase();
            while (!response.equals("yes") && !response.equals("no")) {
                System.out.println("Invalid response. Please enter 'yes' or 'no'.");
                if (!scanner.hasNextLine()) {
                    System.out.println("No input provided. Assuming 'no'.");
                    break;
                }
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

    public void AddStageParticipants(Player player) {
        stageParticipants.add(player);
    }

    public void handleParticipantsDrawingAndTrimming(Scanner scanner) {
        for (Player participant : stageParticipants) {
            Card card = adventureDeck.drawCard();
            participant.addCardToHand(card);
            System.out.println(participant.getId() + " draws a card." + card.getName());

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
                System.out.println(participant.getId() + " has failed the stage." + participant.getAttackValue());
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
            System.out.println(participant.getId() + " receives " + questStages + " shields.");
        }
    }

    public List<Player> getWinners() {
        return winners;
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

    public void sponsorSetsUpQuest(int numStages, Scanner scanner) {
        System.out.println("Sponsor " + sponsor.getId() + " is setting up the quest with " + numStages + " stages.\n");

        int previousStageValue = 0;

        for (int stageNumber = 1; stageNumber <= numStages; stageNumber++) {
            Stage stage = new Stage();
            System.out.println("Setting up Stage " + stageNumber + ":");

            boolean stageComplete = false;
            while (!stageComplete) {
                sponsor.displayHand();
                System.out.println("Enter the position of the card to add to this stage or 'quit' to finish the stage: ");

                if (!scanner.hasNextLine()) {
                    System.out.println("No input provided. Skipping to next stage.");
                    break;
                }
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("quit")) {
                    if (stage.isValid(previousStageValue)) {
                        previousStageValue = stage.getTotalValue();
                        questStages.add(stage);
                        System.out.println("Stage " + stageNumber + " set up with total value: " + previousStageValue);
                        stageComplete = true;
                    } else {
                        // Inform sponsor of specific issues
                        if (!stage.hasFoeCard()) {
                            System.out.println("Stage must contain exactly one Foe card.");
                        }
                        if (stage.getTotalValue() <= previousStageValue) {
                            System.out.println("Stage value must be strictly greater than the previous stage's value (" + previousStageValue + ").");
                        }
                        System.out.println("Please adjust the stage accordingly.");
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
                                System.out.println("Card " + selectedCard.getName() + " added to stage.");
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
        int attackValue = 0;
        System.out.println(participant.getId() + ", set up your attack for this stage:");
        participant.displayHand();

        List<Card> attackCards = new ArrayList<>();
        String input;
        while (true) {
            System.out.print("Enter the position of the card to add to your attack or 'quit' to finish: ");
            input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quit")) {
                break;
            }
            int position;
            try {
                position = Integer.parseInt(input);
                if (position >= 1 && position <= participant.getHandSize()) {
                    Card selectedCard = participant.getHand().get(position - 1);
                    attackCards.add(selectedCard);
                    participant.getHand().remove(selectedCard); // Remove card from hand
                    System.out.println("Added " + selectedCard.getName() + " to your attack.");
                    attackValue = attackValue + selectedCard.getValue();
                } else {
                    System.out.println("Invalid position.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        participant.setAttackValue(attackValue);
        participant.setCurrentAttack(attackCards);
    }

    public void replenishSponsorHand(Player sponsor, Scanner scanner) {
        int cardsUsed = sponsor.getInitialHandSize() - sponsor.getHandSize();
        int cardsToDraw = cardsUsed + 2;
        for (int i = 1; i < sponsor.getHandSize(); i++){
            sponsor.discardCard(i);
        }

        for (int i = 0; i < cardsToDraw; i++) {
            Card card = adventureDeck.drawCard();
            if (card != null) {
                sponsor.addCardToHand(card);
                System.out.println(sponsor.getId() + " draws a replacement card: " + card.getName());
            } else {
                System.out.println(sponsor.getId() + " could not draw a replacement card because the deck is empty.");
                break;
            }
        }
        if(sponsor.getHandSize()  > 12) {
            trimPlayerHand(sponsor, scanner);
        }
    }



}
