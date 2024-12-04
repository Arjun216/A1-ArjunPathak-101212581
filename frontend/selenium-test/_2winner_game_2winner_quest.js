
const { Builder, By, until } = require('selenium-webdriver');
const fetch = require('node-fetch');
require('chromedriver');
const chrome = require('selenium-webdriver/chrome');

(async function testWithRigging() {
    let driver;
    try {
        let options = new chrome.Options();
        // options.addArguments('--headless'); // Uncomment to run in headless mode

        // Initialize the game

        await initializeGame();

        // Rig all required configurations via backend endpoints
        await rigAll();

        driver = await new Builder()
            .forBrowser('chrome')
            .setChromeOptions(options)
            .build();

        // Navigate to your game frontend
        await driver.get('http://127.0.0.1:8081');

        // Wait for the page to load
        await driver.wait(until.titleIs('Card Game'), 10000);

        // Start the game
        await startGame(driver);

        // Simulate inputs
        await simulateUserInputs(driver);

        await driver.sleep(2000000000);


        console.log('Test scenario executed successfully.');
    } catch (error) {
        console.error('An error occurred during the test:', error);
    } finally {
        if (driver) {
            await driver.quit();
        }
    }
})();

async function initializeGame() {
    const response = await fetch('http://localhost:8080/api/initialize', {
        method: 'GET'
    });
    const text = await response.text();
    if (!response.ok) {
        console.error('Response from initializeGame:', text);
        throw new Error('Failed to initialize game.');
    }
    console.log('Game initialized successfully:', text);
}

async function rigAll() {
    try {
        await Promise.all([
            rigAdventureDeck(),
            rigEventDeck(),
            rigPlayerHands()
        ]);
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
        body: JSON.stringify(2),
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
        body: JSON.stringify(2),
    });
    const text = await response.text();
    if (!response.ok) {
        console.error('Response from rigPlayerHands:', text);
        throw new Error('Failed to rig Player Hands.');
    }
    console.log('Player Hands rigged successfully:', text);
}

async function startGame(driver) {
    // Wait for the Start Game button to appear and click it
    let startButton = await driver.findElement(By.id('startGameButton'));
    if (startButton) {
        await startButton.click();
        console.log('Game started successfully.');
    } else {
        throw new Error('Start Game button not found.');
    }

    // Wait for logs to confirm the game has started
    await driver.sleep(200);
}

async function waitForGameToBeReady() {
    let isWaiting = false;
    for (let i = 0; i < 20; i++) { // Timeout after 20 attempts (adjust as needed)
        const response = await fetch('http://localhost:8080/api/isWaitingForInput');
        isWaiting = await response.json();
        if (isWaiting) {
            return;
        }
        await new Promise(resolve => setTimeout(resolve, 1000)); // Wait 1 second before checking again
    }
    throw new Error('Game did not become ready for input within expected time');
}

async function simulateUserInputs(driver) {
    const inputs = ['yes', ' 1', ' quit', ' 1', ' 5', ' quit', ' 1', ' 4', ' quit', ' 1', ' 4', ' quit', ' yes', ' yes', ' yes', ' 1'
    , ' 1', ' 1', ' 7', ' quit', ' quit', ' 7', ' quit', ' yes', ' yes', ' 4', ' quit', ' 4', ' quit', ' yes', ' yes',
     ' 6', ' 6', ' quit', ' 6', ' 6', ' quit', ' yes', ' yes', ' 6', ' 7', ' quit', ' 6', ' 7', ' quit', ' yes', ' yes',
      ' 1', ' 1', ' 1', ' 1', ' no', ' yes', ' 1', ' quit', ' 1', ' 3', ' quit', ' 1', ' 4', ' quit', ' no', ' yes', ' yes',
       ' 6', ' quit', ' 6', ' quit', ' yes', ' yes', ' 7', ' quit', ' 7', ' quit', ' yes', ' yes', ' 10', ' quit', ' 10', ' quit',
        ' yes', ' yes', ' 1', ' 2', ' 2']


    for (let input of inputs) {
        await waitForGameToBeReady();

        let inputField = await driver.findElement(By.id('inputField'));
        let sendButton = await driver.findElement(By.id('sendButton'));

        await inputField.clear();
        await inputField.sendKeys(input);
        await new Promise(resolve => setTimeout(resolve, 700));

        await sendButton.click();

        //wait for the game to process the input
        await new Promise(resolve => setTimeout(resolve, 300));
    }
}
