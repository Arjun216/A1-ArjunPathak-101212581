const { Builder, By, until } = require('selenium-webdriver');
const fetch = require('node-fetch');
require('chromedriver');
const chrome = require('selenium-webdriver/chrome');

(async function testWithRigging() {
    let driver;

    try {
        const options = new chrome.Options();

        // Shutdown and reinitialize the game
        shutdownGame();
        await initializeGame();

        // Rig game configurations
        await rigAll();

        // Start the browser driver
        driver = await new Builder()
            .forBrowser('chrome')
            .setChromeOptions(options)
            .build();

        // Navigate to the game frontend
        await driver.get('http://127.0.0.1:8081');

        // Wait for the page to load
        await driver.wait(until.titleIs('Card Game'), 10000);

        // Start the game
        await startGame(driver);

        // Simulate user inputs
        await simulateUserInputs(driver);

        console.log('Test scenario executed successfully.');

        // Shutdown the game
        await shutdownGame();
    } catch (error) {
        console.error('An error occurred during the test:', error);
    } finally {
        if (driver) {
            await driver.quit();
        }
    }
})();

// Utility Functions
async function initializeGame() {
    const response = await fetch('http://localhost:8080/api/initialize', { method: 'GET' });
    const text = await response.text();

    if (!response.ok) {
        console.error('Response from initializeGame:', text);
        throw new Error('Failed to initialize game.');
    }

    console.log('Game initialized successfully:', text);
}

async function rigAll() {
    try {
        await Promise.all([rigAdventureDeck(), rigEventDeck(), rigPlayerHands()]);
        console.log('All rigging functions executed successfully.');
    } catch (error) {
        console.error('Error during rigging:', error);
        throw error;
    }
}

async function rigAdventureDeck() {
    const response = await fetch('http://localhost:8080/api/rig/adventureDeck', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(2),
    });
    if (!response.ok) throw new Error('Failed to rig Adventure Deck.');
    console.log('Adventure Deck rigged successfully.');
}

async function rigEventDeck() {
    const response = await fetch('http://localhost:8080/api/rig/eventDeck', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(2),
    });
    if (!response.ok) throw new Error('Failed to rig Event Deck.');
    console.log('Event Deck rigged successfully.');
}

async function rigPlayerHands() {
    const response = await fetch('http://localhost:8080/api/rig/playerHands', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(2),
    });
    if (!response.ok) throw new Error('Failed to rig Player Hands.');
    console.log('Player Hands rigged successfully.');
}

async function startGame(driver) {
    const startButton = await driver.findElement(By.id('startGameButton'));
    if (startButton) {
        await startButton.click();
        console.log('Game started successfully.');
    } else {
        throw new Error('Start Game button not found.');
    }
    await driver.sleep(200);
}

async function waitForGameToBeReady() {
    for (let i = 0; i < 20; i++) {
        const response = await fetch('http://localhost:8080/api/isWaitingForInput');
        const isWaiting = await response.json();
        if (isWaiting) return;
        await new Promise(resolve => setTimeout(resolve, 1000));
    }
    throw new Error('Game did not become ready for input within expected time.');
}

