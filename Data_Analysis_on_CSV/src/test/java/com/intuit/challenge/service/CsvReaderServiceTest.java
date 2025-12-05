package com.intuit.challenge.service;

import com.intuit.challenge.model.SalesRecord;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for CsvReaderService.
 * Tests CSV reading, parsing, and error handling.
 */
class CsvReaderServiceTest {
    
    private CsvReaderService csvReaderService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        csvReaderService = new CsvReaderService();
    }
    
    @Test
    void testReadSalesData_ValidFile() throws IOException, CsvException {
        // Given
        Path csvFile = createTestCsvFile("test.csv", createValidCsvContent());
        
        // When
        List<SalesRecord> records = csvReaderService.readSalesData(csvFile.toString());
        
        // Then
        assertThat(records).isNotEmpty();
        assertThat(records).hasSize(3);
        assertThat(records.get(0).getOrderNumber()).isEqualTo(10107);
        assertThat(records.get(0).getCustomerName()).isEqualTo("Land of Toys Inc.");
    }
    
    @Test
    void testReadSalesData_FileNotFound() {
        // Given
        String nonExistentFile = tempDir.resolve("nonexistent.csv").toString();
        
        // When & Then
        assertThatThrownBy(() -> csvReaderService.readSalesData(nonExistentFile))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("File not found");
    }
    
    @Test
    void testReadSalesData_EmptyFile() throws IOException {
        // Given
        Path emptyFile = tempDir.resolve("empty.csv");
        Files.createFile(emptyFile);
        
        // When & Then
        assertThatThrownBy(() -> csvReaderService.readSalesData(emptyFile.toString()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("File is empty");
    }
    
    @Test
    void testCountRecords() throws IOException {
        // Given
        Path csvFile = createTestCsvFile("test.csv", createValidCsvContent());
        
        // When
        long count = csvReaderService.countRecords(csvFile.toString());
        
        // Then
        assertThat(count).isEqualTo(3);
    }
    
    @Test
    void testReadSalesDataLazy_ValidFile() throws IOException {
        // Given
        Path csvFile = createTestCsvFile("test.csv", createValidCsvContent());
        
        // When
        List<SalesRecord> records = csvReaderService.readSalesDataLazy(csvFile.toString());
        
        // Then
        assertThat(records).isNotEmpty();
        assertThat(records.size()).isGreaterThan(0);
    }
    
    @Test
    void testReadSalesData_SkipsHeaderRow() throws IOException, CsvException {
        // Given
        Path csvFile = createTestCsvFile("test.csv", createValidCsvContent());
        
        // When
        List<SalesRecord> records = csvReaderService.readSalesData(csvFile.toString());
        
        // Then - First record should not be the header
        assertThat(records.get(0).getOrderNumber()).isNotNull();
        assertThat(records.get(0).getOrderNumber()).isInstanceOf(Integer.class);
    }
    
    private Path createTestCsvFile(String filename, String content) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.writeString(file, content);
        return file;
    }
    
    private String createValidCsvContent() {
        return "ORDERNUMBER,QUANTITYORDERED,PRICEEACH,ORDERLINENUMBER,SALES,ORDERDATE,STATUS,QTR_ID,MONTH_ID,YEAR_ID,PRODUCTLINE,MSRP,PRODUCTCODE,CUSTOMERNAME,PHONE,ADDRESSLINE1,ADDRESSLINE2,CITY,STATE,POSTALCODE,COUNTRY,TERRITORY,CONTACTLASTNAME,CONTACTFIRSTNAME,DEALSIZE\n" +
               "10107,30,95.7,2,2871,2/24/2003 0:00,Shipped,1,2,2003,Motorcycles,95,S10_1678,Land of Toys Inc.,2125557818,897 Long Airport Avenue,,NYC,NY,10022,USA,NA,Yu,Kwai,Small\n" +
               "10121,34,81.35,5,2765.9,5/7/2003 0:00,Shipped,2,5,2003,Motorcycles,95,S10_1678,Reims Collectables,26.47.1555,59 rue de l'Abbaye,,Reims,,51100,France,EMEA,Henriot,Paul,Small\n" +
               "10134,41,94.74,2,3884.34,7/1/2003 0:00,Shipped,3,7,2003,Motorcycles,95,S10_1678,Lyon Souveniers,+33 1 46 62 7555,27 rue du Colonel Pierre Avia,,Paris,,75508,France,EMEA,Da Cunha,Daniel,Medium\n";
    }
}
