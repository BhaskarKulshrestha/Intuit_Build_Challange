package com.intuit.challenge.service;

import com.intuit.challenge.model.SalesRecord;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for reading and parsing CSV files containing sales data.
 * Demonstrates functional programming using Java Streams API.
 * 
 * @author Intuit Challenge
 * @version 1.0.0
 */
public class CsvReaderService {
    
    /**
     * Reads sales data from a CSV file and converts it to a list of SalesRecord objects.
     * This method uses functional programming with streams to parse and filter records.
     * 
     * @param filePath Path to the CSV file
     * @return List of SalesRecord objects
     * @throws IOException if the file cannot be read
     * @throws CsvException if CSV parsing fails
     */
    public List<SalesRecord> readSalesData(String filePath) throws IOException, CsvException {
        validateFile(filePath);
        
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = csvReader.readAll();
            
            // Skip the header row and convert to SalesRecord using streams
            return rows.stream()
                    .skip(1)  // Skip header row
                    .filter(row -> row.length > 0 && row[0] != null && !row[0].trim().isEmpty())
                    .map(SalesRecord::fromCsvRow)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Reads sales data from a CSV file with lazy evaluation using Stream.
     * This method is more memory-efficient for large files as it doesn't load
     * all records into memory at once.
     * 
     * @param filePath Path to the CSV file
     * @return List of SalesRecord objects
     * @throws IOException if the file cannot be read
     */
    public List<SalesRecord> readSalesDataLazy(String filePath) throws IOException {
        validateFile(filePath);
        
        // Read file using Files.lines for lazy evaluation
        try (var lines = Files.lines(Path.of(filePath))) {
            return lines
                    .skip(1)  // Skip header
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))  // Split by comma, respecting quotes
                    .filter(row -> row.length > 0 && row[0] != null && !row[0].trim().isEmpty())
                    .map(SalesRecord::fromCsvRow)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // If lazy parsing fails, fall back to CSVReader
            try {
                return readSalesData(filePath);
            } catch (CsvException ex) {
                throw new IOException("Failed to read CSV file: " + ex.getMessage(), ex);
            }
        }
    }
    
    /**
     * Validates that the file exists and is readable.
     * 
     * @param filePath Path to validate
     * @throws IOException if validation fails
     */
    private void validateFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + filePath);
        }
        
        if (Files.size(path) == 0) {
            throw new IOException("File is empty: " + filePath);
        }
    }
    
    /**
     * Gets the total number of records in the CSV file (excluding header).
     * This method demonstrates functional approach for counting without loading all data.
     * 
     * @param filePath Path to the CSV file
     * @return Number of records
     * @throws IOException if the file cannot be read
     */
    public long countRecords(String filePath) throws IOException {
        validateFile(filePath);
        
        try (var lines = Files.lines(Path.of(filePath))) {
            return lines
                    .skip(1)  // Skip header
                    .filter(line -> !line.trim().isEmpty())
                    .count();
        }
    }
}
