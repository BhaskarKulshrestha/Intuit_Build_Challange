package com.intuit.challenge.service;

import com.intuit.challenge.model.SalesRecord;
import com.intuit.challenge.model.SalesStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for SalesAnalyticsService.
 * Tests various analytics operations, aggregations, and stream operations.
 */
class SalesAnalyticsServiceTest {
    
    private SalesAnalyticsService analyticsService;
    private List<SalesRecord> testRecords;
    
    @BeforeEach
    void setUp() {
        testRecords = createTestRecords();
        analyticsService = new SalesAnalyticsService(testRecords);
    }
    
    @Test
    void testGetTotalSales() {
        // When
        double totalSales = analyticsService.getTotalSales();
        
        // Then
        assertThat(totalSales).isEqualTo(15000.0);
    }
    
    @Test
    void testGetAverageSales() {
        // When
        double avgSales = analyticsService.getAverageSales();
        
        // Then
        assertThat(avgSales).isEqualTo(3000.0);
    }
    
    @Test
    void testGetSalesByProductLine() {
        // When
        Map<String, SalesStatistics> salesByProduct = analyticsService.getSalesByProductLine();
        
        // Then
        assertThat(salesByProduct).isNotEmpty();
        assertThat(salesByProduct).containsKey("Motorcycles");
        assertThat(salesByProduct).containsKey("Classic Cars");
        
        SalesStatistics motorcycleStats = salesByProduct.get("Motorcycles");
        assertThat(motorcycleStats.getTotalSales()).isEqualTo(10000.0);
    }
    
    @Test
    void testGetSalesByCountry() {
        // When
        Map<String, Double> salesByCountry = analyticsService.getSalesByCountry();
        
        // Then
        assertThat(salesByCountry).containsKey("USA");
        assertThat(salesByCountry).containsKey("France");
        assertThat(salesByCountry.get("USA")).isEqualTo(5000.0);
        assertThat(salesByCountry.get("France")).isEqualTo(10000.0);
    }
    
    @Test
    void testGetSalesByYear() {
        // When
        Map<Integer, SalesStatistics> salesByYear = analyticsService.getSalesByYear();
        
        // Then
        assertThat(salesByYear).containsKey(2003);
        assertThat(salesByYear).containsKey(2004);
        
        SalesStatistics stats2003 = salesByYear.get(2003);
        assertThat(stats2003.getTotalSales()).isEqualTo(10000.0);
    }
    
    @Test
    void testGetSalesByQuarter() {
        // When
        Map<String, SalesStatistics> salesByQuarter = analyticsService.getSalesByQuarter();
        
        // Then
        assertThat(salesByQuarter).containsKey("2003-Q1");
        assertThat(salesByQuarter).containsKey("2004-Q2");
    }
    
    @Test
    void testGetSalesByDealSize() {
        // When
        Map<String, SalesStatistics> salesByDealSize = analyticsService.getSalesByDealSize();
        
        // Then
        assertThat(salesByDealSize).containsKey("Small");
        assertThat(salesByDealSize).containsKey("Medium");
        assertThat(salesByDealSize).containsKey("Large");
    }
    
    @Test
    void testGetTopCustomers() {
        // When
        List<SalesStatistics> topCustomers = analyticsService.getTopCustomers(2);
        
        // Then
        assertThat(topCustomers).hasSize(2);
        assertThat(topCustomers.get(0).getTotalSales())
                .isGreaterThanOrEqualTo(topCustomers.get(1).getTotalSales());
    }
    
    @Test
    void testGetTopProductsByQuantity() {
        // When
        Map<String, Integer> topProducts = analyticsService.getTopProductsByQuantity(3);
        
        // Then
        assertThat(topProducts).isNotEmpty();
        assertThat(topProducts.size()).isLessThanOrEqualTo(3);
    }
    
    @Test
    void testPartitionByOrderValue() {
        // When
        Map<Boolean, List<SalesRecord>> partition = analyticsService.partitionByOrderValue();
        
        // Then
        assertThat(partition).containsKey(true);  // High value
        assertThat(partition).containsKey(false); // Low value
        // All orders are <= 5000, so no orders > 5000
        assertThat(partition.get(true)).hasSize(0);  // No orders > 5000
        assertThat(partition.get(false)).hasSize(5); // All five orders <= 5000
    }
    
    @Test
    void testGetMonthlySalesTrend() {
        // When
        Map<Integer, Double> monthlyTrend = analyticsService.getMonthlySalesTrend(2003);
        
        // Then
        assertThat(monthlyTrend).containsKey(1);
        assertThat(monthlyTrend).containsKey(3);
    }
    
    @Test
    void testFindOrdersByCriteria() {
        // When
        List<SalesRecord> highValueOrders = analyticsService.findOrdersByCriteria(4000.0, "Shipped");
        
        // Then
        assertThat(highValueOrders).isNotEmpty();
        assertThat(highValueOrders).allMatch(r -> r.getSales() >= 4000.0);
        assertThat(highValueOrders).allMatch(r -> "Shipped".equals(r.getStatus()));
    }
    
    @Test
    void testGetSalesByTerritory() {
        // When
        Map<String, SalesStatistics> salesByTerritory = analyticsService.getSalesByTerritory();
        
        // Then
        assertThat(salesByTerritory).containsKey("NA");
        assertThat(salesByTerritory).containsKey("EMEA");
    }
    
