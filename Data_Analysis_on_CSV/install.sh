#!/bin/bash

################################################################################
# CSV Data Analysis Project Installation Script
# This script installs all dependencies and sets up the project
################################################################################

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}\n"
}

################################################################################
# Step 1: Check Prerequisites
################################################################################

print_header "Checking Prerequisites"

# Check Java
print_info "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    print_success "Java found: $JAVA_VERSION"
    
    # Check if Java version is 11 or higher
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
    if [ "$JAVA_MAJOR_VERSION" -lt 11 ] && [ "$JAVA_MAJOR_VERSION" -ne 1 ]; then
        print_error "Java 11 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
else
    print_error "Java is not installed. Please install Java 11 or higher."
    echo "Download from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# Check Maven
print_info "Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    print_success "Maven found: $MAVEN_VERSION"
else
    print_error "Maven is not installed. Please install Maven 3.6 or higher."
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check Git (optional but recommended)
print_info "Checking Git installation..."
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | awk '{print $3}')
    print_success "Git found: $GIT_VERSION"
else
    print_warning "Git is not installed. It's recommended but not required."
fi

################################################################################
# Step 2: Verify Sample Data File
################################################################################

print_header "Verifying Sample Data"

if [ -f "sales_data_sample.csv" ]; then
    print_success "Sample CSV file found"
    LINE_COUNT=$(wc -l < sales_data_sample.csv)
    print_info "CSV contains $LINE_COUNT lines"
else
    print_error "sales_data_sample.csv not found!"
    echo "Please ensure the sample data file is present in the project directory."
    exit 1
fi

################################################################################
# Step 3: Clean Previous Builds
################################################################################

print_header "Cleaning Previous Builds"

if [ -d "target" ]; then
    print_info "Removing previous build artifacts..."
    rm -rf target
    print_success "Previous builds cleaned"
else
    print_info "No previous builds found"
fi

# Clean dependency-reduced-pom.xml if exists
if [ -f "dependency-reduced-pom.xml" ]; then
    print_info "Removing dependency-reduced-pom.xml..."
    rm -f dependency-reduced-pom.xml
fi

################################################################################
# Step 4: Download Dependencies
################################################################################

print_header "Downloading Dependencies"

print_info "Resolving and downloading all Maven dependencies..."
mvn dependency:resolve dependency:resolve-plugins

if [ $? -eq 0 ]; then
    print_success "All runtime dependencies downloaded successfully"
else
    print_error "Failed to download dependencies"
    exit 1
fi

print_info "Downloading test dependencies..."
mvn dependency:resolve -DincludeScope=test

if [ $? -eq 0 ]; then
    print_success "Test dependencies downloaded successfully"
else
    print_error "Failed to download test dependencies"
    exit 1
fi

print_info "Copying dependencies to target/dependency..."
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency

if [ $? -eq 0 ]; then
    print_success "Dependencies copied for verification"
else
    print_warning "Could not copy dependencies (non-critical)"
fi

################################################################################
# Step 4.5: Verify Project-Specific Dependencies
################################################################################

print_header "Verifying Project Dependencies"

# Check OpenCSV library
print_info "Verifying OpenCSV library..."
if mvn dependency:tree | grep -q "com.opencsv:opencsv"; then
    OPENCSV_VERSION=$(mvn dependency:tree | grep "com.opencsv:opencsv" | head -1 | sed 's/.*:opencsv:jar:\([0-9.]*\).*/\1/')
    print_success "OpenCSV library found (version $OPENCSV_VERSION)"
    
    # Verify OpenCSV can be imported
    print_info "Testing OpenCSV import..."
    if mvn dependency:get -Dartifact=com.opencsv:opencsv:$OPENCSV_VERSION > /dev/null 2>&1; then
        print_success "OpenCSV successfully validated"
    fi
else
    print_error "OpenCSV dependency not found!"
    echo "This is required for CSV parsing. Please check pom.xml"
    exit 1
fi

# Check Lombok
print_info "Verifying Lombok library..."
if mvn dependency:tree | grep -q "org.projectlombok:lombok"; then
    LOMBOK_VERSION=$(mvn dependency:tree | grep "org.projectlombok:lombok" | head -1 | sed 's/.*:lombok:jar:\([0-9.]*\).*/\1/')
    print_success "Lombok found (version $LOMBOK_VERSION)"
