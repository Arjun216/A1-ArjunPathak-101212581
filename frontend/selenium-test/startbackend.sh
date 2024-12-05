#!/bin/bash

# Define paths and commands
TEST_FILES=("0_winner_quest.js" "_1winner_game_with_events.js" "_2winner_game_2winner_quest.js" "A1_scenario.js")
NODE_CMD="node"
BACKEND_STOP_CMD="pkill -f GameApplication" # Replace with your backend stop command
BACKEND_START_CMD="java -jar /path/to/GameApplication.jar &" # Replace with your backend start command
BACKEND_START_DELAY=5 # Seconds to wait for the backend to start

# Function to restart the backend
restart_backend() {
    echo "Stopping backend..."
    eval $BACKEND_STOP_CMD
    if [ $? -eq 0 ]; then
        echo "Backend stopped successfully."
    else
        echo "Failed to stop backend."
        exit 1
    fi

    echo "Starting backend..."
    eval $BACKEND_START_CMD
    if [ $? -eq 0 ]; then
        echo "Backend started successfully."
        echo "Waiting for backend to initialize..."
        sleep $BACKEND_START_DELAY
    else
        echo "Failed to start backend."
        exit 1
    fi
}

# Main loop to run tests
for TEST_FILE in "${TEST_FILES[@]}"; do
    echo "Starting test: $TEST_FILE"

    # Restart the backend
    restart_backend

    # Run the test
    echo "Running test script: $TEST_FILE"
    $NODE_CMD $TEST_FILE
    if [ $? -eq 0 ]; then
        echo "Test $TEST_FILE completed successfully."
    else
        echo "Test $TEST_FILE failed."
        exit 1
    fi
done

echo "All tests completed successfully."
