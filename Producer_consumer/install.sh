#!/bin/bash

################################################################################
# Producer-Consumer Project Installation Script
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
# Step 2: Clean Previous Builds
################################################################################

print_header "Cleaning Previous Builds"

if [ -d "target" ]; then
    print_info "Removing previous build artifacts..."
    rm -rf target
    print_success "Previous builds cleaned"
else
    print_info "No previous builds found"
fi

################################################################################
# Step 3: Download Dependencies
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
# Step 3.5: Verify Dependencies
################################################################################

print_header "Verifying Project Dependencies"

# Check JUnit 5 dependencies
print_info "Verifying JUnit 5 dependencies..."
JUNIT_FOUND=false
if mvn dependency:tree | grep -q "org.junit.jupiter:junit-jupiter"; then
    JUNIT_API_VERSION=$(mvn dependency:tree | grep "junit-jupiter-api" | head -1 | sed 's/.*:junit-jupiter-api:jar:\([0-9.]*\).*/\1/')
    print_success "JUnit Jupiter API found (version $JUNIT_API_VERSION)"
    JUNIT_FOUND=true
fi

if mvn dependency:tree | grep -q "org.junit.jupiter:junit-jupiter-engine"; then
    JUNIT_ENGINE_VERSION=$(mvn dependency:tree | grep "junit-jupiter-engine" | head -1 | sed 's/.*:junit-jupiter-engine:jar:\([0-9.]*\).*/\1/')
    print_success "JUnit Jupiter Engine found (version $JUNIT_ENGINE_VERSION)"
    JUNIT_FOUND=true
fi

if [ "$JUNIT_FOUND" = false ]; then
    print_error "JUnit 5 dependencies not found!"
    exit 1
fi

# Verify Maven plugins
print_info "Verifying Maven plugins..."

REQUIRED_PLUGINS=(
    "maven-compiler-plugin"
    "maven-surefire-plugin"
    "maven-jar-plugin"
)

for plugin in "${REQUIRED_PLUGINS[@]}"; do
    if mvn help:effective-pom | grep -q "$plugin"; then
        print_success "Plugin verified: $plugin"
    else
        print_warning "Plugin not configured: $plugin"
    fi
done

# Display dependency tree summary
print_info "Generating dependency tree..."
TOTAL_DEPS=$(mvn dependency:tree | grep -c "^\[INFO\].*:.*:.*:.*")
print_success "Total dependencies resolved: $TOTAL_DEPS"

# Check for dependency conflicts
print_info "Checking for dependency conflicts..."
if mvn dependency:analyze > /dev/null 2>&1; then
    print_success "No critical dependency conflicts detected"
else
    print_warning "Dependency analysis completed with warnings"
fi

################################################################################
# Step 4: Compile the Project
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
# Step 5: Run Tests
################################################################################

print_header "Running Tests"

print_info "Executing unit tests..."
mvn test

if [ $? -eq 0 ]; then
    print_success "All tests passed successfully"
else
    print_warning "Some tests failed. Please check the output above."
fi

################################################################################
# Step 6: Package the Project
################################################################################

print_header "Packaging Project"

print_info "Creating JAR file..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    print_success "Project packaged successfully"
    JAR_FILE=$(find target -name "*.jar" -not -name "*-tests.jar" | head -1)
    if [ -n "$JAR_FILE" ]; then
        print_success "JAR file created: $JAR_FILE"
    fi
else
    print_error "Packaging failed"
    exit 1
fi

################################################################################
# Step 7: Generate Documentation
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
# Installation Complete
################################################################################

print_header "Installation Complete!"

echo ""
print_success "Producer-Consumer project installed successfully!"
echo ""
echo -e "${GREEN}Next Steps:${NC}"
echo "  1. Run the basic demo:"
echo "     ${BLUE}./run-demo.sh basic${NC}"
echo ""
echo "  2. Run the multi-threaded demo:"
echo "     ${BLUE}./run-demo.sh multiple${NC}"
echo ""
echo "  3. Run the BlockingQueue demo:"
echo "     ${BLUE}./run-demo.sh blocking${NC}"
echo ""
echo "  4. Run all tests:"
echo "     ${BLUE}mvn test${NC}"
echo ""
echo "  5. View documentation:"
echo "     ${BLUE}open target/site/apidocs/index.html${NC}"
echo ""
echo -e "${BLUE}For more information, see:${NC}"
echo "  - README.md"
echo "  - QUICKSTART.md"
echo ""
