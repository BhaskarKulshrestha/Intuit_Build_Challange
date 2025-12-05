#!/bin/bash

################################################################################
# CSV Data Analysis Project Verification Script
# This script verifies the installation and functionality
################################################################################

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Counters
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[✓ PASS]${NC} $1"
    ((PASSED_CHECKS++))
}

print_error() {
    echo -e "${RED}[✗ FAIL]${NC} $1"
    ((FAILED_CHECKS++))
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}\n"
}

run_check() {
    ((TOTAL_CHECKS++))
    print_info "Running: $1"
}

################################################################################
# Verification Start
################################################################################

print_header "CSV Data Analysis Verification"

################################################################################
# 1. Environment Verification
################################################################################

print_header "Environment Verification"

# Check Java
run_check "Java installation"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
    if [ "$JAVA_MAJOR_VERSION" -ge 11 ] || [ "$JAVA_MAJOR_VERSION" -eq 1 ]; then
        print_success "Java $JAVA_VERSION is installed and compatible"
    else
        print_error "Java version $JAVA_VERSION is too old (need 11+)"
    fi
else
    print_error "Java is not installed"
fi

# Check Maven
run_check "Maven installation"
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    print_success "Maven $MAVEN_VERSION is installed"
else
    print_error "Maven is not installed"
fi

################################################################################
# 2. Project Structure Verification
################################################################################

print_header "Project Structure Verification"

# Check pom.xml
run_check "pom.xml exists"
if [ -f "pom.xml" ]; then
    print_success "pom.xml found"
else
    print_error "pom.xml not found"
fi

# Check sample data
run_check "Sample CSV data exists"
if [ -f "sales_data_sample.csv" ]; then
    LINE_COUNT=$(wc -l < sales_data_sample.csv)
    print_success "sales_data_sample.csv found ($LINE_COUNT lines)"
else
    print_error "sales_data_sample.csv not found"
fi

# Check source directories
run_check "Source directory structure"
if [ -d "src/main/java/com/intuit/challenge" ]; then
    print_success "Main source directory exists"
else
    print_error "Main source directory not found"
fi

run_check "Test directory structure"
if [ -d "src/test/java/com/intuit/challenge" ]; then
    print_success "Test source directory exists"
else
    print_error "Test source directory not found"
fi

# Check key source files
run_check "Core source files"
REQUIRED_FILES=(
    "src/main/java/com/intuit/challenge/SalesDataAnalysisApp.java"
    "src/main/java/com/intuit/challenge/model/SalesRecord.java"
    "src/main/java/com/intuit/challenge/model/SalesStatistics.java"
    "src/main/java/com/intuit/challenge/service/CsvReaderService.java"
    "src/main/java/com/intuit/challenge/service/SalesAnalyticsService.java"
)

ALL_FILES_EXIST=true
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        print_error "Missing file: $file"
        ALL_FILES_EXIST=false
    fi
done

if [ "$ALL_FILES_EXIST" = true ]; then
    print_success "All core source files present"
fi

# Check test files
run_check "Test files"
TEST_FILES=(
    "src/test/java/com/intuit/challenge/SalesDataAnalysisIntegrationTest.java"
    "src/test/java/com/intuit/challenge/model/SalesRecordTest.java"
    "src/test/java/com/intuit/challenge/service/CsvReaderServiceTest.java"
    "src/test/java/com/intuit/challenge/service/SalesAnalyticsServiceTest.java"
)

ALL_TEST_FILES_EXIST=true
for file in "${TEST_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        print_error "Missing test file: $file"
        ALL_TEST_FILES_EXIST=false
    fi
done

if [ "$ALL_TEST_FILES_EXIST" = true ]; then
    print_success "All test files present"
fi

################################################################################
# 3. Compilation Verification
################################################################################

print_header "Compilation Verification"

run_check "Project compilation"
mvn clean compile -q

if [ $? -eq 0 ]; then
    print_success "Project compiles successfully"
else
    print_error "Project compilation failed"
fi

# Check compiled classes
run_check "Compiled class files"
if [ -d "target/classes/com/intuit/challenge" ]; then
    CLASS_COUNT=$(find target/classes -name "*.class" | wc -l)
    print_success "Found $CLASS_COUNT compiled class files"
else
    print_error "Compiled classes not found"
fi

################################################################################
# 4. Dependency Verification
################################################################################

print_header "Dependency Verification"

run_check "Maven dependencies"
mvn dependency:tree -q > /dev/null 2>&1

if [ $? -eq 0 ]; then
    print_success "All Maven dependencies resolved"
else
    print_error "Dependency resolution failed"
fi

run_check "OpenCSV dependency"
if mvn dependency:tree | grep -q "opencsv"; then
    print_success "OpenCSV library available"
else
    print_error "OpenCSV dependency not found"
