# Quick Start Guide

## Quick Setup (5 Minutes)

### 1. Prerequisites Check
```bash
java -version    # Should be 11 or higher
mvn -version     # Should be 3.6 or higher
```

### 2. Build the Application
```bash
cd Data_Analysis_on_CSV
mvn clean package
```

### 3. Run the Application
```bash
java -jar target/csv-data-analysis-1.0.0-standalone.jar
```

Or with a custom CSV file:
```bash
java -jar target/csv-data-analysis-1.0.0-standalone.jar path/to/your/data.csv
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SalesAnalyticsServiceTest

# Run with coverage
mvn test jacoco:report
```

## What You'll See

The application performs 13 different types of analytics:

1. **Overall Statistics** - Total sales, averages, min/max
2. **Product Line Analysis** - Sales by product category
3. **Geographic Analysis** - Top countries by sales
4. **Yearly Trends** - Sales performance by year
5. **Quarterly Analysis** - Quarterly breakdown
6. **Deal Size Segmentation** - Small, Medium, Large deals
7. **Top Customers** - Best customers by revenue
8. **Product Performance** - Top selling products
9. **Territory Analysis** - Regional performance
10. **Growth Analysis** - Year-over-year growth rates
11. **Status Distribution** - Order status breakdown
12. **Monthly Trends** - Monthly sales patterns
13. **Value Partitioning** - High vs low value orders

## Key Features Demonstrated

### Stream Operations
✅ `filter()` - Selecting records
✅ `map()` - Transforming data
✅ `collect()` - Aggregating results
✅ `groupingBy()` - Grouping data
✅ `partitioningBy()` - Splitting data
✅ `sorted()` - Ordering results
✅ `reduce()` - Combining elements

### Lambda Expressions
```java
records.stream()
    .filter(r -> r.getSales() != null)
    .filter(r -> r.getSales() > 5000)
    .sorted(Comparator.comparing(SalesRecord::getSales).reversed())
    .collect(Collectors.toList());
```

### Method References
```java
.map(SalesRecord::getCustomerName)
.collect(Collectors.groupingBy(SalesRecord::getCountry))
```

### Custom Collectors
```java
Collectors.collectingAndThen(
    Collectors.toList(),
    this::calculateStatistics
)
```

## Project Structure

```
src/
├── main/java/
│   └── com/intuit/challenge/
│       ├── SalesDataAnalysisApp.java      ← Main application
│       ├── model/
│       │   ├── SalesRecord.java           ← Data model
│       │   └── SalesStatistics.java       ← Results DTO
│       └── service/
│           ├── CsvReaderService.java      ← CSV parsing
│           └── SalesAnalyticsService.java ← Analytics engine
└── test/java/
    └── com/intuit/challenge/
        ├── model/
        │   └── SalesRecordTest.java       ← Model tests
        └── service/
            ├── CsvReaderServiceTest.java  ← Reader tests
            └── SalesAnalyticsServiceTest.java ← Analytics tests
```

## Common Issues

### Issue: Java version too old
**Solution**: Install JDK 11 or higher from https://adoptium.net/

### Issue: Maven not found
**Solution**: Install Maven from https://maven.apache.org/download.cgi

### Issue: CSV file not found
**Solution**: Make sure `sales_data_sample.csv` is in the current directory

### Issue: Out of memory
**Solution**: Run with more memory:
```bash
java -Xmx2g -jar target/csv-data-analysis-1.0.0-standalone.jar
```

## Testing

Total Tests: **36**
- Model Tests: 8
- CSV Reader Tests: 6  
- Analytics Tests: 19
- Integration Tests: 3

All tests verify:
- Data parsing correctness
- Stream operations
- Aggregation accuracy
- Edge cases handling

## Sample Output

```
====================================================================================================
CSV DATA ANALYSIS - SALES DATA ANALYTICS
Demonstrating Java Streams API and Functional Programming
====================================================================================================

Step 1: Reading CSV data from: sales_data_sample.csv
✓ Successfully loaded 2,823 sales records

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

... (continues with 13 types of analytics)
```

## Next Steps

1. **Explore the Code**: Start with `SalesDataAnalysisApp.java`
2. **Run Tests**: `mvn test` to see all tests pass
3. **Read Documentation**: Check `README.md` and `ARCHITECTURE.md`
4. **Modify Analytics**: Add your own analytics methods
5. **Try Different Data**: Use your own CSV files

## Need Help?

- 📖 Full documentation: See `README.md`
- 🏗️ Architecture details: See `ARCHITECTURE.md`
- 🧪 Test examples: Check `src/test/java/`
- 💡 Code comments: Every class and method is documented

---

**Congratulations!** You now have a fully functional CSV data analysis application demonstrating Java Streams API and functional programming! 🎉