else
    print_warning "Lombok not found (optional dependency)"
fi

# Check JUnit 5 dependencies
print_info "Verifying JUnit 5 dependencies..."
JUNIT_FOUND=false

if mvn dependency:tree | grep -q "org.junit.jupiter:junit-jupiter-api"; then
    JUNIT_API_VERSION=$(mvn dependency:tree | grep "junit-jupiter-api" | head -1 | sed 's/.*:junit-jupiter-api:jar:\([0-9.]*\).*/\1/')
    print_success "JUnit Jupiter API found (version $JUNIT_API_VERSION)"
    JUNIT_FOUND=true
fi

if mvn dependency:tree | grep -q "org.junit.jupiter:junit-jupiter-engine"; then
    JUNIT_ENGINE_VERSION=$(mvn dependency:tree | grep "junit-jupiter-engine" | head -1 | sed 's/.*:junit-jupiter-engine:jar:\([0-9.]*\).*/\1/')
    print_success "JUnit Jupiter Engine found (version $JUNIT_ENGINE_VERSION)"
    JUNIT_FOUND=true
fi

if mvn dependency:tree | grep -q "org.junit.jupiter:junit-jupiter-params"; then
    JUNIT_PARAMS_VERSION=$(mvn dependency:tree | grep "junit-jupiter-params" | head -1 | sed 's/.*:junit-jupiter-params:jar:\([0-9.]*\).*/\1/')
    print_success "JUnit Jupiter Params found (version $JUNIT_PARAMS_VERSION)"
fi

if [ "$JUNIT_FOUND" = false ]; then
    print_error "JUnit 5 dependencies not found!"
    exit 1
fi

# Check Mockito
print_info "Verifying Mockito library..."
if mvn dependency:tree | grep -q "org.mockito:mockito-core"; then
    MOCKITO_VERSION=$(mvn dependency:tree | grep "mockito-core" | head -1 | sed 's/.*:mockito-core:jar:\([0-9.]*\).*/\1/')
    print_success "Mockito mocking framework found (version $MOCKITO_VERSION)"
else
    print_warning "Mockito not found (may affect some tests)"
fi

# Check AssertJ
print_info "Verifying AssertJ library..."
if mvn dependency:tree | grep -q "org.assertj:assertj-core"; then
    ASSERTJ_VERSION=$(mvn dependency:tree | grep "assertj-core" | head -1 | sed 's/.*:assertj-core:jar:\([0-9.]*\).*/\1/')
    print_success "AssertJ assertions library found (version $ASSERTJ_VERSION)"
else
    print_warning "AssertJ not found (optional testing library)"
fi

# Verify Maven plugins
print_info "Verifying Maven plugins..."

REQUIRED_PLUGINS=(
    "maven-compiler-plugin"
    "maven-surefire-plugin"
    "maven-jar-plugin"
    "maven-shade-plugin"
)

for plugin in "${REQUIRED_PLUGINS[@]}"; do
    if mvn help:effective-pom | grep -q "$plugin"; then
        print_success "Plugin verified: $plugin"
    else
        print_warning "Plugin not configured: $plugin"
    fi
done

# Display dependency tree summary
print_info "Generating dependency tree summary..."
TOTAL_DEPS=$(mvn dependency:tree | grep -c "^\[INFO\].*:.*:.*:.*")
print_success "Total dependencies resolved: $TOTAL_DEPS"

# Check for dependency conflicts
print_info "Checking for dependency conflicts..."
if mvn dependency:analyze > /dev/null 2>&1; then
    print_success "No critical dependency conflicts detected"
else
    print_warning "Dependency analysis completed with warnings"
fi

# List all direct dependencies
print_info "Direct project dependencies:"
mvn dependency:tree | grep "^\[INFO\] +-" | head -10 | while read line; do
    echo "  $line"
done

################################################################################
# Step 5: Compile the Project
################################################################################

print_header "Compiling Project"

print_info "Compiling source code..."
mvn clean compile

if [ $? -eq 0 ]; then
    print_success "Project compiled successfully"
else
    print_error "Compilation failed"
    exit 1
fi

################################################################################
# Step 6: Run Tests
################################################################################

