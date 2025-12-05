package com.intuit.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for aggregated sales statistics.
 * Used to hold results from various analytics operations.
 * 
 * @author Intuit Challenge
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatistics {
    private String category;
    private Double totalSales;
    private Long orderCount;
    private Double averageSales;
    private Double minSales;
    private Double maxSales;
    private Integer totalQuantity;
    
    /**
     * Constructor for basic statistics without quantity
     */
    public SalesStatistics(String category, Double totalSales, Long orderCount, Double averageSales) {
        this.category = category;
        this.totalSales = totalSales;
        this.orderCount = orderCount;
        this.averageSales = averageSales;
    }
    
    @Override
    public String toString() {
        return String.format("%-30s | Total: $%,.2f | Orders: %,d | Avg: $%,.2f | Min: $%,.2f | Max: $%,.2f",
                category,
                totalSales != null ? totalSales : 0.0,
                orderCount != null ? orderCount : 0L,
                averageSales != null ? averageSales : 0.0,
                minSales != null ? minSales : 0.0,
                maxSales != null ? maxSales : 0.0);
    }
}
