const { Builder, until } = require('selenium-webdriver');
const fetch = require('node-fetch');
require('chromedriver');
const chrome = require('selenium-webdriver/chrome');
const { exec } = require('child_process');

const tests = [

    './0_winner_quest.js',
    './_1winner_game_with_events.js',
    './_2winner_game_2winner_quest.js',
    './A1_scenario.js',
];

(async function runAllTests() {
    let driver;
    try {
        const options = new chrome.Options();
        driver = await new Builder().forBrowser('chrome').setChromeOptions(options).build();

        for (const test of tests) {
            console.log(`Starting test: ${test}`);

            // Refresh backend
            console.log('Refreshing backend...');
            await refreshBackend();

            // Refresh frontend
            console.log('Refreshing web page...');
            await driver.get('http://127.0.0.1:8081');
            await driver.wait(until.titleIs('Card Game'), 10000);

            // Execute the test script
            await runTestScript(test);

            console.log(`Completed test: ${test}`);
        }
    } catch (error) {
        console.error('An error occurred:', error);
    } finally {
        if (driver) {
            await driver.quit();
        }
    }
})();

async function refreshBackend() {
    try {
        const response = await fetch('http://localhost:8080/api/initialize', { method: 'GET' });
        if (!response.ok) {
            throw new Error('Failed to refresh backend');
        }
        console.log('Backend refreshed successfully.');
    } catch (error) {
        console.error('Error refreshing backend:', error);
    }
}

function runTestScript(testFile) {
    return new Promise((resolve, reject) => {
        exec(`node ${testFile}`, (error, stdout, stderr) => {
            if (error) {
                console.error(`Error executing test ${testFile}:`, stderr);
                return reject(error);
            }
            console.log(`Output for ${testFile}:\n`, stdout);
            resolve();
        });
    });
}