print_header "Running Tests"

print_info "Executing unit tests..."
mvn test

if [ $? -eq 0 ]; then
    print_success "All tests passed successfully"
else
    print_warning "Some tests failed. Please check the output above."
fi

# Display test summary
if [ -d "target/surefire-reports" ]; then
    TEST_FILES=$(find target/surefire-reports -name "TEST-*.xml" | wc -l)
    print_info "Generated $TEST_FILES test report(s)"
fi

################################################################################
# Step 7: Package the Project
################################################################################

print_header "Packaging Project"

print_info "Creating executable JAR file with dependencies..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    print_success "Project packaged successfully"
    
    # Find the executable JAR (with dependencies)
    if [ -f "target/sales-data-analysis-1.0-jar-with-dependencies.jar" ]; then
        JAR_SIZE=$(du -h target/sales-data-analysis-1.0-jar-with-dependencies.jar | cut -f1)
        print_success "Executable JAR created: sales-data-analysis-1.0-jar-with-dependencies.jar ($JAR_SIZE)"
    else
        JAR_FILE=$(find target -name "*-with-dependencies.jar" | head -1)
        if [ -n "$JAR_FILE" ]; then
            JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
            print_success "Executable JAR created: $(basename $JAR_FILE) ($JAR_SIZE)"
        fi
    fi
else
    print_error "Packaging failed"
    exit 1
fi

################################################################################
# Step 8: Generate Documentation
################################################################################

print_header "Generating Documentation"

print_info "Generating JavaDoc..."
mvn javadoc:javadoc

if [ $? -eq 0 ]; then
    print_success "JavaDoc generated successfully"
    print_info "Documentation available at: target/site/apidocs/index.html"
else
    print_warning "JavaDoc generation had warnings (this is non-critical)"
fi

################################################################################
# Step 9: Create Quick Access Scripts
################################################################################

print_header "Setting Up Quick Access"

# Create a simple run script
cat > run-analysis.sh << 'EOF'
#!/bin/bash
# Quick script to run the sales data analysis

CSV_FILE="${1:-sales_data_sample.csv}"

if [ ! -f "$CSV_FILE" ]; then
    echo "Error: CSV file not found: $CSV_FILE"
    echo "Usage: ./run-analysis.sh [csv_file]"
    exit 1
fi

echo "Running Sales Data Analysis..."
echo "CSV File: $CSV_FILE"
echo ""

mvn exec:java -Dexec.mainClass="com.intuit.challenge.SalesDataAnalysisApp" -Dexec.args="$CSV_FILE"
EOF

chmod +x run-analysis.sh
print_success "Created run-analysis.sh script"

################################################################################
# Installation Complete
################################################################################

print_header "Installation Complete!"

echo ""
print_success "CSV Data Analysis project installed successfully!"
echo ""
echo -e "${GREEN}Project Structure:${NC}"
echo "  ✓ Source code compiled"
echo "  ✓ Tests executed"
echo "  ✓ JAR file created"
echo "  ✓ Documentation generated"
echo "  ✓ Sample data verified"
echo ""
echo -e "${GREEN}Next Steps:${NC}"
echo ""
echo "  1. Run the analysis with default sample data:"
echo "     ${BLUE}./run-analysis.sh${NC}"
echo ""
echo "  2. Run with your own CSV file:"
echo "     ${BLUE}./run-analysis.sh your_data.csv${NC}"
echo ""
echo "  3. Run using Maven:"
echo "     ${BLUE}mvn exec:java -Dexec.mainClass=\"com.intuit.challenge.SalesDataAnalysisApp\"${NC}"
echo ""
echo "  4. Run the standalone JAR:"
echo "     ${BLUE}java -jar target/sales-data-analysis-1.0-jar-with-dependencies.jar sales_data_sample.csv${NC}"
echo ""
echo "  5. Run all tests:"
echo "     ${BLUE}mvn test${NC}"
echo ""
echo "  6. View documentation:"
echo "     ${BLUE}open target/site/apidocs/index.html${NC}"
echo ""
echo -e "${BLUE}For more information, see:${NC}"
echo "  - README.md"
echo "  - QUICKSTART.md"
echo "  - ARCHITECTURE.md"
echo "  - API_DOCUMENTATION.md"
echo ""
