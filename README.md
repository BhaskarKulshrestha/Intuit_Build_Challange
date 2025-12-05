# Intuit Build Coding Challenge

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> **Comprehensive Solutions for Intuit Build Coding Challenge**  
> Demonstrating advanced Java programming, concurrent systems, data analysis, and software engineering best practices.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Projects](#projects)
  - [1. Producer-Consumer Pattern](#1-producer-consumer-pattern)
  - [2. CSV Data Analysis](#2-csv-data-analysis)
- [Key Technical Concepts](#key-technical-concepts)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Technologies & Tools](#technologies--tools)
- [Development Approach](#development-approach)
- [Testing Strategy](#testing-strategy)
- [Author](#author)

---

## 🎯 Overview

This repository contains **two comprehensive Java applications** developed as part of the Intuit Build Coding Challenge. Each project demonstrates mastery of different aspects of Java programming:

1. **Producer-Consumer Pattern**: Concurrent programming, thread synchronization, and multi-threading
2. **CSV Data Analysis**: Functional programming, Stream API, and data analytics

Both projects follow industry best practices including clean code principles, comprehensive testing, detailed documentation, and production-ready architecture.

---

## 📦 Projects

### 1. Producer-Consumer Pattern

**Location**: [`Producer_consumer/`](./Producer_consumer)

A comprehensive implementation of the classic **Producer-Consumer** pattern demonstrating advanced thread synchronization techniques and concurrent programming concepts.

#### 🔑 Key Features

- **Custom SharedBuffer Implementation**: Uses `wait()` and `notify()` for thread synchronization
- **Java BlockingQueue Implementation**: Demonstrates Java's built-in concurrent utilities
- **Multiple Synchronization Patterns**: Wait/notify mechanism and blocking queues
- **Thread-Safe Operations**: Mutual exclusion and condition variables
- **Comprehensive Testing**: Unit tests for thread synchronization and blocking behavior

#### 📊 Core Concepts

- **Thread Synchronization**: Mutual exclusion, condition variables, blocking operations
- **Concurrent Programming**: Race condition prevention, deadlock avoidance, thread communication
- **Design Patterns**: Producer-Consumer pattern with bounded buffer
- **Thread Safety**: Synchronized methods and concurrent data structures

#### 🚀 Quick Start

```bash
cd Producer_consumer
mvn clean compile
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.ProducerConsumerDemo"
```

#### 📖 Documentation

- [Detailed README](./Producer_consumer/README.md)
- [Quick Start Guide](./Producer_consumer/QUICKSTART.md)
- [Execution Flow](./Producer_consumer/EXECUTION_FLOW.md)
- [Synchronization Details](./Producer_consumer/SYNCHRONIZATION.md)
- [Sample Output](./Producer_consumer/SAMPLE_OUTPUT.md)

---

### 2. CSV Data Analysis

**Location**: [`Data_Analysis_on_CSV/`](./Data_Analysis_on_CSV)

A sophisticated Java application demonstrating advanced data analysis using Java Streams API, functional programming paradigms, and various aggregation techniques on CSV sales data.

#### 🔑 Key Features

- **Stream API Mastery**: Advanced map, filter, reduce, collect operations
- **Functional Programming**: Lambda expressions, method references, collectors
- **Comprehensive Analytics**: Sales statistics, geographic analysis, temporal trends
- **Data Aggregation**: Grouping, partitioning, statistical calculations
- **Customer Insights**: Top customers, segmentation, behavior analysis
- **Product Analytics**: Performance tracking, popularity rankings

#### 📊 Core Analytics Capabilities

1. **Overall Sales Statistics**: Total sales, averages, min/max values
2. **Product Line Analysis**: Sales breakdown by category with statistics
3. **Geographic Analysis**: Sales by country and territory, top regions
4. **Temporal Analysis**: Yearly, quarterly, monthly trends and growth rates
5. **Customer Insights**: Top customers by revenue and segmentation
6. **Product Performance**: Best-sellers by quantity and revenue
7. **Deal Size Segmentation**: Analysis by deal size (Small, Medium, Large)
8. **Order Analysis**: Status distribution and value partitioning

#### 🚀 Quick Start

```bash
cd Data_Analysis_on_CSV
mvn clean compile
mvn exec:java -Dexec.mainClass="com.intuit.challenge.SalesDataAnalysisApp"
```

#### 📖 Documentation

- [Detailed README](./Data_Analysis_on_CSV/README.md)
- [Quick Start Guide](./Data_Analysis_on_CSV/QUICKSTART.md)
- [Architecture Overview](./Data_Analysis_on_CSV/ARCHITECTURE.md)
- [API Documentation](./Data_Analysis_on_CSV/API_DOCUMENTATION.md)
- [Sample Output](./Data_Analysis_on_CSV/SAMPLE_OUTPUT.md)

---

## 🎓 Key Technical Concepts

### Concurrent Programming (Producer-Consumer)

| Concept | Implementation |
|---------|---------------|
| **Thread Synchronization** | `synchronized`, `wait()`, `notify()` |
| **Concurrent Utilities** | `BlockingQueue`, `ArrayBlockingQueue` |
| **Thread Safety** | Mutex locks, atomic operations |
| **Deadlock Prevention** | Proper wait/notify usage |
| **Race Condition Handling** | Synchronized access to shared resources |

### Functional Programming (CSV Analysis)

| Concept | Implementation |
|---------|---------------|
| **Stream Operations** | `map()`, `filter()`, `reduce()`, `collect()` |
| **Lambda Expressions** | Concise functional implementations |
| **Method References** | `::` syntax for cleaner code |
| **Collectors** | `groupingBy()`, `partitioningBy()`, `summarizingDouble()` |
| **Functional Interfaces** | `Predicate`, `Function`, `Supplier`, `Consumer` |

---

## ⚡ Quick Start

### Prerequisites

- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **Git**: For cloning the repository

### Clone the Repository

```bash
git clone https://github.com/BhaskarKulshrestha/Intuit_Build_Challange.git
cd Intuit_Build_Challange
```

### Run Producer-Consumer Project

```bash
# Navigate to project
cd Producer_consumer

# Build the project
mvn clean install

# Run basic demo
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.ProducerConsumerDemo"

# Run multi-threaded demo
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.MultipleProducersConsumersDemo"

# Run BlockingQueue demo
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.blockingqueue.BlockingQueueDemo"

# Run tests
mvn test
```

### Run CSV Data Analysis Project

```bash
# Navigate to project
cd Data_Analysis_on_CSV

# Build the project
mvn clean install

# Run the application
mvn exec:java -Dexec.mainClass="com.intuit.challenge.SalesDataAnalysisApp"

# Run tests
mvn test
```

---

## 📁 Project Structure

```
Intuit_Build_Challange/
│
├── README.md                           # This file
│
├── Producer_consumer/                  # Project 1: Producer-Consumer Pattern
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/intuit/producerconsumer/
│   │   │       ├── Container.java
│   │   │       ├── SharedBuffer.java
│   │   │       ├── Producer.java
│   │   │       ├── Consumer.java
│   │   │       ├── ProducerConsumerDemo.java
│   │   │       ├── MultipleProducersConsumersDemo.java
│   │   │       └── blockingqueue/
│   │   └── test/java/
│   │       └── com/intuit/producerconsumer/
│   │           └── ProducerConsumerTest.java
│   ├── pom.xml
│   ├── README.md
│   ├── QUICKSTART.md
│   ├── EXECUTION_FLOW.md
│   ├── SYNCHRONIZATION.md
│   └── SAMPLE_OUTPUT.md
│
└── Data_Analysis_on_CSV/               # Project 2: CSV Data Analysis
    ├── src/
    │   ├── main/java/
    │   │   └── com/intuit/challenge/
    │   │       ├── SalesDataAnalysisApp.java
    │   │       ├── model/
    │   │       │   ├── SalesRecord.java
    │   │       │   └── SalesStatistics.java
    │   │       └── service/
    │   │           ├── CsvReaderService.java
    │   │           └── SalesAnalyticsService.java
    │   └── test/java/
    │       └── com/intuit/challenge/
    │           ├── SalesDataAnalysisIntegrationTest.java
    │           ├── model/
    │           │   └── SalesRecordTest.java
    │           └── service/
    │               ├── CsvReaderServiceTest.java
    │               └── SalesAnalyticsServiceTest.java
    ├── sales_data_sample.csv
    ├── pom.xml
    ├── README.md
    ├── QUICKSTART.md
    ├── ARCHITECTURE.md
    ├── API_DOCUMENTATION.md
    └── SAMPLE_OUTPUT.md
```

---

## 🛠 Technologies & Tools

### Core Technologies

- **Java 11+**: Modern Java features and APIs
- **Maven**: Build automation and dependency management
- **JUnit 5**: Unit and integration testing
- **Mockito**: Mocking framework for tests

### Java Features Demonstrated

- **Concurrency API**: Thread, Runnable, BlockingQueue, synchronized
- **Streams API**: Functional data processing
- **Lambda Expressions**: Functional programming paradigms
- **Method References**: Clean and readable code
- **Collections Framework**: List, Map, Set operations
- **Exception Handling**: Try-with-resources, custom exceptions
- **Generics**: Type-safe collections
- **Optional**: Null-safe operations

---

## 💡 Development Approach

### Design Principles

- ✅ **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution
- ✅ **Clean Code**: Readable, maintainable, self-documenting code
- ✅ **Design Patterns**: Producer-Consumer, Service Layer, DTO
- ✅ **Separation of Concerns**: Clear distinction between layers
- ✅ **DRY Principle**: Don't Repeat Yourself

### Code Quality

- **Comprehensive Documentation**: JavaDoc, README files, inline comments
- **Unit Testing**: High test coverage with JUnit 5
- **Integration Testing**: End-to-end scenario testing
- **Error Handling**: Robust exception handling and logging
- **Code Organization**: Logical package structure

---

## 🧪 Testing Strategy

### Producer-Consumer Testing

- ✅ Thread synchronization correctness
- ✅ Blocking behavior verification
- ✅ Thread-safety validation
- ✅ Multi-producer/consumer scenarios
- ✅ Edge cases (empty/full buffer)

### CSV Analysis Testing

- ✅ Data parsing accuracy
- ✅ Stream operations correctness
- ✅ Aggregation calculations
- ✅ Edge cases handling
- ✅ Integration tests with real data

### Running All Tests

```bash
# Test Producer-Consumer
cd Producer_consumer && mvn test

# Test CSV Analysis
cd Data_Analysis_on_CSV && mvn test

# Generate test reports
mvn surefire-report:report
```

---

## 📊 Performance Highlights

### Producer-Consumer

- ⚡ Efficient thread synchronization with minimal blocking
- ⚡ Scalable to multiple producers and consumers
- ⚡ Low memory footprint with bounded buffer
- ⚡ Deadlock-free implementation

### CSV Analysis

- ⚡ Stream-based processing for large datasets
- ⚡ Lazy evaluation for memory efficiency
- ⚡ Parallel streams support for performance
- ⚡ Optimized collectors for aggregations

---

## 📚 Learning Outcomes

This project demonstrates proficiency in:

1. **Concurrent Programming**: Thread management, synchronization, and communication
2. **Functional Programming**: Streams API, lambda expressions, collectors
3. **Data Processing**: CSV parsing, aggregation, statistical analysis
4. **Software Design**: Clean architecture, design patterns, SOLID principles
5. **Testing**: Unit tests, integration tests, test-driven development
6. **Documentation**: Technical writing, API documentation, user guides
7. **Build Tools**: Maven project configuration and lifecycle management

---

## 🔗 Branch Structure

- **`main`**: Main branch with initial setup
- **`feature/producer-consumer-implementation`**: Complete Producer-Consumer implementation (14 commits)
- **`feature/CSV_Data_Analysis`**: Complete CSV Analysis implementation (15 commits)

---

## 👤 Author

**Bhaskar Kulshrestha**
- GitHub: [@BhaskarKulshrestha](https://github.com/BhaskarKulshrestha)
- Email: bhaskarkulshrestha03@gmail.com

---

## 📄 License

This project is developed as part of the Intuit Build Coding Challenge.

---

## 🙏 Acknowledgments

- **Intuit**: For providing this comprehensive coding challenge
- **Java Community**: For excellent documentation and resources
- **Open Source**: For inspiring clean code and best practices

---

## 📞 Support

For questions or issues:
1. Check the individual project READMEs
2. Review the documentation in each project folder
3. Open an issue on GitHub
4. Contact the author directly

---

<div align="center">

**Made with ❤️ for Intuit Build Coding Challenge**

⭐ If you find this useful, please star this repository! ⭐

</div>
