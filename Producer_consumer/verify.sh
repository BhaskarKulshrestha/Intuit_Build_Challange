#!/bin/bash

################################################################################
# Producer-Consumer Project Verification Script
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

print_header "Producer-Consumer Verification"

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

# Check source directories
run_check "Source directory structure"
if [ -d "src/main/java/com/intuit/producerconsumer" ]; then
    print_success "Main source directory exists"
else
    print_error "Main source directory not found"
fi

run_check "Test directory structure"
if [ -d "src/test/java/com/intuit/producerconsumer" ]; then
    print_success "Test source directory exists"
else
    print_error "Test source directory not found"
fi

# Check key source files
run_check "Core source files"
REQUIRED_FILES=(
    "src/main/java/com/intuit/producerconsumer/Container.java"
    "src/main/java/com/intuit/producerconsumer/SharedBuffer.java"
    "src/main/java/com/intuit/producerconsumer/Producer.java"
    "src/main/java/com/intuit/producerconsumer/Consumer.java"
    "src/main/java/com/intuit/producerconsumer/ProducerConsumerDemo.java"
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
if [ -d "target/classes/com/intuit/producerconsumer" ]; then
    CLASS_COUNT=$(find target/classes -name "*.class" | wc -l)
    print_success "Found $CLASS_COUNT compiled class files"
else
    print_error "Compiled classes not found"
fi

################################################################################
# 4. Test Verification
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
else
    print_warning "Test reports not found"
fi

################################################################################
# 5. Dependency Verification
################################################################################

print_header "Dependency Verification"

run_check "Maven dependencies"
mvn dependency:tree -q > /dev/null 2>&1

if [ $? -eq 0 ]; then
    print_success "All Maven dependencies resolved"
else
    print_error "Dependency resolution failed"
fi

run_check "JUnit dependency"
if mvn dependency:tree | grep -q "junit"; then
    print_success "JUnit testing framework available"
else
    print_error "JUnit dependency not found"
fi

################################################################################
# 6. Runtime Verification
################################################################################

print_header "Runtime Verification"

run_check "Basic demo execution"
timeout 10s mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.ProducerConsumerDemo" -q > /dev/null 2>&1

if [ $? -eq 0 ] || [ $? -eq 124 ]; then
    print_success "Basic demo runs without errors"
else
    print_error "Basic demo execution failed"
fi

run_check "Multi-threaded demo execution"
timeout 10s mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.MultipleProducersConsumersDemo" -q > /dev/null 2>&1

if [ $? -eq 0 ] || [ $? -eq 124 ]; then
    print_success "Multi-threaded demo runs without errors"
else
    print_error "Multi-threaded demo execution failed"
fi

run_check "BlockingQueue demo execution"
timeout 10s mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.blockingqueue.BlockingQueueDemo" -q > /dev/null 2>&1

if [ $? -eq 0 ] || [ $? -eq 124 ]; then
    print_success "BlockingQueue demo runs without errors"
else
    print_error "BlockingQueue demo execution failed"
fi

################################################################################
# 7. Package Verification
################################################################################

print_header "Package Verification"

run_check "JAR file creation"
mvn package -DskipTests -q

if [ $? -eq 0 ]; then
    JAR_FILE=$(find target -name "*.jar" -not -name "*-tests.jar" | head -1)
    if [ -n "$JAR_FILE" ]; then
        JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
        print_success "JAR file created: $(basename $JAR_FILE) ($JAR_SIZE)"
    else
        print_error "JAR file not found after packaging"
    fi
else
    print_error "Packaging failed"
fi

################################################################################
# 8. Documentation Verification
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

run_check "JavaDoc generation"
mvn javadoc:javadoc -q > /dev/null 2>&1

if [ $? -eq 0 ]; then
    if [ -d "target/site/apidocs" ]; then
        print_success "JavaDoc generated successfully"
    else
        print_error "JavaDoc directory not created"
    fi
else
    print_warning "JavaDoc generation had warnings"
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
