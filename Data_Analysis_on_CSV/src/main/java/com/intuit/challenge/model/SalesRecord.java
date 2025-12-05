package com.intuit.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Model class representing a single sales record from the CSV file.
 * This class uses Lombok annotations to reduce boilerplate code.
 * 
 * @author Intuit Challenge
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesRecord {
    
    private Integer orderNumber;
    private Integer quantityOrdered;
    private Double priceEach;
    private Integer orderLineNumber;
    private Double sales;
    private LocalDate orderDate;
    private String status;
    private Integer qtrId;
    private Integer monthId;
    private Integer yearId;
    private String productLine;
    private Integer msrp;
    private String productCode;
    private String customerName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String territory;
    private String contactLastName;
    private String contactFirstName;
    private String dealSize;
    
    /**
     * Parses a CSV row and converts it into a SalesRecord object.
     * This method demonstrates functional programming by handling exceptions gracefully.
     * 
     * @param row Array of strings representing CSV columns
     * @return SalesRecord object populated with data from CSV row
     */
    public static SalesRecord fromCsvRow(String[] row) {
        try {
            return SalesRecord.builder()
                    .orderNumber(parseInteger(row[0]))
                    .quantityOrdered(parseInteger(row[1]))
                    .priceEach(parseDouble(row[2]))
                    .orderLineNumber(parseInteger(row[3]))
                    .sales(parseDouble(row[4]))
                    .orderDate(parseDate(row[5]))
                    .status(row[6])
                    .qtrId(parseInteger(row[7]))
                    .monthId(parseInteger(row[8]))
                    .yearId(parseInteger(row[9]))
                    .productLine(row[10])
                    .msrp(parseInteger(row[11]))
                    .productCode(row[12])
                    .customerName(row[13])
                    .phone(row[14])
                    .addressLine1(row[15])
                    .addressLine2(row.length > 16 ? row[16] : "")
                    .city(row.length > 17 ? row[17] : "")
                    .state(row.length > 18 ? row[18] : "")
                    .postalCode(row.length > 19 ? row[19] : "")
                    .country(row.length > 20 ? row[20] : "")
                    .territory(row.length > 21 ? row[21] : "")
                    .contactLastName(row.length > 22 ? row[22] : "")
                    .contactFirstName(row.length > 23 ? row[23] : "")
                    .dealSize(row.length > 24 ? row[24] : "")
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing CSV row: " + e.getMessage(), e);
        }
    }
    
    /**
     * Safely parses an integer from a string.
     * Returns null if the string is empty or cannot be parsed.
     * 
     * @param value String value to parse
     * @return Integer value or null
     */
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
    
    /**
     * Safely parses a double from a string.
     * Returns null if the string is empty or cannot be parsed.
     * 
     * @param value String value to parse
     * @return Double value or null
     */
    private static Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parses a date from a string using the format "M/d/yyyy H:mm".
     * Returns null if the string is empty or cannot be parsed.
     * 
     * @param value String value to parse
     * @return LocalDate value or null
     */
    private static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm", Locale.ENGLISH);
            return LocalDate.parse(value.trim(), formatter);
        } catch (Exception e) {
            // Try alternate format without time
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
                return LocalDate.parse(value.trim(), formatter);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