// Simulate User Inputs
async function simulateUserInputs(driver) {
    const inputs = [
        'yes', ' 1', ' quit', ' 1', ' 5', ' quit', ' 1', ' 4', ' quit', ' 1', ' 4', ' quit', ' yes', ' yes', ' yes', ' 1',
        ' 1', ' 1', ' 7', ' quit', ' quit', ' 7', ' quit', ' yes', ' yes', ' 4', ' quit', ' 4', ' quit', ' yes', ' yes',
        ' 6', ' 6', ' quit', ' 6', ' 6', ' quit', ' yes', ' yes', ' 6', ' 7', ' quit', ' 6', ' 7', ' quit', ' yes', ' yes',
        ' 1', ' 1', ' 1', ' 1', ' no', ' yes', ' 1', ' quit', ' 1', ' 3', ' quit', ' 1', ' 4', ' quit', ' no', ' yes', ' yes',
        ' 6', ' quit', ' 6', ' quit', ' yes', ' yes', ' 7', ' quit', ' 7', ' quit', ' yes', ' yes', ' 10', ' quit', ' 10', ' quit',
        ' yes', ' yes', ' 1', ' 2', ' 2'
    ];

    for (let index = 0; index < inputs.length; index++) {
        const input = inputs[index];
        await waitForGameToBeReady();

        const inputField = await driver.findElement(By.id('inputField'));
        const sendButton = await driver.findElement(By.id('sendButton'));

        await inputField.clear();
        await inputField.sendKeys(input);
        await new Promise(resolve => setTimeout(resolve, 700));
        await sendButton.click();
        await new Promise(resolve => setTimeout(resolve, 300));

        if (index === 0) await assertSponsor('P1');
        if (index === 12) {
            const expectedStages = [
                { totalValue: 5, cards: ['F5'] },
                { totalValue: 10, cards: ['F5', 'Dagger'] },
                { totalValue: 20, cards: ['F10', 'Horse'] },
                { totalValue: 25, cards: ['F10', 'Battle-Axe'] }
            ];
            await assertQuestStage(expectedStages);
        }
        if (index === 51) {
            const expectedShields = { P1: 0, P2: 4, P3: 0, P4: 4 };
            await assertPlayerShields(expectedShields);
        }
        if (index === 53) await assertSponsor('P3');
        if (index === 61) {
            const expectedStages = [
                { totalValue: 5, cards: ['F5'] },
                { totalValue: 10, cards: ['F5', 'Dagger'] },
                { totalValue: 15, cards: ['F5', 'Horse'] }
            ];
            await assertQuestStage(expectedStages);
        }
    }

    const expectedHands = {
        P1: ['F15', 'F15', 'F20', 'F20', 'F20', 'F20', 'F25', 'F25', 'F30', 'Horse', 'Battle-Axe', 'Lance'],
        P2: ['F10', 'F15', 'F15', 'F25', 'F30', 'F40', 'F50', 'Lance', 'Lance'],
        P3: ['F20', 'F40', 'Dagger', 'Dagger', 'Sword', 'Horse', 'Horse', 'Horse', 'Horse', 'Battle-Axe', 'Battle-Axe', 'Lance'],
        P4: ['F15', 'F15', 'F20', 'F25', 'F30', 'F50', 'F70', 'Lance', 'Lance']
    };
    await assertPlayerHands(expectedHands);

    await assertWinners(['P2', 'P4']);
}

// Assertion Functions
async function shutdownGame() {
    const response = await fetch('http://localhost:8080/api/shutdown', { method: 'POST', headers: { 'Content-Type': 'application/json' } });
    if (!response.ok) throw new Error('Failed to shut down the game.');
    console.log('Game shut down successfully.');
}

async function assertSponsor(expectedSponsorId) {
    const response = await fetch('http://localhost:8080/api/sponsor');
    const sponsorId = await response.text();
    console.assert(sponsorId === expectedSponsorId, 'Wrong sponsor');
}

async function assertQuestStage(expectedStages) {
    const response = await fetch('http://localhost:8080/api/quest-stages');
    const stages = await response.json();

    console.assert(stages.length === expectedStages.length, `Expected ${expectedStages.length} stages, got ${stages.length}`);
    stages.forEach((stage, index) => {
        console.assert(stage.totalValue === expectedStages[index].totalValue,
            `Stage ${index + 1}: Expected ${expectedStages[index].totalValue}, got ${stage.totalValue}`);
        console.assert(JSON.stringify(stage.cards) === JSON.stringify(expectedStages[index].cards),
            `Stage ${index + 1}: Expected cards ${JSON.stringify(expectedStages[index].cards)}, got ${JSON.stringify(stage.cards)}`);
    });

    console.log('Quest stage assertion successful.');
}

async function assertPlayerHands(expectedHands) {
    const response = await fetch('http://localhost:8080/api/players-info');
    const players = await response.json();

    for (const playerId in expectedHands) {
        const player = players.find(p => p.id === playerId);
        if (!player) throw new Error(`Player ${playerId} not found`);

        const actualCards = player.cards.map(card => card.name);
        const expectedCards = expectedHands[playerId];

        console.assert(
            JSON.stringify(actualCards) === JSON.stringify(expectedCards),
            `Player ${playerId}: Expected ${JSON.stringify(expectedCards)}, got ${JSON.stringify(actualCards)}`
        );
    }
}

async function assertPlayerShields(expectedShields) {
    const response = await fetch('http://localhost:8080/api/players-info');
    const players = await response.json();

    for (const playerId in expectedShields) {
        const player = players.find(p => p.id === playerId);
        if (!player) throw new Error(`Player ${playerId} not found`);

        const actualShields = player.shields;
        const expectedShieldCount = expectedShields[playerId];

        console.assert(actualShields === expectedShieldCount,
            `Player ${playerId}: Expected ${expectedShieldCount}, got ${actualShields}`);
    }
}

async function assertWinners(expectedWinners) {
    const response = await fetch('http://localhost:8080/api/winners');
    const actualWinners = await response.json();

    console.assert(
        JSON.stringify(actualWinners) === JSON.stringify(expectedWinners),
        `Expected winners: ${JSON.stringify(expectedWinners)}, got: ${JSON.stringify(actualWinners)}`
    );
}
