package com.intuit.challenge.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for SalesRecord model class.
 * Tests the parsing functionality and data integrity.
 */
class SalesRecordTest {
    
    @Test
    void testFromCsvRow_ValidData() {
        // Given
        String[] row = {
            "10107", "30", "95.7", "2", "2871", "2/24/2003 0:00", "Shipped",
            "1", "2", "2003", "Motorcycles", "95", "S10_1678", "Land of Toys Inc.",
            "2125557818", "897 Long Airport Avenue", "", "NYC", "NY", "10022",
            "USA", "NA", "Yu", "Kwai", "Small"
        };
        
        // When
        SalesRecord record = SalesRecord.fromCsvRow(row);
        
        // Then
        assertThat(record).isNotNull();
        assertThat(record.getOrderNumber()).isEqualTo(10107);
        assertThat(record.getQuantityOrdered()).isEqualTo(30);
        assertThat(record.getPriceEach()).isEqualTo(95.7);
        assertThat(record.getSales()).isEqualTo(2871.0);
        assertThat(record.getStatus()).isEqualTo("Shipped");
        assertThat(record.getProductLine()).isEqualTo("Motorcycles");
        assertThat(record.getCustomerName()).isEqualTo("Land of Toys Inc.");
        assertThat(record.getCountry()).isEqualTo("USA");
        assertThat(record.getDealSize()).isEqualTo("Small");
    }
    
    @Test
    void testFromCsvRow_DateParsing() {
        // Given
        String[] row = {
            "10107", "30", "95.7", "2", "2871", "2/24/2003 0:00", "Shipped",
            "1", "2", "2003", "Motorcycles", "95", "S10_1678", "Land of Toys Inc.",
            "2125557818", "897 Long Airport Avenue", "", "NYC", "NY", "10022",
            "USA", "NA", "Yu", "Kwai", "Small"
        };
        
        // When
        SalesRecord record = SalesRecord.fromCsvRow(row);
        
        // Then
        assertThat(record.getOrderDate()).isEqualTo(LocalDate.of(2003, 2, 24));
    }
    
    @Test
    void testFromCsvRow_NullHandling() {
        // Given - row with empty values
        String[] row = {
            "", "", "", "", "", "", "Shipped",
            "1", "2", "2003", "Motorcycles", "", "", "",
            "", "", "", "", "", "",
            "", "", "", "", ""
        };
        
        // When
        SalesRecord record = SalesRecord.fromCsvRow(row);
        
        // Then
        assertThat(record).isNotNull();
        assertThat(record.getOrderNumber()).isNull();
        assertThat(record.getQuantityOrdered()).isNull();
        assertThat(record.getPriceEach()).isNull();
        assertThat(record.getSales()).isNull();
    }
    
    @Test
    void testBuilder() {
        // Given & When
        SalesRecord record = SalesRecord.builder()
                .orderNumber(12345)
                .quantityOrdered(100)
                .sales(5000.0)
                .customerName("Test Customer")
                .productLine("Test Product")
                .country("USA")
                .dealSize("Large")
                .build();
        
        // Then
        assertThat(record.getOrderNumber()).isEqualTo(12345);
        assertThat(record.getQuantityOrdered()).isEqualTo(100);
        assertThat(record.getSales()).isEqualTo(5000.0);
        assertThat(record.getCustomerName()).isEqualTo("Test Customer");
    }
    
    @ParameterizedTest
    @CsvSource({
        "10107, 10107",
        "999, 999",
        "1, 1"
    })
    void testOrderNumberParsing(String input, int expected) {
        // Given
        String[] row = createBasicRow();
        row[0] = input;
        
        // When
        SalesRecord record = SalesRecord.fromCsvRow(row);
        
        // Then
        assertThat(record.getOrderNumber()).isEqualTo(expected);
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Given
        SalesRecord record1 = SalesRecord.builder()
                .orderNumber(10107)
                .quantityOrdered(30)
                .sales(2871.0)
                .build();
        
        SalesRecord record2 = SalesRecord.builder()
                .orderNumber(10107)
                .quantityOrdered(30)
                .sales(2871.0)
                .build();
        
        // Then
        assertThat(record1).isEqualTo(record2);
        assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
    }
    
    private String[] createBasicRow() {
        return new String[]{
            "10107", "30", "95.7", "2", "2871", "2/24/2003 0:00", "Shipped",
            "1", "2", "2003", "Motorcycles", "95", "S10_1678", "Land of Toys Inc.",
            "2125557818", "897 Long Airport Avenue", "", "NYC", "NY", "10022",
            "USA", "NA", "Yu", "Kwai", "Small"
        };
    }
}
