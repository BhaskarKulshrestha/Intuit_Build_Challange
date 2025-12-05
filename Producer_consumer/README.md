# Producer-Consumer Pattern Implementation

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)

A comprehensive implementation of the classic **Producer-Consumer** pattern in Java, demonstrating advanced thread synchronization techniques and concurrent programming concepts.

## 📋 Table of Contents

- [Overview](#overview)
- [Key Concepts](#key-concepts)
- [Project Structure](#project-structure)
- [Features](#features)
- [Getting Started](#getting-started)
- [Running the Demos](#running-the-demos)
- [Testing](#testing)
- [Implementation Details](#implementation-details)
- [Thread Synchronization Techniques](#thread-synchronization-techniques)

## 🎯 Overview

This project implements a classic producer-consumer pattern demonstrating thread synchronization and communication. The program simulates concurrent data transfer between:

- **Producer threads** that read from a source container and place items into a shared queue
- **Consumer threads** that read from the queue and store items in a destination container

## 🔑 Key Concepts

### Thread Synchronization
- **Mutual Exclusion**: Only one thread can access critical sections at a time
- **Condition Variables**: Threads wait for specific conditions (wait/notify mechanism)
- **Blocking Operations**: Threads block when buffer is full (producer) or empty (consumer)

### Concurrent Programming
- **Race Conditions**: Prevented through synchronized access to shared resources
- **Deadlock Prevention**: Proper use of wait/notify to avoid circular waiting
- **Thread Communication**: Producers and consumers coordinate through shared buffer

### Blocking Queues
- **Bounded Buffer**: Fixed-capacity queue with blocking behavior
- **ArrayBlockingQueue**: Java's built-in thread-safe blocking queue implementation
- **Thread-Safe Operations**: put() and take() methods handle synchronization automatically

## 📁 Project Structure

```
Producer_consumer/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── intuit/
│   │               └── producerconsumer/
│   │                   ├── Container.java                    # Thread-safe storage container
│   │                   ├── SharedBuffer.java                 # Custom wait/notify buffer
│   │                   ├── Producer.java                     # Producer thread implementation
│   │                   ├── Consumer.java                     # Consumer thread implementation
│   │                   ├── ProducerConsumerDemo.java        # Basic demo
│   │                   ├── MultipleProducersConsumersDemo.java  # Multi-thread demo
│   │                   └── blockingqueue/
│   │                       ├── BlockingQueueProducer.java   # Producer with BlockingQueue
│   │                       ├── BlockingQueueConsumer.java   # Consumer with BlockingQueue
│   │                       └── BlockingQueueDemo.java       # BlockingQueue demo
│   └── test/
│       └── java/
│           └── com/
│               └── intuit/
│                   └── producerconsumer/
│                       └── ProducerConsumerTest.java        # Unit tests
├── pom.xml                                                   # Maven configuration
└── README.md                                                 # This file
```

## ✨ Features

### 1. Custom SharedBuffer Implementation
- Uses `wait()` and `notify()` for thread synchronization
- Bounded buffer with configurable capacity
- Blocking behavior when full (producers wait) or empty (consumers wait)
- Thread-safe operations with synchronized methods

### 2. Java BlockingQueue Implementation
- Demonstrates Java's built-in `ArrayBlockingQueue`
- Automatic thread synchronization
- Comparison with custom implementation

### 3. Multiple Synchronization Patterns
- **Wait/Notify Mechanism**: Classic low-level synchronization
- **Blocking Queues**: High-level Java concurrent utilities
- Thread-safe containers with synchronized methods

### 4. Comprehensive Testing
- Unit tests for thread synchronization
- Blocking behavior verification
- Thread-safety tests
- Multi-producer/consumer scenarios

## 🚀 Getting Started

### Prerequisites

- **Java JDK 11 or higher**
- **Maven 3.6 or higher**

### Installation

1. **Clone or navigate to the project directory:**
   ```bash
   cd /Users/I528989/Downloads/intuit/Intuit_Build_Challange/Producer_consumer
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

## 🎮 Running the Demos

### 1. Basic Producer-Consumer Demo
Demonstrates a single producer and single consumer using custom SharedBuffer:

```bash
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.ProducerConsumerDemo"
```

**What it demonstrates:**
- Producer reads 10 items from source container
- Items placed in shared buffer (capacity: 5)
- Consumer removes items from buffer and stores in destination
- Shows blocking behavior when buffer is full/empty

**📄 See detailed output:** [SAMPLE_OUTPUT.md](SAMPLE_OUTPUT.md) - Section 1

### 2. Multiple Producers and Consumers Demo
Demonstrates concurrent operation of multiple producers and consumers:

```bash
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.MultipleProducersConsumersDemo"
```

**What it demonstrates:**
- Two producers adding items concurrently
- Two consumers removing items concurrently
- Thread interleaving and synchronization
- Fair access to shared resources

**📄 See detailed output:** [SAMPLE_OUTPUT.md](SAMPLE_OUTPUT.md) - Section 2

### 3. BlockingQueue Demo
Demonstrates Java's built-in BlockingQueue implementation:

```bash
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.blockingqueue.BlockingQueueDemo"
```

**What it demonstrates:**
- Using `ArrayBlockingQueue` for thread synchronization
- Automatic handling of blocking operations
- Comparison with custom implementation

**📄 See detailed output:** [SAMPLE_OUTPUT.md](SAMPLE_OUTPUT.md) - Section 3

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=ProducerConsumerTest
```

### Test Coverage

The test suite includes:

1. **Single Producer/Consumer Test**: Verifies basic functionality
2. **Multiple Producers/Consumers Test**: Tests concurrent operations
3. **Buffer Blocking Tests**: Verifies blocking when full/empty
4. **Thread-Safety Tests**: Validates synchronized access
5. **Edge Cases**: Tests boundary conditions

## 🔧 Implementation Details

### SharedBuffer Class

The `SharedBuffer` class implements a bounded buffer using wait/notify:

```java
public synchronized void produce(T item) throws InterruptedException {
    while (buffer.size() == capacity) {
        wait();  // Wait if buffer is full
    }
    buffer.add(item);
    notifyAll();  // Notify waiting consumers
}

public synchronized T consume() throws InterruptedException {
    while (buffer.isEmpty()) {
        wait();  // Wait if buffer is empty
    }
    T item = buffer.poll();
    notifyAll();  // Notify waiting producers
    return item;
}
```

**Key Points:**
- `synchronized` ensures mutual exclusion
- `wait()` releases the lock and waits for notification
- `notifyAll()` wakes up all waiting threads
- Loop condition prevents spurious wakeups

### Producer Thread

The `Producer` reads from a source container and produces to the shared buffer:

```java
public void run() {
    while (index < sourceContainer.size()) {
        T item = sourceContainer.get(index);
        sharedBuffer.produce(item);  // May block if buffer full
        index++;
        Thread.sleep(delayMs);  // Simulate processing time
    }
}
```

### Consumer Thread

The `Consumer` consumes from the shared buffer and stores in destination:

```java
public void run() {
    while (consumed < itemsToConsume) {
        T item = sharedBuffer.consume();  // May block if buffer empty
        destinationContainer.add(item);
        consumed++;
        Thread.sleep(delayMs);  // Simulate processing time
    }
}
```

## 🔐 Thread Synchronization Techniques

### 1. Wait/Notify Mechanism

**When to use:**
- Low-level control over thread synchronization
- Custom condition-based waiting
- Educational purposes to understand synchronization

**Advantages:**
- Fine-grained control
- Efficient (threads don't busy-wait)
- Standard Java synchronization primitive

**Challenges:**
- Requires careful implementation
- Prone to errors (missed signals, spurious wakeups)
- Must be used within synchronized blocks

### 2. BlockingQueue

**When to use:**
- Producer-consumer patterns
- Thread pools and work queues
- When high-level abstractions are preferred

**Advantages:**
- Built-in thread safety
- Simple API (put/take)
- No explicit synchronization needed
- Less error-prone

**Available Implementations:**
- `ArrayBlockingQueue`: Fixed-size array-based
- `LinkedBlockingQueue`: Optionally bounded linked list
- `PriorityBlockingQueue`: Priority-ordered elements
- `DelayQueue`: Elements with expiration times

### 3. Synchronized Collections

The `Container` class uses synchronized lists for thread-safe storage:

```java
private final List<T> items = Collections.synchronizedList(new ArrayList<>());
```

## 📊 Output Examples

### Basic Demo Output
```
=== Producer-Consumer Pattern Demo ===

Initial Source [size=10, items=[Item-1, Item-2, ..., Item-10]]

Producer-1 started producing...
Consumer-1 started consuming...
Producer-1 - Produced: Item-1 | Buffer size: 1
Producer-1 - Produced: Item-2 | Buffer size: 2
Consumer-1 - Consumed: Item-1 | Buffer size: 1
...
Producer-1 - Buffer is full. Producer waiting...
Consumer-1 - Consumed: Item-5 | Buffer size: 4
...

=== Final Results ===
Destination [size=10, items=[Item-1, Item-2, ..., Item-10]]
Total time: 4532ms

All items successfully transferred from source to destination!
```

## 🎓 Learning Objectives Achieved

✅ **Thread Synchronization**: Understanding wait/notify and synchronized blocks  
✅ **Concurrent Programming**: Multiple threads working cooperatively  
✅ **Blocking Queues**: Using Java's concurrent utilities effectively  
✅ **Wait/Notify Mechanism**: Low-level thread coordination  
✅ **Race Condition Prevention**: Proper synchronization to avoid data corruption  
✅ **Deadlock Avoidance**: Proper use of wait/notify patterns  
✅ **Thread Communication**: Producers and consumers coordinating through shared state

## 🛠️ Customization

You can customize the behavior by modifying:

- **Buffer Capacity**: Change the size in SharedBuffer constructor
- **Processing Delays**: Adjust delay parameters in Producer/Consumer constructors
- **Item Count**: Modify the number of items in source containers
- **Thread Count**: Add more producers/consumers in demo classes

## 📚 Additional Resources

- [Java Concurrency Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [BlockingQueue Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
- [Java Memory Model](https://www.jcp.org/en/jsr/detail?id=133)

## 📝 Notes

- The lint warnings about System.out are expected for demo purposes
- In production code, use a proper logging framework (SLF4J, Log4j)
- Thread delays are simulated using `Thread.sleep()` for demonstration
- All synchronization follows Java best practices

## 🤝 Contributing

This is a demonstration project for the Intuit Build Challenge. Feel free to:
- Experiment with different synchronization techniques
- Add new demo scenarios
- Improve test coverage
- Optimize performance

---

**Author**: Intuit Build Challenge Submission  
**Date**: December 2025  
**Java Version**: 11+
