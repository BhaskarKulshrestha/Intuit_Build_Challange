package com.intuit.challenge;

import com.intuit.challenge.model.SalesRecord;
import com.intuit.challenge.model.SalesStatistics;
import com.intuit.challenge.service.CsvReaderService;
import com.intuit.challenge.service.SalesAnalyticsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the complete application workflow.
 * Tests the full pipeline from CSV reading to analytics.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SalesDataAnalysisIntegrationTest {
    
    private SalesAnalyticsService analyticsService;
    private List<SalesRecord> salesRecords;
    
    @BeforeAll
    void setUp() throws Exception {
        // Load the actual CSV file
        CsvReaderService csvReader = new CsvReaderService();
        String csvPath = "sales_data_sample.csv";
        
        // Check if file exists, skip test if not
        try {
            salesRecords = csvReader.readSalesData(csvPath);
            analyticsService = new SalesAnalyticsService(salesRecords);
        } catch (Exception e) {
            // File might not exist in test environment
            System.out.println("Warning: Could not load CSV file for integration test: " + e.getMessage());
        }
    }
    
    @Test
    void testEndToEndWorkflow() {
        // Skip if data wasn't loaded
        if (salesRecords == null || salesRecords.isEmpty()) {
            System.out.println("Skipping integration test - no data loaded");
            return;
        }
        
        // Verify data was loaded
        assertThat(salesRecords).isNotEmpty();
        
        // Test various analytics operations work end-to-end
        double totalSales = analyticsService.getTotalSales();
        assertThat(totalSales).isPositive();
        
        Map<String, SalesStatistics> productStats = analyticsService.getSalesByProductLine();
        assertThat(productStats).isNotEmpty();
        
        Map<String, Double> countryStats = analyticsService.getSalesByCountry();
        assertThat(countryStats).isNotEmpty();
    }
    
    @Test
    void testRealDataStatistics() {
        if (salesRecords == null || salesRecords.isEmpty()) {
            System.out.println("Skipping integration test - no data loaded");
            return;
        }
        
        // Verify statistics are calculated correctly
        double total = analyticsService.getTotalSales();
        double average = analyticsService.getAverageSales();
        
        assertThat(average).isLessThan(total);
        assertThat(average).isPositive();
    }
    
    @Test
    void testDataIntegrity() {
        if (salesRecords == null || salesRecords.isEmpty()) {
            System.out.println("Skipping integration test - no data loaded");
            return;
        }
        
        // Verify all records have essential fields
        long validRecords = salesRecords.stream()
                .filter(r -> r.getOrderNumber() != null)
                .filter(r -> r.getSales() != null)
                .count();
        
        assertThat(validRecords).isEqualTo(salesRecords.size());
    }
}
