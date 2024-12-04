package org.example;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:8081") // Adjusted origin
public class GameController {
    private Game game = Game.getInstance();
    private boolean isGameInitialized = false;
    // Add a field to track if the game thread is running
    private Thread gameThread;


    @GetMapping("/initialize")
    public ResponseEntity<String> initializeGame(){
        game.setupDecks();
        game.initializePlayers();
        game.dealCardsToPlayers();
        game.initializeTurnOrder();
        isGameInitialized = true;
        return ResponseEntity.ok("Game initialized");
    }
    @GetMapping("/isGameInitialized")
    public ResponseEntity<Boolean> isGameInitialized() {
        return ResponseEntity.ok(isGameInitialized);
    }


    // Modify the startGame method
    @GetMapping("/start")
    public ResponseEntity<String> startGame() {
        if (isGameInitialized) {


            // Start the game loop in a new thread
            gameThread = new Thread(() -> {
                while (!game.isGameOver()) {
                    game.playTurn();
//                    game.checkForWinners();
//                    if (!game.isGameOver()) {
//                        game.endTurn();
//                    }
                }
                game.addLog("Game Over!");
            });
            gameThread.start();
            return ResponseEntity.ok("Game started");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game is already in progress!");
    }


    // Endpoint to get game logs
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        List<String> logs = game.getLogs();
        return ResponseEntity.ok(logs);
    }
    // Endpoint to check if the game is waiting for input
    @GetMapping("/isWaitingForInput")
    public ResponseEntity<Boolean> isWaitingForInput() {
        return ResponseEntity.ok(game.isWaitingForInput());
    }


    // Endpoint to send user input
    @PostMapping("/input")
    public ResponseEntity<String> handleUserInput(@RequestBody String userInput) {
        game.processUserInput(userInput);
        return ResponseEntity.ok("Input received");
    }


    // Endpoint to check if game is over
    @GetMapping("/isGameOver")
    public ResponseEntity<Boolean> isGameOver() {
        return ResponseEntity.ok(game.isGameOver());
    }


    @GetMapping("/players-info")
    public ResponseEntity<List<Map<String, Object>>> getPlayersInfo() {
        List<Map<String, Object>> playersInfo = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("id", player.getId());
            playerInfo.put("shields", player.getShields());

            // Get detailed information about the cards in the player's hand
            List<Map<String, Object>> cardDetails = player.getHandDetails();
            playerInfo.put("cards", cardDetails);

            playersInfo.add(playerInfo);
        }

        return ResponseEntity.ok(playersInfo);
    }

    @PostMapping("/rig/adventureDeck")
    public ResponseEntity<String> rigAdventureDeck(@RequestBody Integer winner) {
        if (winner == 0) {
            game.rigAdventureDeck0();
            return ResponseEntity.ok("Adventure deck rigged for 0 winners");

        } else if (winner == 1) {
            game.rigAdventureDeck1();
            return ResponseEntity.ok("Adventure deck rigged for 1 winner");

        } else if (winner == 2) {
            game.rigAdventureDeck2();
            return ResponseEntity.ok("Adventure deck rigged for 2 winners");

        } else if (winner == 3) {
            game.rigAdventureDeck3();
            return ResponseEntity.ok("Adventure deck rigged for A1");

        }

        return ResponseEntity.ok("Adventure deck rigged");
    }

    @PostMapping("/rig/eventDeck")
    public ResponseEntity<String> rigEventDeck(@RequestBody Integer winner) {
        if (winner == 0) {
            game.rigEventDeck0();
            return ResponseEntity.ok("Event deck rigged for 0 winners");

        } else if (winner == 1) {
            game.rigEventDeck1();
            return ResponseEntity.ok("Event deck rigged for 1 winner");

        } else if (winner == 2) {
            game.rigEventDeck2();
            return ResponseEntity.ok("Event deck rigged for 2 winners");

        } else if (winner == 3) {
            game.rigEventDeck3();
            return ResponseEntity.ok("Event deck rigged for A1");

        }
        return ResponseEntity.ok("Event deck rigged");
    }

    @PostMapping("/rig/playerHands")
    public ResponseEntity<List<Map<String, Object>>> rigPlayerHands(@RequestBody Integer winner) {
        if (winner == 0) { game.rigPlayersHands0(); }
        else if (winner == 1) { game.rigPlayersHands1(); }
        else if (winner == 2) { game.rigPlayersHands2(); }
        else if (winner == 3) { game.rigPlayersHands3(); }

        List<Map<String, Object>> playersInfo = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("id", player.getId());

            List<String> cardNames = new ArrayList<>();
            for (Card card : player.getHand()) {
                cardNames.add(card.getName());
            }
            playerInfo.put("cards", cardNames);

            playersInfo.add(playerInfo);
        }

        return ResponseEntity.ok(playersInfo);
    }


}
