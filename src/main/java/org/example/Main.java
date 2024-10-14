package org.example;
public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        while (!game.isGameOver()) {
            Player currentPlayer = game.getCurrentPlayer();
            System.out.println("\nIt's " + currentPlayer.getId() + "'s turn.");
            currentPlayer.displayHand();

            game.playTurn();

            // Check for winners after the turn
            game.checkForWinners();

            if (!game.isGameOver()) {
                game.endTurn();
            }
        }

        System.out.println("Game Over!");
    }
}

