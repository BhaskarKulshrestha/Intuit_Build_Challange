package com.intuit.challenge.service;

import com.intuit.challenge.model.SalesRecord;
import com.intuit.challenge.model.SalesStatistics;

import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for performing various analytics operations on sales data.
 * Demonstrates advanced use of Java Streams API, including:
 * - Filtering and mapping operations
 * - Grouping and partitioning
 * - Statistical aggregations
 * - Custom collectors
 * - Lambda expressions
 * 
 * @author Intuit Challenge
 * @version 1.0.0
 */
public class SalesAnalyticsService {
    
    private final List<SalesRecord> salesRecords;
    
    /**
     * Constructor initializing the service with sales data.
     * 
     * @param salesRecords List of sales records to analyze
     */
    public SalesAnalyticsService(List<SalesRecord> salesRecords) {
        this.salesRecords = salesRecords != null ? salesRecords : new ArrayList<>();
    }
    
    /**
     * Calculates total sales amount across all records.
     * Demonstrates use of reduce operation with lambda expression.
     * 
     * @return Total sales amount
     */
    public double getTotalSales() {
        return salesRecords.stream()
                .filter(record -> record.getSales() != null)
                .mapToDouble(SalesRecord::getSales)
                .sum();
    }
    
    /**
     * Gets the average order value across all sales.
     * Demonstrates use of average aggregation.
     * 
     * @return Average sales value
     */
    public double getAverageSales() {
        return salesRecords.stream()
                .filter(record -> record.getSales() != null)
                .mapToDouble(SalesRecord::getSales)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Groups sales by product line and calculates statistics for each.
     * Demonstrates grouping, mapping, and complex aggregations using collectors.
     * 
     * @return Map of product line to sales statistics
     */
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
    
    /**
     * Groups sales by country and calculates total sales for each.
     * Demonstrates grouping and summation.
     * 
     * @return Map of country to total sales
     */
    public Map<String, Double> getSalesByCountry() {
        return salesRecords.stream()
                .filter(record -> record.getCountry() != null && record.getSales() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getCountry,
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
    }
    
    /**
     * Groups sales by year and calculates statistics.
     * Demonstrates grouping by temporal data.
     * 
     * @return Map of year to sales statistics
     */
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
    
    /**
     * Groups sales by quarter and calculates statistics.
     * Demonstrates multi-level grouping.
     * 
     * @return Map of year-quarter to sales statistics
     */
    public Map<String, SalesStatistics> getSalesByQuarter() {
        return salesRecords.stream()
                .filter(record -> record.getYearId() != null && record.getQtrId() != null && record.getSales() != null)
                .collect(Collectors.groupingBy(
                        record -> record.getYearId() + "-Q" + record.getQtrId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                records -> calculateStatistics(records, r -> r.getYearId() + "-Q" + r.getQtrId())
                        )
                ));
    }
    
    /**
     * Groups sales by deal size (Small, Medium, Large).
     * Demonstrates categorical grouping.
     * 
     * @return Map of deal size to sales statistics
     */
    public Map<String, SalesStatistics> getSalesByDealSize() {
        return salesRecords.stream()
                .filter(record -> record.getDealSize() != null && record.getSales() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getDealSize,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                records -> calculateStatistics(records, SalesRecord::getDealSize)
                        )
                ));
    }
    
    /**
     * Gets top N customers by total sales.
     * Demonstrates sorting, limiting, and complex stream operations.
     * 
     * @param topN Number of top customers to return
     * @return List of top customers with their sales statistics
     */
    public List<SalesStatistics> getTopCustomers(int topN) {
        return salesRecords.stream()
                .filter(record -> record.getCustomerName() != null && record.getSales() != null)
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
    
    /**
     * Gets top N products by quantity sold.
     * Demonstrates grouping by product and summing quantities.
     * 
     * @param topN Number of top products to return
     * @return Map of product code to total quantity ordered
     */
    public Map<String, Integer> getTopProductsByQuantity(int topN) {
        return salesRecords.stream()
                .filter(record -> record.getProductCode() != null && record.getQuantityOrdered() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getProductCode,
                        Collectors.summingInt(SalesRecord::getQuantityOrdered)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    /**
     * Partitions sales into high-value (>$5000) and low-value orders.
     * Demonstrates partitioning based on predicates.
     * 
     * @return Map with true for high-value and false for low-value orders
     */
    public Map<Boolean, List<SalesRecord>> partitionByOrderValue() {
        return salesRecords.stream()
                .filter(record -> record.getSales() != null)
                .collect(Collectors.partitioningBy(record -> record.getSales() > 5000.0));
    }
    
    /**
     * Gets monthly sales trends for a specific year.
     * Demonstrates filtering and temporal grouping.
     * 
     * @param year Year to analyze
     * @return Map of month to total sales
     */
    public Map<Integer, Double> getMonthlySalesTrend(int year) {
        return salesRecords.stream()
                .filter(record -> record.getYearId() != null && record.getYearId() == year)
                .filter(record -> record.getMonthId() != null && record.getSales() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getMonthId,
                        TreeMap::new,
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
    }
    
    /**
     * Finds orders that match specific criteria using custom predicate.
     * Demonstrates flexible filtering with lambda expressions.
     * 
     * @param minSales Minimum sales amount
     * @param status Order status to filter by
     * @return List of matching sales records
     */
    public List<SalesRecord> findOrdersByCriteria(double minSales, String status) {
        return salesRecords.stream()
                .filter(record -> record.getSales() != null && record.getSales() >= minSales)
                .filter(record -> status == null || status.equals(record.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets sales statistics by territory.
     * Demonstrates regional analysis.
     * 
     * @return Map of territory to sales statistics
     */
    public Map<String, SalesStatistics> getSalesByTerritory() {
        return salesRecords.stream()
                .filter(record -> record.getTerritory() != null && record.getSales() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getTerritory,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                records -> calculateStatistics(records, SalesRecord::getTerritory)
                        )
                ));
    }
    
    /**
     * Calculates year-over-year growth rate.
     * Demonstrates comparative analysis using streams.
     * 
     * @return Map of year to growth percentage
     */
    public Map<Integer, Double> getYearOverYearGrowth() {
        Map<Integer, Double> yearlyTotals = salesRecords.stream()
                .filter(record -> record.getYearId() != null && record.getSales() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getYearId,
                        TreeMap::new,
                        Collectors.summingDouble(SalesRecord::getSales)
                ));
        
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
    
    /**
     * Gets the distribution of order statuses.
     * Demonstrates counting and grouping.
     * 
     * @return Map of status to count of orders
     */
    public Map<String, Long> getOrderStatusDistribution() {
        return salesRecords.stream()
                .filter(record -> record.getStatus() != null)
                .collect(Collectors.groupingBy(
                        SalesRecord::getStatus,
                        Collectors.counting()
                ));
    }
    
    /**
     * Calculates average order value by product line and deal size.
     * Demonstrates multi-dimensional grouping.
     * 
     * @return Nested map structure with statistics
     */
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
    
    /**
     * Helper method to calculate comprehensive statistics for a group of records.
     * This demonstrates the use of multiple stream operations and aggregations.
     * 
     * @param records List of sales records to analyze
     * @param categoryExtractor Function to extract the category name
     * @return SalesStatistics object with calculated values
     */
    private SalesStatistics calculateStatistics(List<SalesRecord> records, 
                                                Function<SalesRecord, String> categoryExtractor) {
        if (records.isEmpty()) {
            return new SalesStatistics();
        }
        
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
    
    /**
     * Gets basic summary statistics for all sales data.
     * 
     * @return DoubleSummaryStatistics object
     */
    public DoubleSummaryStatistics getSummaryStatistics() {
        return salesRecords.stream()
                .filter(record -> record.getSales() != null)
                .mapToDouble(SalesRecord::getSales)
                .summaryStatistics();
    }
}
