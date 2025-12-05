#!/bin/bash

# Producer-Consumer Demo Runner Script
# This script provides easy access to all demo programs

echo "========================================"
echo "  Producer-Consumer Pattern Demo"
echo "========================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven from https://maven.apache.org/"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java JDK 11 or higher"
    exit 1
fi

echo "Building project..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

echo "Build successful!"
echo ""

# Menu
echo "Select a demo to run:"
echo "1. Basic Producer-Consumer (Single Producer & Consumer)"
echo "2. Multiple Producers and Consumers"
echo "3. BlockingQueue Demo"
echo "4. Run All Tests"
echo "5. Exit"
echo ""
read -p "Enter your choice (1-5): " choice

case $choice in
    1)
        echo ""
        echo "Running Basic Producer-Consumer Demo..."
        echo "========================================"
        mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.ProducerConsumerDemo" -q
        ;;
    2)
        echo ""
        echo "Running Multiple Producers and Consumers Demo..."
        echo "========================================"
        mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.MultipleProducersConsumersDemo" -q
        ;;
    3)
        echo ""
        echo "Running BlockingQueue Demo..."
        echo "========================================"
        mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.blockingqueue.BlockingQueueDemo" -q
        ;;
    4)
        echo ""
        echo "Running All Tests..."
        echo "========================================"
        mvn test
        ;;
    5)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "Invalid choice. Please run the script again."
        exit 1
        ;;
esac

echo ""
echo "========================================"
echo "Demo completed!"
echo "========================================"