    @Test
    void testGetYearOverYearGrowth() {
        // When
        Map<Integer, Double> growth = analyticsService.getYearOverYearGrowth();
        
        // Then
        assertThat(growth).containsKey(2004);
        // 2004 sales (5000) vs 2003 sales (10000) = -50% growth
        assertThat(growth.get(2004)).isCloseTo(-50.0, within(0.1));
    }
    
    @Test
    void testGetOrderStatusDistribution() {
        // When
        Map<String, Long> statusDist = analyticsService.getOrderStatusDistribution();
        
        // Then
        assertThat(statusDist).containsKey("Shipped");
        assertThat(statusDist.get("Shipped")).isEqualTo(5L);
    }
    
    @Test
    void testGetAverageOrderValueByProductAndDeal() {
        // When
        Map<String, Map<String, Double>> avgByProductAndDeal = 
                analyticsService.getAverageOrderValueByProductAndDeal();
        
        // Then
        assertThat(avgByProductAndDeal).isNotEmpty();
        assertThat(avgByProductAndDeal).containsKey("Motorcycles");
    }
    
    @Test
    void testGetSummaryStatistics() {
        // When
        DoubleSummaryStatistics stats = analyticsService.getSummaryStatistics();
        
        // Then
        assertThat(stats.getSum()).isEqualTo(15000.0);
        assertThat(stats.getAverage()).isEqualTo(3000.0);
        assertThat(stats.getCount()).isEqualTo(5);
        assertThat(stats.getMin()).isEqualTo(2000.0);
        assertThat(stats.getMax()).isEqualTo(5000.0);
    }
    
    @Test
    void testEmptyRecordsList() {
        // Given
        SalesAnalyticsService emptyService = new SalesAnalyticsService(new ArrayList<>());
        
        // When & Then
        assertThat(emptyService.getTotalSales()).isZero();
        assertThat(emptyService.getAverageSales()).isZero();
        assertThat(emptyService.getSalesByProductLine()).isEmpty();
    }
    
    @Test
    void testNullRecordsList() {
        // Given
        SalesAnalyticsService nullService = new SalesAnalyticsService(null);
        
        // When & Then
        assertThat(nullService.getTotalSales()).isZero();
        assertThat(nullService.getSalesByCountry()).isEmpty();
    }
    
    /**
     * Creates test records for unit testing.
     */
    private List<SalesRecord> createTestRecords() {
        List<SalesRecord> records = new ArrayList<>();
        
        // Record 1
        records.add(SalesRecord.builder()
                .orderNumber(10107)
                .quantityOrdered(30)
                .priceEach(95.7)
                .sales(2000.0)
                .orderDate(LocalDate.of(2003, 1, 15))
                .status("Shipped")
                .qtrId(1)
                .monthId(1)
                .yearId(2003)
                .productLine("Motorcycles")
                .productCode("S10_1678")
                .customerName("Customer A")
                .country("USA")
                .territory("NA")
                .dealSize("Small")
                .build());
        
        // Record 2
        records.add(SalesRecord.builder()
                .orderNumber(10108)
                .quantityOrdered(40)
                .priceEach(100.0)
                .sales(3000.0)
                .orderDate(LocalDate.of(2003, 3, 20))
                .status("Shipped")
                .qtrId(1)
                .monthId(3)
                .yearId(2003)
                .productLine("Classic Cars")
                .productCode("S12_1234")
                .customerName("Customer B")
                .country("France")
                .territory("EMEA")
                .dealSize("Medium")
                .build());
        
        // Record 3
        records.add(SalesRecord.builder()
                .orderNumber(10109)
                .quantityOrdered(50)
                .priceEach(80.0)
                .sales(5000.0)
                .orderDate(LocalDate.of(2003, 1, 25))
                .status("Shipped")
                .qtrId(1)
                .monthId(1)
                .yearId(2003)
                .productLine("Motorcycles")
                .productCode("S10_1678")
                .customerName("Customer C")
                .country("France")
                .territory("EMEA")
                .dealSize("Large")
                .build());
        
        // Record 4
        records.add(SalesRecord.builder()
                .orderNumber(10110)
                .quantityOrdered(25)
                .priceEach(120.0)
                .sales(3000.0)
                .orderDate(LocalDate.of(2004, 5, 10))
                .status("Shipped")
                .qtrId(2)
                .monthId(5)
                .yearId(2004)
                .productLine("Motorcycles")
                .productCode("S10_9999")
                .customerName("Customer A")
                .country("USA")
                .territory("NA")
                .dealSize("Medium")
                .build());
        
        // Record 5
        records.add(SalesRecord.builder()
                .orderNumber(10111)
                .quantityOrdered(20)
                .priceEach(100.0)
                .sales(2000.0)
                .orderDate(LocalDate.of(2004, 6, 15))
                .status("Shipped")
                .qtrId(2)
                .monthId(6)
                .yearId(2004)
                .productLine("Classic Cars")
                .productCode("S12_1234")
                .customerName("Customer D")
                .country("France")
                .territory("EMEA")
                .dealSize("Small")
                .build());
        
        return records;
    }
}
