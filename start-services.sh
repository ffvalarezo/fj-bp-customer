#!/bin/bash

echo "Starting customer-service on port 8081..."
./gradlew :customer-service:bootRun &
CUSTOMER_PID=$!

echo "Waiting 30 seconds for customer-service to start..."
sleep 30

echo "Starting account-service on port 8082..."
./gradlew :account-service:bootRun &
ACCOUNT_PID=$!

echo "Both services are starting..."
echo "Customer Service PID: $CUSTOMER_PID"
echo "Account Service PID: $ACCOUNT_PID"

echo "Press Ctrl+C to stop both services"
wait
