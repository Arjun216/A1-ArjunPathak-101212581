const logDiv = document.getElementById('log');
const inputField = document.getElementById('inputField');
const sendButton = document.getElementById('sendButton');

let isGameOver = false;
let lastLogLength = 0;

// Function to append messages to the log
function appendLog(message) {
    const messageElement = document.createElement('div');
    messageElement.textContent = message;
    logDiv.appendChild(messageElement);
    logDiv.scrollTop = logDiv.scrollHeight;
}

// Function to fetch logs from the backend
function fetchLogs() {
fetch('http://127.0.0.1:8080/api/logs')
        .then(response => response.json())
        .then(data => {
            // Only append new logs
            const newLogs = data.slice(lastLogLength);
            newLogs.forEach(log => appendLog(log));
            lastLogLength = data.length;
            console.log(newLogs)
        });
}

// Function to send user input to the backend
function sendUserInput() {
    const userInput = inputField.value.trim();
    if (userInput === '') {
        alert('Please enter a valid input.');
        return;
    }
    inputField.value = '';
    fetch('http://localhost:8080/api/input', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: userInput
    })
    .then(() => {
        fetchLogs();
    });
}


// Event listener for the send button
sendButton.addEventListener('click', sendUserInput);

// Start the game when the page loads
function startGame() {
    fetch('http://localhost:8080/api/start')
        .then(response => response.text())
        .then(data => {
            appendLog(data);
            fetchLogs();
        });
}
function fetchPlayersInfo() {
    fetch('http://localhost:8080/api/players-info')
        .then(response => response.json())
        .then(data => {
            const playersContainer = document.getElementById('players-container');
            playersContainer.innerHTML = ''; // Clear previous data

            data.forEach(player => {
                const playerDiv = document.createElement('div');
                playerDiv.className = 'player';

                const playerId = document.createElement('h4');
                playerId.textContent = `Player: ${player.id}`;
                playerDiv.appendChild(playerId);

                const shields = document.createElement('p');
                shields.textContent = `Shields: ${player.shields}`;
                playerDiv.appendChild(shields);

                const cardsHeader = document.createElement('p');
                cardsHeader.textContent = 'Cards:';
                playerDiv.appendChild(cardsHeader);

                const cardsList = document.createElement('ol'); // Ordered list

                player.cards.forEach((card, index) => {
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





// Poll for new logs every 2 seconds
setInterval(() => {
    if (!isGameOver) {
        fetchLogs();
        // Check if the game is over
        fetch('http://localhost:8080/api/isGameOver')
            .then(response => response.json())
            .then(data => {
                isGameOver = data;
                if (isGameOver) {
                    appendLog('Game Over!');
                    inputField.disabled = true;
                    sendButton.disabled = true;
                }
            });
    }
}, 2000);
function checkWaitingForInput() {
    fetch('http://localhost:8080/api/isWaitingForInput')
        .then(response => response.json())
        .then(data => {
            if (data) {
                inputField.disabled = false;
                sendButton.disabled = false;
            } else {
                inputField.disabled = true;
                sendButton.disabled = true;
            }
        });
}

// Modify the interval to check for waitingForInput
setInterval(() => {
    if (!isGameOver) {
        fetchLogs();
        checkWaitingForInput();
        // Check if the game is over
        fetch('http://localhost:8080/api/isGameOver')
            .then(response => response.json())
            .then(data => {
                isGameOver = data;
                if (isGameOver) {
                    appendLog('Game Over!');
                    inputField.disabled = true;
                    sendButton.disabled = true;
                }
            });
    }
}, 2000);




setInterval(fetchPlayersInfo, 30); // Update every 3 seconds

// Reference to the Start Game button
const startGameButton = document.getElementById('startGameButton');

// Event listener for Start Game button

startGameButton.addEventListener('click', () => {
    fetch('http://localhost:8080/api/isGameInitialized')
        .then(response => response.json())
        .then(isInitialized => {
            if (!isInitialized) {
                initializeGame().then(() => {
                    startGameButton.disabled = true; // Disable button after starting the game
                    startGame();
                }).catch(error => {
                    appendLog("Error initializing the game: " + error.message);
                });
            } else {
                startGameButton.disabled = true; // Disable button after starting the game
                startGame();
            }
        })
        .catch(error => {
            appendLog("Error checking if game is initialized: " + error.message);
        });
});


// Function to initialize the game
function initializeGame() {
    return fetch('http://localhost:8080/api/initialize', {
        method: 'GET'
    }).then(response => {
        if (!response.ok) {
            throw new Error("Failed to initialize the game");
        }
        return response.text();
    }).then(data => appendLog(data));

}
