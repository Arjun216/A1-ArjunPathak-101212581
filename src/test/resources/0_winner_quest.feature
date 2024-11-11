Feature: Quest with no winner

  Scenario: 0_winner_quest
    Given a new game0 is started with 4 players
    And the decks0 are created
    And the players'0 hands are rigged with the specified initial cards

    When P1 draws a 2-stage quest and decides to sponsor it
    And P1 builds the 2 stages of the quest
    And P2, P3, and P4 participate0 in stage 1
    Then P2, P3, and P4 all lose stage 1 and cannot proceed

    When the quest ends
    Then there is no winner
    And P1 discards all cards used in the quest and draws new cards to refill their hand
