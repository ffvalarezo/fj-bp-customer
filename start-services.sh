#!/bin/bash

echo "Starting customer-service on port 8081..."
./gradlew :customer-service:bootRun &
CUSTOMER_PID=$!

echo "Waiting 30 seconds for customer-service to start..."
sleep 30

echo "Starting account-service on port 8082..."
./gradlew :account-service:bootRun &
ACCOUNT_PID=$!

echo "Waiting 30 seconds for movement-service to start..."
sleep 30

echo "Starting movement-service on port 8080..."
./gradlew :movement-service:bootRun &
MOVEMENT_PID=$!

echo "Both services are starting..."
echo "Customer Service PID: $CUSTOMER_PID"
echo "Account Service PID: $ACCOUNT_PID"
echo "Movement Service PID: $MOVEMENT_PID"

echo "Press Ctrl+C to stop both services"
wait
