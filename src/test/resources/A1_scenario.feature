Feature: Quest with multiple stages and winners

  Scenario: Compulsory scenario A-TEST JP-Scenario
    Given a new game is started with 4 players
    And the decks are created
    And the players' hands are rigged with the specified initial cards
    When P1 draws a quest card of 4 stages
    And P1 declines to sponsor the quest
    And P2 accepts to sponsor the quest and builds the 4 stages as specified
    And Quest 1 begins with P1, P3, and P4 participating
    And Player 1 and 3 got out in Quest1
    Then P3 has 0 shields and P4 has 4 shields
    And P2 has 12 cards in hand