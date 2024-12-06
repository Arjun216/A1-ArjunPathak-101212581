const { Builder, By, until } = require('selenium-webdriver');
const fetch = require('node-fetch');
require('chromedriver');
const chrome = require('selenium-webdriver/chrome');

(async function testWithRigging() {
    let driver;
    try {
        const options = new chrome.Options();

        // Ensure the game is shut down before starting
        await shutdownGame();

        // Initialize the game
        await initializeGame();

        // Rig the game configurations
        await rigAll();

        // Set up the browser driver
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

        // Allow time for processing
        await driver.sleep(5000);

        console.log('Test scenario executed successfully.');

        // Shut down the game
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
        body: JSON.stringify(3),
    });

    if (!response.ok) {
        const text = await response.text();
        console.error('Response from rigAdventureDeck:', text);
        throw new Error('Failed to rig Adventure Deck.');
    }

    console.log('Adventure Deck rigged successfully.');
}

async function rigEventDeck() {
    const response = await fetch('http://localhost:8080/api/rig/eventDeck', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(3),
    });

    if (!response.ok) {
        const text = await response.text();
        console.error('Response from rigEventDeck:', text);
        throw new Error('Failed to rig Event Deck.');
    }

    console.log('Event Deck rigged successfully.');
}

async function rigPlayerHands() {
    const response = await fetch('http://localhost:8080/api/rig/playerHands', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(3),
    });

    if (!response.ok) {
        const text = await response.text();
        console.error('Response from rigPlayerHands:', text);
        throw new Error('Failed to rig Player Hands.');
    }

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
        'no', 'yes', '1', '8', 'quit', '2', '5', 'quit', '2', '3', '4', 'quit', '2', '3', 'quit', // 14
        'yes', 'yes', 'yes', '1', '1', '1', '5', '5', 'quit', '5', '4', 'quit', '5', '7', 'quit', // 34
        'yes', 'yes', 'yes', '7', '6', 'quit', '9', '4', 'quit', '6', '7', 'quit', 'yes', 'yes', // 54
        '9', '6', '4', 'quit', '7', '5', '7', 'quit', 'yes', 'yes', '7', '6', '6', 'quit', '4', // 64
        '4', '5', '5', 'quit', 'yes', '10'
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

        if (index === 2) await assertSponsor("P2");
        if (index === 14) {
            const expectedStages = [
                { totalValue: 15, cards: ['F5', 'Horse'] },
                { totalValue: 25, cards: ['F15', 'Sword'] },
                { totalValue: 35, cards: ['F15', 'Dagger', 'Battle-Axe'] },
                { totalValue: 55, cards: ['F40', 'Battle-Axe'] }
            ];
            await assertQuestStage(expectedStages);
        }
        if (index === 41) {
            const expectedHands = {
                P1: ['F5', 'F10', 'F15', 'F15', 'F30', 'Horse', 'Battle-Axe', 'Battle-Axe', 'Lance'],
                P2: ['F5', 'Horse', 'Excalibur'],
                P3: ['F5', 'F5', 'F15', 'Sword', 'Sword', 'Horse', 'Horse', 'Lance', 'Lance'],
                P4: ['F15', 'F15', 'F40', 'Dagger', 'Sword', 'Battle-Axe', 'Lance', 'Lance', 'Excalibur']
            };
            await assertPlayerHands(expectedHands);
        }
    }

    const finalExpectedHands = {
        P1: ['F5', 'F10', 'F15', 'F15', 'F30', 'Horse', 'Battle-Axe', 'Battle-Axe', 'Lance'],
        P2: ['F5', 'F10', 'F10', 'F10', 'F10', 'F10', 'F10', 'Sword', 'Sword', 'Sword', 'Sword', 'Sword', 'Sword', 'Horse', 'Excalibur'],
        P3: ['F5', 'F5', 'F15', 'F30', 'Sword'],
        P4: ['F15', 'F15', 'F40', 'Lance']
    };
    await assertPlayerHands(finalExpectedHands);

    const expectedShields = { P1: 0, P2: 0, P3: 0, P4: 4 };
    await assertPlayerShields(expectedShields);

    await assertWinners([]);
}

// Assertion Functions
async function shutdownGame() {
    const url = 'http://localhost:8080/api/shutdown';
    try {
        const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' } });

        if (!response.ok) throw new Error('Failed to shut down the game.');

        console.log('Game shut down successfully.');
    } catch (error) {
        console.error('Error sending shutdown request:', error);
    }
}

async function assertSponsor(expectedSponsorId) {
    const response = await fetch('http://localhost:8080/api/sponsor');
    const sponsorId = await response.text();

    console.assert(sponsorId === expectedSponsorId, 'Wrong sponsor.');
}

async function assertQuestStage(expectedStages) {
    const response = await fetch('http://localhost:8080/api/quest-stages');
    const stages = await response.json();

    console.assert(stages.length === expectedStages.length, `Expected ${expectedStages.length} stages, got ${stages.length}.`);
    if (stages.length !== expectedStages.length) throw new Error('Quest stage assertion failed.');

    stages.forEach((stage, index) => {
        console.assert(stage.totalValue === expectedStages[index].totalValue,
            `Stage ${index + 1}: Expected ${expectedStages[index].totalValue}, got ${stage.totalValue}.`);
        console.assert(JSON.stringify(stage.cards) === JSON.stringify(expectedStages[index].cards),
            `Stage ${index + 1}: Cards mismatch.`);
    });

    console.log('Quest stage assertion successful.');
}

async function assertPlayerHands(expectedHands) {
    const response = await fetch('http://localhost:8080/api/players-info');
    const players = await response.json();

    for (const playerId in expectedHands) {
        const player = players.find(p => p.id === playerId);
        if (!player) throw new Error(`Player ${playerId} not found.`);

        const actualCards = player.cards.map(card => card.name);
        console.assert(
            JSON.stringify(actualCards) === JSON.stringify(expectedHands[playerId]),
            `Player ${playerId}: Hand mismatch.`
        );

        console.log(`Player ${playerId}'s hand matches expected.`);
    }
}

async function assertPlayerShields(expectedShields) {
    const response = await fetch('http://localhost:8080/api/players-info');
    const players = await response.json();

    for (const playerId in expectedShields) {
        const player = players.find(p => p.id === playerId);
        if (!player) throw new Error(`Player ${playerId} not found.`);

        console.assert(player.shields === expectedShields[playerId],
            `Player ${playerId}: Shield count mismatch.`);
    }
}

async function assertWinners(expectedWinners) {
    const response = await fetch('http://localhost:8080/api/winners');
    const actualWinners = await response.json();

    console.assert(
        JSON.stringify(actualWinners) === JSON.stringify(expectedWinners),
        `Winners mismatch.`
    );

    console.log('Winners match expected.');
}
