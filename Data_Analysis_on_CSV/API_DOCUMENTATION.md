# API Documentation

## Table of Contents
1. [APIs Used in the Project](#apis-used-in-the-project)
2. [Functional Programming Implementation](#functional-programming-implementation)
3. [Stream Operations Implementation](#stream-operations-implementation)
4. [Data Aggregation Implementation](#data-aggregation-implementation)
5. [Lambda Expressions Implementation](#lambda-expressions-implementation)

---

## APIs Used in the Project

### 1. OpenCSV API (v5.8)
**Purpose**: CSV file parsing and reading  
**Package**: `com.opencsv`  
**Maven Dependency**:
```xml
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.8</version>
</dependency>
```

**Key Classes Used**:
- `CSVReader` - Main class for reading CSV files
- `CsvException` - Exception handling for CSV parsing errors

**Implementation Location**: `CsvReaderService.java`

---

### 2. Java Streams API (Java 11+)
**Purpose**: Functional-style operations on sequences of elements  
**Package**: `java.util.stream`  

**Key Interfaces**:
- `Stream<T>` - Sequential/parallel stream of elements
- `IntStream`, `LongStream`, `DoubleStream` - Primitive specialized streams
- `Collectors` - Implementations of reduction operations

**Implementation Locations**: 
- `CsvReaderService.java`
- `SalesAnalyticsService.java`
- `SalesDataAnalysisApp.java`

---

### 3. Java Collections API
**Purpose**: Data structure management  
**Package**: `java.util`  

**Key Classes Used**:
- `List<T>` - Ordered collection
- `Map<K, V>` - Key-value pairs
- `TreeMap<K, V>` - Sorted map
- `LinkedHashMap<K, V>` - Ordered map
- `ArrayList<T>` - Resizable array implementation
- `Comparator<T>` - Comparison function

**Implementation Locations**: All service and model classes

---

### 4. Java Collectors API
**Purpose**: Mutable reduction operations  
**Package**: `java.util.stream.Collectors`  

**Key Methods Used**:
- `toList()` - Collect to list
- `groupingBy()` - Group elements
- `partitioningBy()` - Binary split
- `summingDouble()` - Sum numeric values
- `averagingDouble()` - Calculate average
- `counting()` - Count elements
- `collectingAndThen()` - Transform result

**Implementation Location**: `SalesAnalyticsService.java`

---

### 5. Java NIO Files API
**Purpose**: File I/O operations with lazy evaluation  
**Package**: `java.nio.file`  

**Key Classes Used**:
- `Files` - File operations utility
- `Path` - File path representation

**Implementation Location**: `CsvReaderService.java`

---

### 6. Lombok API (v1.18.28)
**Purpose**: Reduce boilerplate code through annotations  
**Package**: `org.projectlombok`  

**Annotations Used**:
- `@Data` - Generates getters, setters, equals, hashCode, toString
- `@Builder` - Implements builder pattern
- `@NoArgsConstructor` - Generates no-args constructor
- `@AllArgsConstructor` - Generates all-args constructor

**Implementation Locations**: 
- `SalesRecord.java`
- `SalesStatistics.java`

---

### 7. Java Time API
**Purpose**: Date and time handling  
**Package**: `java.time`  

**Key Classes Used**:
- `LocalDate` - Date without time-zone
- `DateTimeFormatter` - Date/time formatting

**Implementation Location**: `SalesRecord.java`

---

### 8. Java Functional Interfaces
**Purpose**: Enable functional programming paradigm  
**Package**: `java.util.function`  

**Key Interfaces Used**:
- `Function<T, R>` - Function that accepts one argument
- `Predicate<T>` - Boolean-valued function
- `Consumer<T>` - Operation that accepts argument and returns no result
- `Supplier<T>` - Supplier of results

**Implementation Location**: `SalesAnalyticsService.java`

---

### 9. JUnit 5 API (v5.9.3)
**Purpose**: Unit testing framework  
**Package**: `org.junit.jupiter`  

**Key Annotations Used**:
- `@Test` - Mark test methods
- `@BeforeEach` - Setup before each test
- `@ParameterizedTest` - Parameterized tests
- `@TestInstance` - Test instance lifecycle

**Implementation Locations**: All test classes

---

### 10. AssertJ API (v3.24.2)
**Purpose**: Fluent assertions for tests  
**Package**: `org.assertj.core.api`  

**Key Methods Used**:
- `assertThat()` - Entry point for assertions
- Fluent assertion chains

**Implementation Locations**: All test classes

---

## Functional Programming Implementation

### Overview
Functional programming is achieved through:
- Immutable data structures
- Pure functions
- Higher-order functions
- Function composition
- Declarative style

### Implementation by File

#### 1. `SalesRecord.java`
**Functional Concepts**:
- **Immutability**: Using `@Data` and `@Builder` for immutable objects
- **Pure Functions**: Static factory method `fromCsvRow()`

```java
// Pure function - no side effects, deterministic
public static SalesRecord fromCsvRow(String[] row) {
    return SalesRecord.builder()
        .orderNumber(parseInteger(row[0]))
        .quantityOrdered(parseInteger(row[1]))
        // ... more fields
        .build();
}

// Helper pure functions
private static Integer parseInteger(String value) {
    if (value == null || value.trim().isEmpty()) {
        return null;
    }
    try {
        return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
        return null;
    }
}
```

**Lines**: 26-95

---

#### 2. `CsvReaderService.java`
**Functional Concepts**:
- **Declarative Programming**: Stream-based file reading
- **Function Composition**: Chaining stream operations
- **Lazy Evaluation**: Using `Files.lines()`

```java
// Declarative style - what to do, not how
public List<SalesRecord> readSalesData(String filePath) throws IOException, CsvException {
    validateFile(filePath);
    
    try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
        List<String[]> rows = csvReader.readAll();
        
        return rows.stream()
                .skip(1)  // Skip header row
                .filter(row -> row.length > 0 && row[0] != null)
                .map(SalesRecord::fromCsvRow)  // Method reference
                .collect(Collectors.toList());
    }
}

// Lazy evaluation for memory efficiency
public List<SalesRecord> readSalesDataLazy(String filePath) throws IOException {
    try (var lines = Files.lines(Path.of(filePath))) {
        return lines
            .skip(1)
            .filter(line -> !line.trim().isEmpty())
            .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
            .map(SalesRecord::fromCsvRow)
            .collect(Collectors.toList());
    }
}
```

**Lines**: 32-74

---

#### 3. `SalesAnalyticsService.java`
**Functional Concepts**:
- **Higher-Order Functions**: Functions that take/return functions
- **Function Composition**: Complex operations built from simple ones
- **Immutable Operations**: Never modify original data

```java
// Higher-order function accepting Function parameter
private SalesStatistics calculateStatistics(
    List<SalesRecord> records, 
    Function<SalesRecord, String> categoryExtractor  // Function as parameter
) {
    DoubleSummaryStatistics stats = records.stream()
        .filter(r -> r.getSales() != null)
        .mapToDouble(SalesRecord::getSales)
        .summaryStatistics();
    
    // ... create and return statistics
}

// Function composition example
public Map<String, SalesStatistics> getSalesByProductLine() {
    return salesRecords.stream()
        .filter(record -> record.getProductLine() != null)  // Operation 1
        .collect(Collectors.groupingBy(                      // Operation 2
            SalesRecord::getProductLine,                     // Operation 3
            Collectors.collectingAndThen(                    // Operation 4
                Collectors.toList(),
                records -> calculateStatistics(records, SalesRecord::getProductLine)
            )
        ));
}
```

**Lines**: 68-80, 332-357

---

#### 4. `SalesDataAnalysisApp.java`
**Functional Concepts**:
- **Method References**: Clean function passing
- **Declarative Iteration**: Using forEach with method references

```java
// Method reference usage
analytics.getSalesByProductLine().values()
    .forEach(System.out::println);  // Method reference

// Declarative sorting and displaying
analytics.getSalesByCountry().entrySet().stream()
    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
    .limit(10)
    .forEach(entry -> System.out.printf("%-30s | $%,.2f%n", 
        entry.getKey(), entry.getValue()));
```

**Lines**: 150-160, 180-189

---

## Stream Operations Implementation

### Stream Pipeline Components
1. **Source** - Origin of data
2. **Intermediate Operations** - Transform stream (lazy)
3. **Terminal Operations** - Produce result (eager)

### Implementation by File and Operation Type

#### 1. `CsvReaderService.java`

**Filter Operations**:
```java
// Line 41-43: Filter non-empty rows
rows.stream()
    .skip(1)
    .filter(row -> row.length > 0 && row[0] != null && !row[0].trim().isEmpty())
    .map(SalesRecord::fromCsvRow)
    .collect(Collectors.toList());
```

**Map Operations**:
```java
// Line 44: Transform String[] to SalesRecord
.map(SalesRecord::fromCsvRow)

// Line 64: Split CSV line
.map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
```

---

#### 2. `SalesAnalyticsService.java`

**Aggregation Operations**:
```java
// Lines 42-47: Sum operation
public double getTotalSales() {
    return salesRecords.stream()
        .filter(record -> record.getSales() != null)
        .mapToDouble(SalesRecord::getSales)
        .sum();  // Terminal operation
}

// Lines 55-61: Average operation
public double getAverageSales() {
    return salesRecords.stream()
        .filter(record -> record.getSales() != null)
        .mapToDouble(SalesRecord::getSales)
        .average()
        .orElse(0.0);
}
```

**GroupingBy Operations**:
```java
// Lines 68-80: Group by product line
public Map<String, SalesStatistics> getSalesByProductLine() {
    return salesRecords.stream()
        .filter(record -> record.getProductLine() != null && record.getSales() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getProductLine,
            Collectors.collectingAndThen(
                Collectors.toList(),
                records -> calculateStatistics(records, SalesRecord::getProductLine)
            )
        ));
}

// Lines 87-95: Simple grouping with sum
public Map<String, Double> getSalesByCountry() {
    return salesRecords.stream()
        .filter(record -> record.getCountry() != null && record.getSales() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getCountry,
            Collectors.summingDouble(SalesRecord::getSales)
        ));
}
```

**Sorting Operations**:
```java
// Lines 157-171: Sort and limit
public List<SalesStatistics> getTopCustomers(int topN) {
    return salesRecords.stream()
        .filter(record -> record.getCustomerName() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getCustomerName,
            Collectors.collectingAndThen(
                Collectors.toList(),
                records -> calculateStatistics(records, SalesRecord::getCustomerName)
            )
        ))
        .values().stream()
        .sorted(Comparator.comparing(SalesStatistics::getTotalSales).reversed())
        .limit(topN)
        .collect(Collectors.toList());
}
```

**Partitioning Operations**:
```java
// Lines 204-211: Partition by predicate
public Map<Boolean, List<SalesRecord>> partitionByOrderValue() {
    return salesRecords.stream()
        .filter(record -> record.getSales() != null)
        .collect(Collectors.partitioningBy(record -> record.getSales() > 5000.0));
}
```

**Multi-level Grouping**:
```java
// Lines 308-320: Nested grouping
public Map<String, Map<String, Double>> getAverageOrderValueByProductAndDeal() {
    return salesRecords.stream()
        .filter(record -> record.getProductLine() != null 
                && record.getDealSize() != null 
                && record.getSales() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getProductLine,
            Collectors.groupingBy(
                SalesRecord::getDealSize,
                Collectors.averagingDouble(SalesRecord::getSales)
            )
        ));
}
```

**Counting Operations**:
```java
// Lines 296-302: Count by group
public Map<String, Long> getOrderStatusDistribution() {
    return salesRecords.stream()
        .filter(record -> record.getStatus() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getStatus,
            Collectors.counting()
        ));
}
```

---

#### 3. `SalesDataAnalysisApp.java`

**Chained Operations**:
```java
// Lines 150-157: Complex stream chain
productStats.entrySet().stream()
    .sorted(Map.Entry.<String, SalesStatistics>comparingByValue(
        (s1, s2) -> Double.compare(s2.getTotalSales(), s1.getTotalSales()))
    )
    .forEach(entry -> System.out.println(entry.getValue()));

// Lines 180-189: Filter, sort, limit, iterate
analytics.getSalesByCountry().entrySet().stream()
    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
    .limit(10)
    .forEach(entry -> System.out.printf("%-30s | $%,.2f%n", 
        entry.getKey(), entry.getValue()));
```

---

## Data Aggregation Implementation

### Aggregation Techniques Used

#### 1. Statistical Aggregations
**File**: `SalesAnalyticsService.java`

**Sum Aggregation**:
```java
// Lines 42-47: Total sales
public double getTotalSales() {
    return salesRecords.stream()
        .mapToDouble(SalesRecord::getSales)
        .sum();
}

// Lines 87-95: Sum by group
public Map<String, Double> getSalesByCountry() {
    return salesRecords.stream()
        .collect(Collectors.groupingBy(
            SalesRecord::getCountry,
            Collectors.summingDouble(SalesRecord::getSales)  // Aggregation
        ));
}

// Lines 180-192: Sum integers
public Map<String, Integer> getTopProductsByQuantity(int topN) {
    return salesRecords.stream()
        .collect(Collectors.groupingBy(
            SalesRecord::getProductCode,
            Collectors.summingInt(SalesRecord::getQuantityOrdered)  // Integer sum
        ))
        // ... sort and limit
}
```

**Average Aggregation**:
```java
// Lines 55-61: Overall average
public double getAverageSales() {
    return salesRecords.stream()
        .mapToDouble(SalesRecord::getSales)
        .average()
        .orElse(0.0);
}

// Lines 308-320: Average by multiple dimensions
public Map<String, Map<String, Double>> getAverageOrderValueByProductAndDeal() {
    return salesRecords.stream()
        .collect(Collectors.groupingBy(
            SalesRecord::getProductLine,
            Collectors.groupingBy(
                SalesRecord::getDealSize,
                Collectors.averagingDouble(SalesRecord::getSales)  // Nested average
            )
        ));
}
```

**Count Aggregation**:
```java
// Lines 296-302: Count by category
public Map<String, Long> getOrderStatusDistribution() {
    return salesRecords.stream()
        .collect(Collectors.groupingBy(
            SalesRecord::getStatus,
            Collectors.counting()  // Count aggregation
        ));
}
```

**Min/Max Aggregation**:
```java
// Lines 332-357: Summary statistics with min/max
private SalesStatistics calculateStatistics(
    List<SalesRecord> records, 
    Function<SalesRecord, String> categoryExtractor
) {
    DoubleSummaryStatistics stats = records.stream()
        .filter(r -> r.getSales() != null)
        .mapToDouble(SalesRecord::getSales)
        .summaryStatistics();  // Comprehensive stats including min/max
    
    result.setMinSales(stats.getMin());
    result.setMaxSales(stats.getMax());
    // ... other statistics
}
```

---

#### 2. Grouping Aggregations

**Single-Level Grouping**:
```java
// Lines 102-112: Group by year
public Map<Integer, SalesStatistics> getSalesByYear() {
    return salesRecords.stream()
        .filter(record -> record.getYearId() != null && record.getSales() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getYearId,
            Collectors.collectingAndThen(
                Collectors.toList(),
                records -> calculateStatistics(records, r -> String.valueOf(r.getYearId()))
            )
        ));
}
```

**Multi-Level Grouping**:
```java
// Lines 120-131: Group by year and quarter
public Map<String, SalesStatistics> getSalesByQuarter() {
    return salesRecords.stream()
        .filter(record -> record.getYearId() != null 
                && record.getQtrId() != null 
                && record.getSales() != null)
        .collect(Collectors.groupingBy(
            record -> record.getYearId() + "-Q" + record.getQtrId(),
            Collectors.collectingAndThen(
                Collectors.toList(),
                records -> calculateStatistics(records, 
                    r -> r.getYearId() + "-Q" + r.getQtrId())
            )
        ));
}
```

**Temporal Grouping**:
```java
// Lines 217-227: Group by month with TreeMap for ordering
public Map<Integer, Double> getMonthlySalesTrend(int year) {
    return salesRecords.stream()
        .filter(record -> record.getYearId() != null && record.getYearId() == year)
        .filter(record -> record.getMonthId() != null && record.getSales() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getMonthId,
            TreeMap::new,  // Sorted map
            Collectors.summingDouble(SalesRecord::getSales)
        ));
}
```

---

#### 3. Partitioning Aggregations

**Binary Partitioning**:
```java
// Lines 204-211: Split into two groups
public Map<Boolean, List<SalesRecord>> partitionByOrderValue() {
    return salesRecords.stream()
        .filter(record -> record.getSales() != null)
        .collect(Collectors.partitioningBy(
            record -> record.getSales() > 5000.0  // Boolean predicate
        ));
}
```

---

#### 4. Custom Aggregations

**Complex Statistics Calculation**:
```java
// Lines 332-357: Custom aggregation logic
private SalesStatistics calculateStatistics(
    List<SalesRecord> records, 
    Function<SalesRecord, String> categoryExtractor
) {
    if (records.isEmpty()) {
        return new SalesStatistics();
    }
    
    // Multiple aggregations in one pass
    DoubleSummaryStatistics stats = records.stream()
        .filter(r -> r.getSales() != null)
        .mapToDouble(SalesRecord::getSales)
        .summaryStatistics();
    
    int totalQuantity = records.stream()
        .filter(r -> r.getQuantityOrdered() != null)
        .mapToInt(SalesRecord::getQuantityOrdered)
        .sum();
    
    SalesStatistics result = new SalesStatistics();
    result.setCategory(categoryExtractor.apply(records.get(0)));
    result.setTotalSales(stats.getSum());
    result.setOrderCount(stats.getCount());
    result.setAverageSales(stats.getAverage());
    result.setMinSales(stats.getMin());
    result.setMaxSales(stats.getMax());
    result.setTotalQuantity(totalQuantity);
    
    return result;
}
```

**Year-over-Year Growth**:
```java
// Lines 263-287: Complex temporal aggregation
public Map<Integer, Double> getYearOverYearGrowth() {
    // First aggregation: yearly totals
    Map<Integer, Double> yearlyTotals = salesRecords.stream()
        .filter(record -> record.getYearId() != null && record.getSales() != null)
        .collect(Collectors.groupingBy(
            SalesRecord::getYearId,
            TreeMap::new,
            Collectors.summingDouble(SalesRecord::getSales)
        ));
    
    // Second aggregation: calculate growth
    Map<Integer, Double> growthRates = new LinkedHashMap<>();
    List<Map.Entry<Integer, Double>> entries = new ArrayList<>(yearlyTotals.entrySet());
    
    for (int i = 1; i < entries.size(); i++) {
        int currentYear = entries.get(i).getKey();
        double currentSales = entries.get(i).getValue();
        double previousSales = entries.get(i - 1).getValue();
        
        double growthRate = ((currentSales - previousSales) / previousSales) * 100;
        growthRates.put(currentYear, growthRate);
    }
    
    return growthRates;
}
```

---

## Lambda Expressions Implementation

### Lambda Expression Types and Usage

#### 1. Filter Predicates
**File**: `SalesAnalyticsService.java`

```java
// Line 44: Null check predicate
.filter(record -> record.getSales() != null)

// Line 71: Multiple condition predicate
.filter(record -> record.getProductLine() != null && record.getSales() != null)

// Line 207: Value comparison predicate
.collect(Collectors.partitioningBy(record -> record.getSales() > 5000.0))

// Line 239: Multiple filters
.filter(record -> record.getSales() != null && record.getSales() >= minSales)
.filter(record -> status == null || status.equals(record.getStatus()))
```

**File**: `CsvReaderService.java`
```java
// Line 42: Complex condition
.filter(row -> row.length > 0 && row[0] != null && !row[0].trim().isEmpty())

// Line 62: String operation
.filter(line -> !line.trim().isEmpty())
```

---

#### 2. Mapping Functions
**File**: `SalesAnalyticsService.java`

```java
// Line 124: String concatenation
.collect(Collectors.groupingBy(
    record -> record.getYearId() + "-Q" + record.getQtrId(),  // Lambda mapping
    // ...
))

// Line 126: Lambda in collectingAndThen
records -> calculateStatistics(records, r -> r.getYearId() + "-Q" + r.getQtrId())

// Line 272: Complex transformation
r -> String.valueOf(r.getYearId())
```

**File**: `CsvReaderService.java`
```java
// Line 64: String splitting
.map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
```

---

#### 3. Comparison Lambdas
**File**: `SalesAnalyticsService.java`

```java
// Line 166: Comparator lambda
.sorted(Comparator.comparing(SalesStatistics::getTotalSales).reversed())

// Lines 185-191: Multi-step comparison
.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
```

**File**: `SalesDataAnalysisApp.java`
```java
// Lines 151-154: Custom comparator
.sorted(Map.Entry.<String, SalesStatistics>comparingByValue(
    (s1, s2) -> Double.compare(s2.getTotalSales(), s1.getTotalSales())
))

// Line 181: Reversed comparison
.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
```

---

#### 4. Consumer Lambdas
**File**: `SalesDataAnalysisApp.java`

```java
// Line 155: forEach with lambda
.forEach(entry -> System.out.println(entry.getValue()))

// Lines 183-184: Formatted output lambda
.forEach(entry -> System.out.printf("%-30s | $%,.2f%n", 
    entry.getKey(), entry.getValue()))

// Lines 256-258: Multi-line lambda
.forEach(entry -> {
    double percentage = (entry.getValue() * 100.0) / total;
    System.out.printf("%-20s | Count: %,5d | %.2f%%%n", 
        entry.getKey(), entry.getValue(), percentage);
})
```

---

#### 5. Supplier Lambdas
**File**: `SalesAnalyticsService.java`

```java
// Line 223: TreeMap supplier
Collectors.groupingBy(
    SalesRecord::getMonthId,
    TreeMap::new,  // Supplier for sorted map
    Collectors.summingDouble(SalesRecord::getSales)
)

// Line 189: LinkedHashMap supplier
.collect(Collectors.toMap(
    Map.Entry::getKey,
    Map.Entry::getValue,
    (e1, e2) -> e1,  // Merge function
    LinkedHashMap::new  // Map supplier
))
```

---

#### 6. Complex Lambda Expressions

**Multi-line Lambdas**:
```java
// File: SalesAnalyticsService.java, Lines 332-357
private SalesStatistics calculateStatistics(
    List<SalesRecord> records, 
    Function<SalesRecord, String> categoryExtractor  // Lambda parameter
) {
    // Lambda used internally
    DoubleSummaryStatistics stats = records.stream()
        .filter(r -> r.getSales() != null)  // Lambda predicate
        .mapToDouble(SalesRecord::getSales)
        .summaryStatistics();
    
    int totalQuantity = records.stream()
        .filter(r -> r.getQuantityOrdered() != null)  // Lambda predicate
        .mapToInt(SalesRecord::getQuantityOrdered)
        .sum();
    
    // Lambda application
    result.setCategory(categoryExtractor.apply(records.get(0)));
    
    return result;
}
```

**Nested Lambdas**:
```java
// File: SalesAnalyticsService.java, Lines 308-320
public Map<String, Map<String, Double>> getAverageOrderValueByProductAndDeal() {
    return salesRecords.stream()
        .filter(record -> record.getProductLine() != null    // Lambda 1
                && record.getDealSize() != null              // Lambda 1
                && record.getSales() != null)                // Lambda 1
        .collect(Collectors.groupingBy(
            SalesRecord::getProductLine,                     // Method reference
            Collectors.groupingBy(
                SalesRecord::getDealSize,                    // Method reference
                Collectors.averagingDouble(SalesRecord::getSales)  // Method reference
            )
        ));
}
```

---

## Summary Table

| Requirement | Primary Files | Key Lines | API Used |
|-------------|--------------|-----------|----------|
| **Functional Programming** | `SalesRecord.java`<br>`CsvReaderService.java`<br>`SalesAnalyticsService.java` | 26-95<br>32-74<br>68-357 | Streams API<br>Functional Interfaces<br>Immutable patterns |
| **Stream Operations** | `CsvReaderService.java`<br>`SalesAnalyticsService.java`<br>`SalesDataAnalysisApp.java` | 39-45<br>42-320<br>150-270 | java.util.stream.*<br>IntStream, DoubleStream<br>Stream<T> |
| **Data Aggregation** | `SalesAnalyticsService.java` | 42-61 (Stats)<br>68-302 (Grouping)<br>332-357 (Custom) | Collectors API<br>DoubleSummaryStatistics<br>Custom collectors |
| **Lambda Expressions** | All service classes<br>Main application | Throughout codebase<br>44, 71, 124, 207, 239<br>151-154, 256-258 | Predicate<br>Function<br>Consumer<br>Supplier<br>Comparator |

---

## Code Examples Cross-Reference

### Quick Reference by Feature

| Feature | Example Location | Line Numbers |
|---------|------------------|--------------|
| CSV Parsing with Streams | `CsvReaderService.java` | 32-45 |
| Filter Operations | `SalesAnalyticsService.java` | 44, 71, 89, 104 |
| Map Operations | `CsvReaderService.java` | 44, 64 |
| Reduce Operations | `SalesAnalyticsService.java` | 45, 59 |
| GroupBy Single Level | `SalesAnalyticsService.java` | 87-95 |
| GroupBy Multi Level | `SalesAnalyticsService.java` | 308-320 |
| Partition | `SalesAnalyticsService.java` | 204-211 |
| Sort and Limit | `SalesAnalyticsService.java` | 157-171 |
| Custom Collectors | `SalesAnalyticsService.java` | 73-78 |
| Summary Statistics | `SalesAnalyticsService.java` | 364-370 |
| Lambda Predicates | Throughout | Multiple locations |
| Method References | Throughout | Multiple locations |
| Higher-Order Functions | `SalesAnalyticsService.java` | 332-357 |

---

## Best Practices Demonstrated

1. **Immutability**: Using final fields and builders
2. **Pure Functions**: No side effects in stream operations
3. **Declarative Style**: Focus on what, not how
4. **Method References**: Cleaner than lambdas when possible
5. **Stream Pipelines**: Efficient chaining of operations
6. **Type Safety**: Leveraging Java's type system
7. **Resource Management**: Using try-with-resources
8. **Error Handling**: Proper exception handling in streams

---

*This documentation is current as of version 1.0.0 of the CSV Data Analysis application.*
