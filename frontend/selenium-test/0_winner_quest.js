const { Builder, By, until } = require('selenium-webdriver');
const fetch = require('node-fetch');
require('chromedriver');
const chrome = require('selenium-webdriver/chrome');

(async function testWithRigging() {
    let driver;

    try {
        let options = new chrome.Options();

        // Ensure the game is shut down before starting
        await shutdownGame();

        // Initialize the game
        await initializeGame();

        // Rig the game configuration
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

        // Simulate player inputs
        await simulateUserInputs(driver);

        // Allow the game some time to process
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
        body: JSON.stringify(0),
    });
    const text = await response.text();

    if (!response.ok) {
        console.error('Response from rigAdventureDeck:', text);
        throw new Error('Failed to rig Adventure Deck.');
    }

    console.log('Adventure Deck rigged successfully:', text);
}

async function rigEventDeck() {
    const response = await fetch('http://localhost:8080/api/rig/eventDeck', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(0),
    });
    const text = await response.text();

    if (!response.ok) {
        console.error('Response from rigEventDeck:', text);
        throw new Error('Failed to rig Event Deck.');
    }

    console.log('Event Deck rigged successfully:', text);
}

async function rigPlayerHands() {
    const response = await fetch('http://localhost:8080/api/rig/playerHands', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(0),
    });
    const text = await response.text();

    if (!response.ok) {
        console.error('Response from rigPlayerHands:', text);
        throw new Error('Failed to rig Player Hands.');
    }

    console.log('Player Hands rigged successfully:', text);
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

    throw new Error('Game did not become ready for input within expected time');
}

async function simulateUserInputs(driver) {
    const inputs = ['yes', ' 1', ' 2', ' 3', ' 4', ' 5', ' 6', ' quit', ' 1', ' 1', ' 1', ' 1', ' 1', ' 1',
        ' quit', ' yes', ' yes', ' yes', ' 1', ' 4', ' 3', ' 12', ' quit', ' quit', ' quit', ' 1', ' 1'];

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
        if (index === 14) {
            const expectedStages = [
                { totalValue: 110, cards: ['F50', 'Dagger', 'Sword', 'Horse', 'Battle-Axe', 'Lance'] },
                { totalValue: 130, cards: ['F70', 'Dagger', 'Sword', 'Horse', 'Battle-Axe', 'Lance'] },
            ];
            await assertQuestStage(expectedStages);
        }
    }

    const expectedHands = {
        P1: ['F15', 'Dagger', 'Dagger', 'Dagger', 'Dagger', 'Sword', 'Sword', 'Sword', 'Horse', 'Horse', 'Horse', 'Horse'],
        P2: ['F5', 'F5', 'F10', 'F15', 'F15', 'F20', 'F20', 'F25', 'F30', 'F30', 'F40'],
        P3: ['F5', 'F5', 'F10', 'F15', 'F15', 'F20', 'F20', 'F25', 'F25', 'F30', 'F40', 'Lance'],
        P4: ['F5', 'F5', 'F10', 'F15', 'F15', 'F20', 'F20', 'F25', 'F25', 'F30', 'F50', 'Excalibur'],
    };
    await assertPlayerHands(expectedHands);

    const expectedShields = { P1: 0, P2: 0, P3: 0, P4: 0 };
    await assertPlayerShields(expectedShields);

    await assertWinners([]);
}

async function shutdownGame() {
    const url = 'http://localhost:8080/api/shutdown';

    try {
        const response = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' } });

        if (response.ok) {
            console.log('Game shut down successfully.');
        } else {
            console.error('Failed to shut down the game:', response.status);
        }
    } catch (error) {
        console.error('Error sending shutdown request:', error);
    }
}

// Assertion Functions
async function assertSponsor(expectedSponsorId) {
    const response = await fetch('http://localhost:8080/api/sponsor');
    const sponsorId = await response.text();

    console.assert(sponsorId === expectedSponsorId, 'Wrong sponsor');
}

async function assertQuestStage(expectedStages) {
    const response = await fetch('http://localhost:8080/api/quest-stages');
    const stages = await response.json();

    console.assert(stages.length === expectedStages.length, `Expected ${expectedStages.length} stages, but got ${stages.length}`);
    if (stages.length !== expectedStages.length) throw new Error('Quest stage assertion failed');

    stages.forEach((stage, index) => {
        console.assert(stage.totalValue === expectedStages[index].totalValue,
            `Stage ${index + 1}: Expected total value ${expectedStages[index].totalValue}, but got ${stage.totalValue}`);
        console.assert(JSON.stringify(stage.cards) === JSON.stringify(expectedStages[index].cards),
            `Stage ${index + 1}: Expected cards ${JSON.stringify(expectedStages[index].cards)}, but got ${JSON.stringify(stage.cards)}`);
    });

    console.log('Quest stage assertion successful');
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
            `Player ${playerId}: Expected hand ${JSON.stringify(expectedCards)}, but got ${JSON.stringify(actualCards)}`
        );

        if (JSON.stringify(actualCards) === JSON.stringify(expectedCards)) {
            console.log(`Player ${playerId}'s hand matches expected.`);
        }
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

        console.assert(
            actualShields === expectedShieldCount,
            `Player ${playerId}: Expected ${expectedShieldCount} shields, but got ${actualShields}`
        );

        if (actualShields === expectedShieldCount) {
            console.log(`Player ${playerId}'s shields match expected.`);
        }
    }
}

async function assertWinners(expectedWinners) {
    const response = await fetch('http://localhost:8080/api/winners');
    const actualWinners = await response.json();

    console.assert(
        JSON.stringify(actualWinners) === JSON.stringify(expectedWinners),
        `Expected winners: ${JSON.stringify(expectedWinners)}, but got: ${JSON.stringify(actualWinners)}`
    );

    if (JSON.stringify(actualWinners) === JSON.stringify(expectedWinners)) {
        console.log(`Winners match expected: ${JSON.stringify(actualWinners)}`);
    }
}
