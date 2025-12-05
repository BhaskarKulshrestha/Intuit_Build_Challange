# Architecture Documentation

## System Architecture Overview

This document provides detailed information about the architecture, design decisions, and implementation details of the CSV Data Analysis application.

## Table of Contents

1. [High-Level Architecture](#high-level-architecture)
2. [Component Details](#component-details)
3. [Data Flow](#data-flow)
4. [Design Patterns](#design-patterns)
5. [Stream Operations Explained](#stream-operations-explained)
6. [Performance Considerations](#performance-considerations)

## High-Level Architecture

The application follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│                  (SalesDataAnalysisApp)                      │
│  - User interaction                                          │
│  - Result formatting and display                             │
│  - Orchestration of services                                 │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                      SERVICE LAYER                           │
│  ┌─────────────────────┐    ┌──────────────────────────┐   │
│  │ CsvReaderService    │    │ SalesAnalyticsService    │   │
│  │                     │    │                          │   │
│  │ - File I/O          │    │ - Stream operations      │   │
│  │ - CSV parsing       │    │ - Data aggregation       │   │
│  │ - Data validation   │    │ - Statistical analysis   │   │
│  └─────────────────────┘    └──────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                      MODEL LAYER                             │
│  ┌─────────────────────┐    ┌──────────────────────────┐   │
│  │   SalesRecord       │    │   SalesStatistics        │   │
│  │   (Entity)          │    │   (DTO)                  │   │
│  │                     │    │                          │   │
│  │ - Domain object     │    │ - Aggregated results     │   │
│  │ - CSV mapping       │    │ - Display formatting     │   │
│  └─────────────────────┘    └──────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Component Details

### 1. Presentation Layer

#### SalesDataAnalysisApp
- **Responsibility**: Main entry point and user interface
- **Key Methods**:
  - `main()`: Application entry point
  - `performAnalytics()`: Orchestrates all analytics operations
  - `displayXxx()`: Format and display results

**Design Decisions**:
- Console-based UI for simplicity and portability
- Formatted output using printf for alignment
- Clear separation of display logic from business logic

### 2. Service Layer

#### CsvReaderService
- **Responsibility**: CSV file handling and data loading
- **Key Features**:
  - Eager loading with `readSalesData()`
  - Lazy loading with `readSalesDataLazy()`
  - File validation
  - Error handling

**Implementation Details**:
```java
// Uses OpenCSV for robust parsing
try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
    return rows.stream()
        .skip(1)  // Skip header
        .map(SalesRecord::fromCsvRow)
        .collect(Collectors.toList());
}
```

#### SalesAnalyticsService
- **Responsibility**: Core analytics engine
- **Key Capabilities**:
  - 15+ analytical operations
  - Stream-based processing
  - Statistical aggregations
  - Flexible querying

**Stream Operations Examples**:

1. **Simple Aggregation**:
```java
public double getTotalSales() {
    return salesRecords.stream()
        .filter(record -> record.getSales() != null)
        .mapToDouble(SalesRecord::getSales)
        .sum();
}
```

2. **Grouping with Statistics**:
```java
public Map<String, SalesStatistics> getSalesByProductLine() {
    return salesRecords.stream()
        .collect(Collectors.groupingBy(
            SalesRecord::getProductLine,
            Collectors.collectingAndThen(
                Collectors.toList(),
                this::calculateStatistics
            )
        ));
}
```

3. **Multi-level Grouping**:
```java
public Map<String, Map<String, Double>> getAverageOrderValueByProductAndDeal() {
    return salesRecords.stream()
        .collect(Collectors.groupingBy(
            SalesRecord::getProductLine,
            Collectors.groupingBy(
                SalesRecord::getDealSize,
                Collectors.averagingDouble(SalesRecord::getSales)
            )
        ));
}
```

### 3. Model Layer

#### SalesRecord
- **Type**: Entity / Domain Object
- **Annotations**: 
  - `@Data`: Generates getters, setters, equals, hashCode, toString
  - `@Builder`: Implements builder pattern
  - `@NoArgsConstructor`, `@AllArgsConstructor`: Constructors

**Key Methods**:
- `fromCsvRow()`: Static factory method for CSV parsing
- Parsing helpers: `parseInteger()`, `parseDouble()`, `parseDate()`

#### SalesStatistics
- **Type**: Data Transfer Object (DTO)
- **Purpose**: Encapsulate aggregated results
- **Fields**: 
  - Category identifier
  - Statistical measures (total, average, min, max, count)
  - Custom toString() for formatted display

## Data Flow

### Complete Data Processing Pipeline

```
1. CSV File
   │
   ├─> CsvReaderService.readSalesData()
   │   │
   │   ├─> File Validation
   │   ├─> CSV Parsing (OpenCSV)
   │   └─> Stream Processing
   │
2. List<String[]> (Raw CSV Rows)
   │
   ├─> Stream.map(SalesRecord::fromCsvRow)
   │   │
   │   ├─> Field Parsing
   │   ├─> Type Conversion
   │   └─> Object Creation
   │
3. List<SalesRecord> (Domain Objects)
   │
   ├─> SalesAnalyticsService Constructor
   │
4. Analytics Operations
   │
   ├─> Stream Operations
   │   ├─> Filter
   │   ├─> Map
   │   ├─> Collect
   │   └─> Aggregate
   │
5. Results (SalesStatistics / Maps / Lists)
   │
   └─> Display Formatting
       └─> Console Output
```

## Design Patterns

### 1. Builder Pattern
**Used in**: SalesRecord

**Purpose**: Flexible object construction with many optional fields

**Example**:
```java
SalesRecord record = SalesRecord.builder()
    .orderNumber(10107)
    .quantityOrdered(30)
    .sales(2871.0)
    .customerName("Land of Toys Inc.")
    .build();
```

### 2. Service Pattern
**Used in**: CsvReaderService, SalesAnalyticsService

**Purpose**: Encapsulate business logic in dedicated service classes

**Benefits**:
- Single Responsibility Principle
- Testability
- Reusability

### 3. Strategy Pattern (Functional)
**Used in**: Analytics methods with Function parameters

**Example**:
```java
private SalesStatistics calculateStatistics(
    List<SalesRecord> records,
    Function<SalesRecord, String> categoryExtractor
) {
    // Flexible calculation using strategy function
}
```

### 4. Template Method Pattern
**Used in**: Display methods in main application

**Purpose**: Define algorithm structure with customizable steps

## Stream Operations Explained

### Key Stream Operations Used

#### 1. Filter
**Purpose**: Select elements matching criteria

```java
records.stream()
    .filter(record -> record.getSales() != null)
    .filter(record -> record.getSales() > 5000)
```

#### 2. Map
**Purpose**: Transform elements

```java
records.stream()
    .map(SalesRecord::getCustomerName)
    .map(String::toUpperCase)
```

#### 3. FlatMap
**Purpose**: Flatten nested structures

```java
// Not heavily used in this app, but available
```

#### 4. Collect
**Purpose**: Accumulate elements into collections

```java
.collect(Collectors.toList())
.collect(Collectors.groupingBy(...))
.collect(Collectors.summingDouble(...))
```

#### 5. Reduce
**Purpose**: Combine elements into single result

```java
.reduce(0.0, Double::sum)
.reduce(Double::max)
```

#### 6. Sorted
**Purpose**: Order elements

```java
.sorted(Comparator.comparing(SalesStatistics::getTotalSales).reversed())
```

### Advanced Collectors

#### GroupingBy
```java
Collectors.groupingBy(
    SalesRecord::getCountry,              // Classifier
    Collectors.summingDouble(...)          // Downstream collector
)
```

#### PartitioningBy
```java
Collectors.partitioningBy(
    record -> record.getSales() > 5000    // Predicate
)
```

#### CollectingAndThen
```java
Collectors.collectingAndThen(
    Collectors.toList(),                   // Collector
    this::calculateStatistics              // Finisher
)
```

## Performance Considerations

### 1. Stream Processing
- **Lazy Evaluation**: Operations are not executed until terminal operation
- **Short-circuiting**: Operations like `findFirst()`, `anyMatch()` stop early
- **Parallel Streams**: Not used in this app, but available for large datasets

### 2. Memory Management
- **Eager Loading**: Full dataset loaded into memory (suitable for medium datasets)
- **Lazy Loading Option**: `readSalesDataLazy()` for larger files
- **Streaming**: Results are computed on-demand

### 3. CSV Parsing
- **Library**: OpenCSV chosen for robustness
- **Alternative**: Custom parsing available in lazy mode
- **Validation**: Early validation prevents processing invalid data

### 4. Caching Considerations
- **Current**: No caching (stateless service)
- **Future**: Could cache intermediate results for repeated queries

### 5. Scalability
**Current Limitations**:
- In-memory processing (limited by heap size)
- Single-threaded execution

**Potential Improvements**:
- Parallel streams for large datasets
- Database integration for very large datasets
- Pagination for results

## Testing Strategy

### Unit Tests
- Test each component in isolation
- Mock dependencies
- Cover edge cases

### Integration Tests
- Test complete workflows
- Use real CSV file
- Verify end-to-end functionality

### Test Coverage Goals
- Line coverage: >80%
- Branch coverage: >75%
- Method coverage: 100%

## Future Enhancements

1. **Database Integration**: Store parsed data in database
2. **REST API**: Expose analytics as web services
3. **Caching**: Add Redis/Caffeine for performance
4. **Parallel Processing**: Use parallel streams for large datasets
5. **Real-time Analytics**: Stream processing with Kafka
6. **Visualization**: Add charts and graphs
7. **Export**: Generate PDF/Excel reports

## Conclusion

This architecture provides:
- ✅ Clear separation of concerns
- ✅ Maintainable and testable code
- ✅ Extensible design for new features
- ✅ Demonstration of modern Java features
- ✅ Best practices and design patterns
