// References to UI elements
const logDiv = document.getElementById('log');
const inputField = document.getElementById('inputField');
const sendButton = document.getElementById('sendButton');
const startGameButton = document.getElementById('startGameButton');

let isGameOver = false;
let lastLogLength = 0;

// Function to append messages to the log
function appendLog(message) {
    const messageElement = document.createElement('div');
    messageElement.textContent = message;
    logDiv.appendChild(messageElement);
    logDiv.scrollTop = logDiv.scrollHeight;
}

// Fetch logs from the backend and append new ones
function fetchLogs() {
    fetch('http://127.0.0.1:8080/api/logs')
        .then(response => response.json())
        .then(data => {
            const newLogs = data.slice(lastLogLength);
            newLogs.forEach(log => appendLog(log));
            lastLogLength = data.length;
            console.log(newLogs);
        })
        .catch(error => console.error('Error fetching logs:', error));
}

// Send user input to the backend
function sendUserInput() {
    const userInput = inputField.value.trim();
    if (!userInput) {
        alert('Please enter a valid input.');
        return;
    }
    inputField.value = '';

    fetch('http://localhost:8080/api/input', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: userInput,
    }).then(() => fetchLogs())
      .catch(error => console.error('Error sending input:', error));
}

// Fetch and display player information
function fetchPlayersInfo() {
    fetch('http://localhost:8080/api/players-info')
        .then(response => response.json())
        .then(data => {
            const playersContainer = document.getElementById('players-container');
            playersContainer.innerHTML = ''; // Clear previous data

            data.forEach(player => {
                const playerDiv = document.createElement('div');
                playerDiv.className = 'player';

                // Player ID
                const playerId = document.createElement('h4');
                playerId.textContent = `Player: ${player.id}`;
                playerDiv.appendChild(playerId);

                // Shields
                const shields = document.createElement('p');
                shields.textContent = `Shields: ${player.shields}`;
                playerDiv.appendChild(shields);

                // Cards Header
                const cardsHeader = document.createElement('p');
                cardsHeader.textContent = 'Cards:';
                playerDiv.appendChild(cardsHeader);

                // List of Cards
                const cardsList = document.createElement('ol');
                player.cards.forEach(card => {
                    const cardItem = document.createElement('li');
                    cardItem.textContent = `${card.name} (${card.type}, Power: ${card.power})`;
                    cardsList.appendChild(cardItem);
                });
                playerDiv.appendChild(cardsList);

                playersContainer.appendChild(playerDiv);
            });
        })
        .catch(error => console.error('Error fetching players info:', error));
}

// Check if the game is waiting for input
function checkWaitingForInput() {
    fetch('http://localhost:8080/api/isWaitingForInput')
        .then(response => response.json())
        .then(data => {
            inputField.disabled = !data;
            sendButton.disabled = !data;
        })
        .catch(error => console.error('Error checking waiting input status:', error));
}

// Start the game
function startGame() {
    fetch('http://localhost:8080/api/start')
        .then(response => response.text())
        .then(data => {
            appendLog(data);
            fetchLogs();
        })
        .catch(error => console.error('Error starting game:', error));
}

// Initialize the game
function initializeGame() {
    return fetch('http://localhost:8080/api/initialize', { method: 'GET' })
        .then(response => {
            if (!response.ok) throw new Error("Failed to initialize the game");
            return response.text();
        })
        .then(data => appendLog(data))
        .catch(error => console.error('Error initializing game:', error));
}

// Check if the game is initialized and start it
startGameButton.addEventListener('click', () => {
    fetch('http://localhost:8080/api/isGameInitialized')
        .then(response => response.json())
        .then(isInitialized => {
            if (!isInitialized) {
                initializeGame()
                    .then(() => {
                        startGameButton.disabled = true; // Disable button after starting
                        startGame();
                    })
                    .catch(error => appendLog("Error initializing the game: " + error.message));
            } else {
                startGameButton.disabled = true; // Disable button after starting
                startGame();
            }
        })
        .catch(error => appendLog("Error checking if game is initialized: " + error.message));
});



// Check if the game is waiting for input every 0.5 seconds
setInterval(() => {
    if (!isGameOver) {
        fetchLogs();
        checkWaitingForInput();
    }
}, 500);

// Fetch player information every 0.5 seconds
setInterval(fetchPlayersInfo, 50);

// Event listener for the send button
sendButton.addEventListener('click', sendUserInput);

//npx http-server --cors
