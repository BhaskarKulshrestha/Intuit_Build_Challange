package com.intuit.challenge;

import com.intuit.challenge.model.SalesRecord;
import com.intuit.challenge.model.SalesStatistics;
import com.intuit.challenge.service.CsvReaderService;
import com.intuit.challenge.service.SalesAnalyticsService;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * Main application class that demonstrates CSV data analysis using Java Streams API.
 * This application showcases:
 * - Functional programming paradigms
 * - Stream operations (map, filter, reduce, collect)
 * - Data aggregation and grouping
 * - Lambda expressions
 * - Method references
 * 
 * @author Intuit Challenge
 * @version 1.0.0
 */
public class SalesDataAnalysisApp {
    
    private static final String DEFAULT_CSV_FILE = "sales_data_sample.csv";
    private static final String SEPARATOR = "=".repeat(100);
    private static final String DASH_SEPARATOR = "-".repeat(100);
    
    /**
     * Main entry point of the application.
     * 
     * @param args Command line arguments (optional: path to CSV file)
     */
    public static void main(String[] args) {
        try {
            // Determine CSV file path
            String csvFilePath = args.length > 0 ? args[0] : DEFAULT_CSV_FILE;
            
            System.out.println(SEPARATOR);
            System.out.println("CSV DATA ANALYSIS - SALES DATA ANALYTICS");
            System.out.println("Demonstrating Java Streams API and Functional Programming");
            System.out.println(SEPARATOR);
            System.out.println();
            
            // Step 1: Read CSV data
            System.out.println("Step 1: Reading CSV data from: " + csvFilePath);
            CsvReaderService csvReader = new CsvReaderService();
            List<SalesRecord> salesRecords = csvReader.readSalesData(csvFilePath);
            System.out.println("✓ Successfully loaded " + salesRecords.size() + " sales records");
            System.out.println();
            
            // Step 2: Initialize analytics service
            System.out.println("Step 2: Initializing analytics service...");
            SalesAnalyticsService analytics = new SalesAnalyticsService(salesRecords);
            System.out.println("✓ Analytics service ready");
            System.out.println();
            
            // Step 3: Perform various analytics operations
            performAnalytics(analytics);
            
            System.out.println(SEPARATOR);
            System.out.println("Analysis completed successfully!");
            System.out.println(SEPARATOR);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Performs and displays various analytics operations on the sales data.
     * 
     * @param analytics SalesAnalyticsService instance
     */
    private static void performAnalytics(SalesAnalyticsService analytics) {
        // 1. Overall Sales Statistics
        displayOverallStatistics(analytics);
        
        // 2. Sales by Product Line
        displaySalesByProductLine(analytics);
        
        // 3. Sales by Country
        displaySalesByCountry(analytics);
        
        // 4. Sales by Year
        displaySalesByYear(analytics);
        
        // 5. Sales by Quarter
        displaySalesByQuarter(analytics);
        
        // 6. Sales by Deal Size
        displaySalesByDealSize(analytics);
        
        // 7. Top Customers
        displayTopCustomers(analytics);
        
        // 8. Top Products by Quantity
        displayTopProductsByQuantity(analytics);
        
        // 9. Sales by Territory
        displaySalesByTerritory(analytics);
        
        // 10. Year-over-Year Growth
        displayYearOverYearGrowth(analytics);
        
        // 11. Order Status Distribution
        displayOrderStatusDistribution(analytics);
        
        // 12. Monthly Sales Trend
        displayMonthlySalesTrend(analytics, 2004);
        
        // 13. High-Value vs Low-Value Orders
        displayOrderValuePartition(analytics);
    }
    
    /**
     * Displays overall sales statistics using DoubleSummaryStatistics.
     */
    private static void displayOverallStatistics(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("1. OVERALL SALES STATISTICS");
        System.out.println(SEPARATOR);
        
        DoubleSummaryStatistics stats = analytics.getSummaryStatistics();
        System.out.printf("Total Sales:     $%,.2f%n", analytics.getTotalSales());
        System.out.printf("Average Sales:   $%,.2f%n", analytics.getAverageSales());
        System.out.printf("Minimum Order:   $%,.2f%n", stats.getMin());
        System.out.printf("Maximum Order:   $%,.2f%n", stats.getMax());
        System.out.printf("Total Orders:    %,d%n", stats.getCount());
        System.out.println();
    }
    
    /**
     * Displays sales statistics grouped by product line.
     */
    private static void displaySalesByProductLine(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("2. SALES BY PRODUCT LINE");
        System.out.println(SEPARATOR);
        
        Map<String, SalesStatistics> productStats = analytics.getSalesByProductLine();
        productStats.entrySet().stream()
                .sorted(Map.Entry.<String, SalesStatistics>comparingByValue(
                        (s1, s2) -> Double.compare(s2.getTotalSales(), s1.getTotalSales()))
                )
                .forEach(entry -> System.out.println(entry.getValue()));
        System.out.println();
    }
    
    /**
     * Displays top countries by total sales.
     */
    private static void displaySalesByCountry(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("3. TOP 10 COUNTRIES BY SALES");
        System.out.println(SEPARATOR);
        
        analytics.getSalesByCountry().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> System.out.printf("%-30s | $%,.2f%n", 
                        entry.getKey(), entry.getValue()));
        System.out.println();
    }
    
    /**
     * Displays sales statistics by year.
     */
    private static void displaySalesByYear(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("4. SALES BY YEAR");
        System.out.println(SEPARATOR);
        
        analytics.getSalesByYear().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getValue()));
        System.out.println();
    }
    
    /**
     * Displays sales statistics by quarter.
     */
    private static void displaySalesByQuarter(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("5. SALES BY QUARTER (Last 8 Quarters)");
        System.out.println(SEPARATOR);
        
        analytics.getSalesByQuarter().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .skip(Math.max(0, analytics.getSalesByQuarter().size() - 8))
                .forEach(entry -> System.out.println(entry.getValue()));
        System.out.println();
    }
    
    /**
     * Displays sales statistics by deal size.
     */
    private static void displaySalesByDealSize(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("6. SALES BY DEAL SIZE");
        System.out.println(SEPARATOR);
        
        analytics.getSalesByDealSize().values()
                .forEach(System.out::println);
        System.out.println();
    }
    
    /**
     * Displays top customers by total sales.
     */
    private static void displayTopCustomers(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("7. TOP 10 CUSTOMERS BY TOTAL SALES");
        System.out.println(SEPARATOR);
        
        List<SalesStatistics> topCustomers = analytics.getTopCustomers(10);
        int rank = 1;
        for (SalesStatistics customer : topCustomers) {
            System.out.printf("#%-2d | %s%n", rank++, customer);
        }
        System.out.println();
    }
    
    /**
     * Displays top products by quantity sold.
     */
    private static void displayTopProductsByQuantity(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("8. TOP 10 PRODUCTS BY QUANTITY SOLD");
        System.out.println(SEPARATOR);
        
        Map<String, Integer> topProducts = analytics.getTopProductsByQuantity(10);
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topProducts.entrySet()) {
            System.out.printf("#%-2d | %-20s | Quantity: %,d units%n", 
                    rank++, entry.getKey(), entry.getValue());
        }
        System.out.println();
    }
    
    /**
     * Displays sales statistics by territory.
     */
    private static void displaySalesByTerritory(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("9. SALES BY TERRITORY");
        System.out.println(SEPARATOR);
        
        analytics.getSalesByTerritory().entrySet().stream()
                .sorted(Map.Entry.<String, SalesStatistics>comparingByValue(
                        (s1, s2) -> Double.compare(s2.getTotalSales(), s1.getTotalSales()))
                )
                .forEach(entry -> System.out.println(entry.getValue()));
        System.out.println();
    }
    
    /**
     * Displays year-over-year growth rates.
     */
    private static void displayYearOverYearGrowth(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("10. YEAR-OVER-YEAR GROWTH");
        System.out.println(SEPARATOR);
        
        analytics.getYearOverYearGrowth().forEach((year, growth) -> 
                System.out.printf("Year %d: %+.2f%%%n", year, growth));
        System.out.println();
    }
    
    /**
     * Displays distribution of order statuses.
     */
    private static void displayOrderStatusDistribution(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("11. ORDER STATUS DISTRIBUTION");
        System.out.println(SEPARATOR);
        
        Map<String, Long> statusDist = analytics.getOrderStatusDistribution();
        long total = statusDist.values().stream().mapToLong(Long::longValue).sum();
        
        statusDist.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / total;
                    System.out.printf("%-20s | Count: %,5d | %.2f%%%n", 
                            entry.getKey(), entry.getValue(), percentage);
                });
        System.out.println();
    }
    
    /**
     * Displays monthly sales trend for a specific year.
     */
    private static void displayMonthlySalesTrend(SalesAnalyticsService analytics, int year) {
        System.out.println(SEPARATOR);
        System.out.println("12. MONTHLY SALES TREND FOR " + year);
        System.out.println(SEPARATOR);
        
        Map<Integer, Double> monthlyTrend = analytics.getMonthlySalesTrend(year);
        monthlyTrend.forEach((month, sales) -> 
                System.out.printf("Month %2d: $%,.2f%n", month, sales));
        System.out.println();
    }
    
    /**
     * Displays partition of orders by value (high vs low).
     */
    private static void displayOrderValuePartition(SalesAnalyticsService analytics) {
        System.out.println(SEPARATOR);
        System.out.println("13. ORDER VALUE PARTITION (>$5000 vs <=$5000)");
        System.out.println(SEPARATOR);
        
        Map<Boolean, List<SalesRecord>> partition = analytics.partitionByOrderValue();
        
        long highValueCount = partition.get(true).size();
        double highValueTotal = partition.get(true).stream()
                .mapToDouble(SalesRecord::getSales)
                .sum();
        
        long lowValueCount = partition.get(false).size();
        double lowValueTotal = partition.get(false).stream()
                .mapToDouble(SalesRecord::getSales)
                .sum();
        
        System.out.printf("High-Value Orders (>$5000):%n");
        System.out.printf("  Count: %,d | Total: $%,.2f | Average: $%,.2f%n", 
                highValueCount, highValueTotal, highValueTotal / highValueCount);
        
        System.out.printf("Low-Value Orders (<=$5000):%n");
        System.out.printf("  Count: %,d | Total: $%,.2f | Average: $%,.2f%n", 
                lowValueCount, lowValueTotal, lowValueTotal / lowValueCount);
        System.out.println();
    }
}
