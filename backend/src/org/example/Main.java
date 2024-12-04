package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = Game.getInstance();
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();

        while (!game.isGameOver()) {
            Player currentPlayer = game.getCurrentPlayer();
            System.out.println("\nIt's " + currentPlayer.getId() + "'s turn.");
            game.addLog("\nIt's " + currentPlayer.getId() + "'s turn.");
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