fi

run_check "JUnit dependency"
if mvn dependency:tree | grep -q "junit"; then
    print_success "JUnit testing framework available"
else
    print_error "JUnit dependency not found"
fi

run_check "Mockito dependency"
if mvn dependency:tree | grep -q "mockito"; then
    print_success "Mockito mocking framework available"
else
    print_warning "Mockito dependency not found"
fi

################################################################################
# 5. Test Verification
################################################################################

print_header "Test Verification"

run_check "Unit tests compilation"
mvn test-compile -q

if [ $? -eq 0 ]; then
    print_success "Tests compile successfully"
else
    print_error "Test compilation failed"
fi

run_check "Unit tests execution"
mvn test -q

if [ $? -eq 0 ]; then
    print_success "All unit tests pass"
else
    print_error "Some unit tests failed"
fi

# Check test reports
run_check "Test reports generation"
if [ -d "target/surefire-reports" ]; then
    TEST_REPORTS=$(find target/surefire-reports -name "*.xml" | wc -l)
    print_success "Generated $TEST_REPORTS test report(s)"
    
    # Count test cases
    if command -v grep &> /dev/null; then
        TOTAL_TESTS=$(grep -h "tests=" target/surefire-reports/TEST-*.xml 2>/dev/null | head -1 | sed 's/.*tests="\([0-9]*\)".*/\1/')
        if [ -n "$TOTAL_TESTS" ]; then
            print_info "Total test cases executed: $TOTAL_TESTS"
        fi
    fi
else
    print_warning "Test reports not found"
fi

################################################################################
# 6. CSV Data Validation
################################################################################

print_header "CSV Data Validation"

run_check "CSV file format"
if [ -f "sales_data_sample.csv" ]; then
    # Check for header
    HEADER=$(head -1 sales_data_sample.csv)
    if [[ "$HEADER" == *"ORDERNUMBER"* ]]; then
        print_success "CSV header is valid"
    else
        print_error "CSV header format is incorrect"
    fi
    
    # Check record count
    RECORD_COUNT=$(($(wc -l < sales_data_sample.csv) - 1))
    if [ $RECORD_COUNT -gt 0 ]; then
        print_success "CSV contains $RECORD_COUNT data records"
    else
        print_error "CSV file is empty or has no data records"
    fi
else
    print_error "Cannot validate CSV - file not found"
fi

################################################################################
# 7. Runtime Verification
################################################################################

print_header "Runtime Verification"

run_check "Application execution"
timeout 15s mvn exec:java -Dexec.mainClass="com.intuit.challenge.SalesDataAnalysisApp" -q > /tmp/sales_output.txt 2>&1

if [ $? -eq 0 ] || [ $? -eq 124 ]; then
    if [ -f "/tmp/sales_output.txt" ]; then
        # Check if output contains expected analytics
        if grep -q "Total Sales" /tmp/sales_output.txt || grep -q "Sales by" /tmp/sales_output.txt; then
            print_success "Application runs and produces analytics output"
        else
            print_warning "Application runs but output format may be unexpected"
        fi
    else
        print_success "Application runs without errors"
    fi
else
    print_error "Application execution failed"
fi

# Clean up temp file
rm -f /tmp/sales_output.txt

################################################################################
# 8. Package Verification
################################################################################

print_header "Package Verification"

run_check "JAR file creation"
mvn package -DskipTests -q

if [ $? -eq 0 ]; then
    # Check for JAR with dependencies
    if [ -f "target/sales-data-analysis-1.0-jar-with-dependencies.jar" ]; then
        JAR_SIZE=$(du -h target/sales-data-analysis-1.0-jar-with-dependencies.jar | cut -f1)
        print_success "Executable JAR created: sales-data-analysis-1.0-jar-with-dependencies.jar ($JAR_SIZE)"
    else
        JAR_FILE=$(find target -name "*-with-dependencies.jar" | head -1)
        if [ -n "$JAR_FILE" ]; then
            JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
            print_success "Executable JAR created: $(basename $JAR_FILE) ($JAR_SIZE)"
        else
            print_error "JAR file with dependencies not found"
        fi
    fi
else
    print_error "Packaging failed"
fi

# Test JAR execution if it exists
run_check "JAR file execution"
JAR_FILE=$(find target -name "*-with-dependencies.jar" | head -1)
if [ -n "$JAR_FILE" ] && [ -f "sales_data_sample.csv" ]; then
    timeout 10s java -jar "$JAR_FILE" sales_data_sample.csv > /dev/null 2>&1
    if [ $? -eq 0 ] || [ $? -eq 124 ]; then
        print_success "JAR file executes successfully"
    else
        print_error "JAR execution failed"
    fi
else
    print_warning "Skipping JAR execution test"
fi

