package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        Scanner scanner = new Scanner((System.in));
        while (!game.isGameOver()) {
            Player currentPlayer = game.getCurrentPlayer();
            System.out.println("\nIt's " + currentPlayer.getId() + "'s turn.");
            currentPlayer.displayHand();

            game.playTurn(scanner);

            // Check for winners after the turn
            game.checkForWinners();

            if (!game.isGameOver()) {
                game.endTurn();
            }
        }

        System.out.println("Game Over!");
    }
}

