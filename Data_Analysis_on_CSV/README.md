# CSV Data Analysis - Sales Analytics Application

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A comprehensive Java application demonstrating advanced data analysis using Java Streams API, functional programming paradigms, and various aggregation techniques on CSV sales data.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Sample Output](#sample-output)
- [Key Concepts Demonstrated](#key-concepts-demonstrated)
- [Technologies Used](#technologies-used)

## 🎯 Overview

This application showcases proficiency with Java 8+ Streams API by performing various analytical queries on sales data from a CSV file. It demonstrates functional programming paradigms, including:

- **Stream Operations**: map, filter, reduce, collect
- **Data Aggregation**: grouping, partitioning, statistical calculations
- **Lambda Expressions**: concise functional implementations
- **Method References**: cleaner code through reference syntax
- **Collectors**: custom and built-in collectors for complex aggregations

## ✨ Features

### Analytics Capabilities

1. **Overall Sales Statistics**
   - Total sales, average, min, max values
   - Order count and summary statistics

2. **Product Line Analysis**
   - Sales breakdown by product category
   - Comprehensive statistics per product line

3. **Geographic Analysis**
   - Sales by country and territory
   - Top-performing regions

4. **Temporal Analysis**
   - Yearly and quarterly trends
   - Monthly sales patterns
   - Year-over-year growth rates

5. **Customer Insights**
   - Top customers by revenue
   - Customer segmentation

6. **Product Performance**
   - Top products by quantity sold
   - Product popularity rankings

7. **Deal Size Segmentation**
   - Analysis by deal size (Small, Medium, Large)
   - Average order values

8. **Order Analysis**
   - Status distribution
   - High-value vs low-value order partitioning

## 🏗 Architecture

### Application Architecture

```
┌─────────────────────────────────────────────────────┐
│              SalesDataAnalysisApp                   │
│               (Main Application)                     │
└───────────────────┬─────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
┌───────▼──────────┐  ┌────────▼─────────────┐
│ CsvReaderService │  │ SalesAnalyticsService│
│                  │  │                      │
│ - Read CSV       │  │ - Stream Operations  │
│ - Parse Data     │  │ - Aggregations       │
│ - Validate       │  │ - Grouping           │
└──────────────────┘  │ - Statistics         │
                      └──────────────────────┘
                                │
                    ┌───────────┴──────────┐
                    │                      │
            ┌───────▼────────┐   ┌────────▼──────────┐
            │  SalesRecord   │   │ SalesStatistics   │
            │  (Model)       │   │ (DTO)             │
            └────────────────┘   └───────────────────┘
```

### Key Components

#### 1. **Model Layer**
- `SalesRecord`: Immutable data class representing a single sales transaction
- `SalesStatistics`: DTO for aggregated analytics results

#### 2. **Service Layer**
- `CsvReaderService`: Handles CSV file reading and parsing
  - Supports both eager and lazy loading
  - Validates file integrity
  - Parses CSV rows into domain objects

- `SalesAnalyticsService`: Core analytics engine
  - Implements all analytical operations
  - Uses Streams API extensively
  - Provides flexible querying capabilities

#### 3. **Application Layer**
- `SalesDataAnalysisApp`: Main entry point
  - Orchestrates data loading and analysis
  - Formats and displays results
  - Handles user interaction

### Design Patterns Used

- **Builder Pattern**: Used in `SalesRecord` for flexible object creation
- **Service Pattern**: Separation of concerns through service classes
- **Strategy Pattern**: Functional interfaces for flexible aggregations
- **DTO Pattern**: `SalesStatistics` for data transfer

## 📦 Prerequisites

- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Windows, macOS, or Linux

## 🚀 Installation

### Step 1: Clone or Download the Project

```bash
cd /path/to/project/directory
```

### Step 2: Verify Java Installation

```bash
java -version
# Should show Java 11 or higher
```

### Step 3: Verify Maven Installation

```bash
mvn -version
# Should show Maven 3.6 or higher
```

### Step 4: Build the Project

```bash
mvn clean install
```

This command will:
- Download all dependencies
- Compile source code
- Run all unit tests
- Package the application as a JAR file

## 🎮 Running the Application

### Method 1: Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.intuit.challenge.SalesDataAnalysisApp"
```

### Method 2: Using Standalone JAR

```bash
# After building with 'mvn clean package'
java -jar target/csv-data-analysis-1.0.0-standalone.jar
```

### Method 3: With Custom CSV File

```bash
java -jar target/csv-data-analysis-1.0.0-standalone.jar /path/to/your/data.csv
```

### Expected Behavior

The application will:
1. Load the CSV file (`sales_data_sample.csv` by default)
2. Parse all records
3. Perform 13 different types of analytics
4. Display formatted results to the console

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

Coverage report will be generated in `target/site/jacoco/index.html`

### Run Specific Test Class

```bash
mvn test -Dtest=SalesAnalyticsServiceTest
```

### Test Structure

```
src/test/java/
└── com/intuit/challenge/
    ├── model/
    │   └── SalesRecordTest.java          # Model unit tests
    └── service/
        ├── CsvReaderServiceTest.java     # CSV parsing tests
        └── SalesAnalyticsServiceTest.java # Analytics tests
```

### Test Coverage

- **Model Tests**: Data parsing, validation, builder pattern
- **Service Tests**: 
  - CSV reading (valid files, errors, edge cases)
  - Analytics operations (all 15+ analytical methods)
  - Stream operations and aggregations
  - Edge cases (empty data, null handling)

## 📁 Project Structure

```
Data_Analysis_on_CSV/
├── pom.xml                                 # Maven configuration
├── README.md                               # This file
├── sales_data_sample.csv                   # Sample data file
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/intuit/challenge/
│   │           ├── SalesDataAnalysisApp.java      # Main application
│   │           ├── model/
│   │           │   ├── SalesRecord.java           # Sales record model
│   │           │   └── SalesStatistics.java       # Statistics DTO
│   │           └── service/
│   │               ├── CsvReaderService.java      # CSV reader
│   │               └── SalesAnalyticsService.java # Analytics engine
│   └── test/
│       └── java/
│           └── com/intuit/challenge/
│               ├── model/
│               │   └── SalesRecordTest.java
│               └── service/
│                   ├── CsvReaderServiceTest.java
│                   └── SalesAnalyticsServiceTest.java
└── target/                                 # Build output (generated)
```

## 📊 Sample Output

```
====================================================================================================
CSV DATA ANALYSIS - SALES DATA ANALYTICS
Demonstrating Java Streams API and Functional Programming
====================================================================================================

Step 1: Reading CSV data from: sales_data_sample.csv
✓ Successfully loaded 2823 sales records

Step 2: Initializing analytics service...
✓ Analytics service ready

====================================================================================================
1. OVERALL SALES STATISTICS
====================================================================================================
Total Sales:     $10,032,628.85
Average Sales:   $3,553.89
Minimum Order:   $482.13
Maximum Order:   $14,082.80
Total Orders:    2,823

====================================================================================================
2. SALES BY PRODUCT LINE
====================================================================================================
Classic Cars                   | Total: $3,919,615.66 | Orders: 967 | Avg: $4,052.96 | ...
Vintage Cars                   | Total: $1,903,150.84 | Orders: 606 | Avg: $3,140.68 | ...
Motorcycles                    | Total: $1,166,388.34 | Orders: 331 | Avg: $3,523.92 | ...
...

====================================================================================================
3. TOP 10 COUNTRIES BY SALES
====================================================================================================
USA                            | $3,627,982.83
Spain                          | $1,215,686.92
France                         | $1,110,916.52
...

====================================================================================================
7. TOP 10 CUSTOMERS BY TOTAL SALES
====================================================================================================
#1  | Euro Shopping Channel      | Total: $912,294.11 | Orders: 259 | Avg: $3,522.76 | ...
#2  | Mini Gifts Distributors Ltd. | Total: $654,858.06 | Orders: 180 | Avg: $3,638.10 | ...
...
```

## 🔑 Key Concepts Demonstrated

### 1. Functional Programming

```java
// Lambda expressions for filtering
records.stream()
    .filter(record -> record.getSales() != null)
    .filter(record -> record.getSales() > 5000.0)
    .collect(Collectors.toList());
```

### 2. Stream Operations

```java
// Chaining multiple stream operations
return salesRecords.stream()
    .filter(record -> record.getCountry() != null)
    .collect(Collectors.groupingBy(
        SalesRecord::getCountry,
        Collectors.summingDouble(SalesRecord::getSales)
    ));
```

### 3. Data Aggregation

```java
// Complex grouping with statistics
return salesRecords.stream()
    .collect(Collectors.groupingBy(
        SalesRecord::getProductLine,
        Collectors.collectingAndThen(
            Collectors.toList(),
            records -> calculateStatistics(records)
        )
    ));
```

### 4. Method References

```java
// Using method references for cleaner code
.mapToDouble(SalesRecord::getSales)
.collect(Collectors.groupingBy(SalesRecord::getCountry))
.sorted(Comparator.comparing(SalesStatistics::getTotalSales).reversed())
```

### 5. Partitioning

```java
// Partition data based on predicate
return salesRecords.stream()
    .collect(Collectors.partitioningBy(
        record -> record.getSales() > 5000.0
    ));
```

### 6. Custom Collectors

```java
// Using summary statistics
DoubleSummaryStatistics stats = records.stream()
    .mapToDouble(SalesRecord::getSales)
    .summaryStatistics();
```

## 🛠 Technologies Used

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 11+ | Core programming language |
| Maven | 3.6+ | Build automation & dependency management |
| OpenCSV | 5.8 | CSV file parsing |
| Lombok | 1.18.28 | Reduce boilerplate code |
| JUnit 5 | 5.9.3 | Unit testing framework |
| AssertJ | 3.24.2 | Fluent assertions for tests |
| Mockito | 5.3.1 | Mocking framework |

## 📝 Code Quality Features

- ✅ **Comprehensive Comments**: Every class and method documented
- ✅ **Clean Code**: Following Java best practices
- ✅ **SOLID Principles**: Single responsibility, dependency injection
- ✅ **Immutable Objects**: Using Lombok's `@Data` and builders
- ✅ **Error Handling**: Proper exception handling throughout
- ✅ **Test Coverage**: Extensive unit tests for all components
- ✅ **Functional Style**: Leveraging Java's functional features

## 🎓 Learning Outcomes

This project demonstrates:

1. **Stream API Mastery**: Complex stream operations and pipelines
2. **Functional Programming**: Lambda expressions, method references
3. **Data Processing**: Efficient CSV parsing and data transformation
4. **Aggregation Techniques**: Grouping, partitioning, collecting
5. **Testing Skills**: Comprehensive unit testing with JUnit 5
6. **Clean Architecture**: Well-structured, maintainable code
7. **Best Practices**: Modern Java development patterns

## 📄 License

This project is licensed under the MIT License.

## 👤 Author

**Intuit Build Challenge - Data Analysis on CSV**

---

**Happy Coding! 🚀**
