Feature: Single winner game with events

  Scenario: 1winner_game_with_events
    Given a new1 game is started with 4 players
    And the decks1 are created
    And the players'1 hands are rigged with the specified initial cards

    When P1 draws a 4-stage quest and decides to sponsor it
    And P1 builds the 4 stages of the quest
    And P2, P3, and P4 participate and win all stages
    Then P2, P3, and P4 each earn 4 shields

    When P2 draws the "Plague" event card
    Then P2 loses 2 shields

    When P3 draws the "Prosperity" event card
    Then all players each receive 2 adventure cards

    When P4 draws the "Queen’s Favor" event card
    Then P4 draws 2 adventure cards

    When P1 draws a 3-stage quest and decides to sponsor it
    And P1 builds the 3 stages of the quest
    And P2, P3, and P4 participate in stage 1
    When P2 and P3 win Quest 1
    Then P2 and P3 each earn 3 shields
    And P3 is declared the winner
