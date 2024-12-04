#!/bin/bash

# Start frontend
(cd frontend && npx http-server --cors) &
sleep 2
# Start Selenium tests
(cd frontend/selenium-test && node simple_test.js)