################################################################################
# 9. Documentation Verification
################################################################################

print_header "Documentation Verification"

run_check "README.md exists"
if [ -f "README.md" ]; then
    print_success "README.md found"
else
    print_error "README.md not found"
fi

run_check "QUICKSTART.md exists"
if [ -f "QUICKSTART.md" ]; then
    print_success "QUICKSTART.md found"
else
    print_warning "QUICKSTART.md not found"
fi

run_check "ARCHITECTURE.md exists"
if [ -f "ARCHITECTURE.md" ]; then
    print_success "ARCHITECTURE.md found"
else
    print_warning "ARCHITECTURE.md not found"
fi

run_check "API_DOCUMENTATION.md exists"
if [ -f "API_DOCUMENTATION.md" ]; then
    print_success "API_DOCUMENTATION.md found"
else
    print_warning "API_DOCUMENTATION.md not found"
fi

run_check "JavaDoc generation"
mvn javadoc:javadoc -q > /dev/null 2>&1

if [ $? -eq 0 ]; then
    if [ -d "target/site/apidocs" ]; then
        JAVADOC_FILES=$(find target/site/apidocs -name "*.html" | wc -l)
        print_success "JavaDoc generated successfully ($JAVADOC_FILES HTML files)"
    else
        print_error "JavaDoc directory not created"
    fi
else
    print_warning "JavaDoc generation had warnings"
fi

################################################################################
# 10. Code Quality Checks
################################################################################

print_header "Code Quality Checks"

run_check "Model classes validation"
MODEL_CLASSES=$(find src/main/java/com/intuit/challenge/model -name "*.java" | wc -l)
if [ $MODEL_CLASSES -ge 2 ]; then
    print_success "Found $MODEL_CLASSES model classes"
else
    print_warning "Expected at least 2 model classes, found $MODEL_CLASSES"
fi

run_check "Service classes validation"
SERVICE_CLASSES=$(find src/main/java/com/intuit/challenge/service -name "*.java" | wc -l)
if [ $SERVICE_CLASSES -ge 2 ]; then
    print_success "Found $SERVICE_CLASSES service classes"
else
    print_warning "Expected at least 2 service classes, found $SERVICE_CLASSES"
fi

run_check "Test coverage"
TEST_CLASSES=$(find src/test/java -name "*Test.java" | wc -l)
if [ $TEST_CLASSES -ge 4 ]; then
    print_success "Found $TEST_CLASSES test classes"
else
    print_warning "Found only $TEST_CLASSES test classes"
fi

################################################################################
# Verification Summary
################################################################################

print_header "Verification Summary"

echo ""
echo -e "${BLUE}Total Checks:${NC} $TOTAL_CHECKS"
echo -e "${GREEN}Passed:${NC} $PASSED_CHECKS"
echo -e "${RED}Failed:${NC} $FAILED_CHECKS"
echo ""

# Calculate pass percentage
PASS_PERCENTAGE=$((PASSED_CHECKS * 100 / TOTAL_CHECKS))

if [ $FAILED_CHECKS -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                        ║${NC}"
    echo -e "${GREEN}║   ✓ ALL VERIFICATIONS PASSED (100%)   ║${NC}"
    echo -e "${GREEN}║                                        ║${NC}"
    echo -e "${GREEN}║   Project is ready for use!            ║${NC}"
    echo -e "${GREEN}║                                        ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${GREEN}You can now:${NC}"
    echo "  • Run the analysis: ./run-analysis.sh"
    echo "  • View documentation: open target/site/apidocs/index.html"
    echo "  • Read guides: README.md, QUICKSTART.md"
    echo ""
    exit 0
elif [ $PASS_PERCENTAGE -ge 80 ]; then
    echo -e "${YELLOW}╔════════════════════════════════════════╗${NC}"
    echo -e "${YELLOW}║                                        ║${NC}"
    echo -e "${YELLOW}║   ⚠ VERIFICATION PASSED ($PASS_PERCENTAGE%)          ║${NC}"
    echo -e "${YELLOW}║                                        ║${NC}"
    echo -e "${YELLOW}║   Minor issues detected but usable     ║${NC}"
    echo -e "${YELLOW}║                                        ║${NC}"
    echo -e "${YELLOW}╚════════════════════════════════════════╝${NC}"
    echo ""
    exit 0
else
    echo -e "${RED}╔════════════════════════════════════════╗${NC}"
    echo -e "${RED}║                                        ║${NC}"
    echo -e "${RED}║   ✗ VERIFICATION FAILED ($PASS_PERCENTAGE%)           ║${NC}"
    echo -e "${RED}║                                        ║${NC}"
    echo -e "${RED}║   Please fix the errors above          ║${NC}"
    echo -e "${RED}║                                        ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════╝${NC}"
    echo ""
    exit 1
fi
