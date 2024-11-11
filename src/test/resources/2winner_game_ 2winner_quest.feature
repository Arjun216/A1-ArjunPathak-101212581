Feature: Quest with multiple stages and winners

  Scenario: Two players win the game after multiple quests
    Given a new game is started
    And each player is dealt 12 cards
    And the adventure deck is rigged
    And the event deck is rigged
    And the players' hands are rigged
    And Player 1 is offered sponsorship and accepts
    And Players 2, 3, and 4 decide to participate
    And Players 2, 3, and 4 complete the first Quest
    Then Players 2 and 4 should have won Quest 1
    And Player 3 should have lost Quest 1
    Then Players 2 and 4 should each have 4 shields
    When Player 2 draws a 3-stage quest and declines to sponsor it
    And Player 3 decides to sponsor the quest and builds its stages
    And Player 1 declines to participate and Players 2 and 4 participate
    And Players 2 and 4 play and win stages 1, 2, and 3
    Then Players 2 and 4 should each earn 3 shields
    And Players 2 and 4 are declared winners
