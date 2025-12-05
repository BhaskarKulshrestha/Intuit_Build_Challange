# Quick Start Guide

## � Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   # Check your Java version
   java -version
   ```
   Expected output: `java version "11.0.x"` or higher
   
   **Download:** [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)

2. **Apache Maven 3.6 or higher**
   ```bash
   # Check your Maven version
   mvn -version
   ```
   Expected output: `Apache Maven 3.6.x` or higher
   
   **Download:** [Maven Official Site](https://maven.apache.org/download.cgi)

3. **Git** (Optional - for version control)
   ```bash
   # Check your Git version
   git --version
   ```

### System Requirements

- **Operating System:** macOS, Linux, or Windows
- **RAM:** Minimum 2GB (4GB recommended)
- **Disk Space:** At least 500MB free space

### Verify Prerequisites

Run this command to verify all prerequisites:
```bash
java -version && mvn -version
```

If both commands work, you're ready to proceed! ✅

---

## �🚀 Quick Start (3 Steps)

### 1. Build the Project
```bash
cd /Users/I528989/Downloads/intuit/Intuit_Build_Challange/Producer_consumer
mvn clean install
```

### 2. Run a Demo (Choose One)

#### Option A: Use the Interactive Menu
```bash
./run-demo.sh
```

#### Option B: Run Directly

**Basic Demo:**
```bash
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.ProducerConsumerDemo"
```

**Multiple Producers/Consumers:**
```bash
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.MultipleProducersConsumersDemo"
```

**BlockingQueue Demo:**
```bash
mvn exec:java -Dexec.mainClass="com.intuit.producerconsumer.blockingqueue.BlockingQueueDemo"
```

### 3. Run Tests
```bash
mvn test
```

## 📝 What You'll See

### Basic Demo Output Example:
```
=== Producer-Consumer Pattern Demo ===

Initial Source [size=10, items=[Item-1, Item-2, ..., Item-10]]

Producer-1 started producing...
Consumer-1 started consuming...
Producer-1 - Produced: Item-1 | Buffer size: 1
Consumer-1 - Consumed: Item-1 | Buffer size: 0
...
Producer-1 - Buffer is full. Producer waiting...
...

=== Final Results ===
Destination [size=10, items=[Item-1, Item-2, ..., Item-10]]
Total time: 4532ms
```

## 🎯 Key Features Demonstrated

1. **Thread Synchronization** with wait/notify
2. **Blocking Behavior** when buffer is full/empty
3. **Concurrent Programming** with multiple threads
4. **Thread-Safe Operations** on shared resources
5. **Java BlockingQueue** implementation comparison

## 📚 Documentation Files

- **README.md** - Complete project documentation
- **SYNCHRONIZATION.md** - Detailed synchronization concepts with diagrams
- **QUICKSTART.md** - This file (quick reference)

## 🔧 Project Structure Summary

```
src/main/java/com/intuit/producerconsumer/
├── Container.java              # Thread-safe storage
├── SharedBuffer.java           # Custom wait/notify buffer
├── Producer.java               # Producer thread
├── Consumer.java               # Consumer thread
├── ProducerConsumerDemo.java   # Basic demo
├── MultipleProducersConsumersDemo.java  # Multi-thread demo
└── blockingqueue/
    ├── BlockingQueueProducer.java
    ├── BlockingQueueConsumer.java
    └── BlockingQueueDemo.java

src/test/java/com/intuit/producerconsumer/
└── ProducerConsumerTest.java   # Unit tests
```

## ✅ Testing Objectives Covered

- ✅ Thread synchronization with wait/notify
- ✅ Concurrent programming with multiple threads
- ✅ Blocking queues (custom and Java's built-in)
- ✅ Wait/Notify mechanism implementation
- ✅ Race condition prevention
- ✅ Deadlock avoidance
- ✅ Thread-safe operations

## 🐛 Troubleshooting

**Java not found:**
```bash
# Install Java JDK 11 or higher
# macOS (using Homebrew):
brew install openjdk@11

# Ubuntu/Debian:
sudo apt install openjdk-11-jdk

# Windows: Download from Oracle or Adoptium and add to PATH
```

**Maven not found:**
```bash
# macOS (using Homebrew):
brew install maven

# Ubuntu/Debian:
sudo apt install maven

# Windows: Download from Apache Maven website and add to PATH
```

**Build fails:**
```bash
# Check Java version (needs 11+)
java -version

# Check Maven version (needs 3.6+)
mvn -version

# Clean and rebuild
mvn clean install -U
```

**Permission denied on run-demo.sh:**
```bash
chmod +x run-demo.sh
```

**Tests fail:**
```bash
# Run with verbose output
mvn test -X
```

## 💡 Tips

1. **Adjust processing speeds**: Modify delay parameters in demo classes
2. **Change buffer size**: Edit capacity in SharedBuffer constructor
3. **Add more threads**: Create additional Producer/Consumer instances
4. **View blocking behavior**: Reduce buffer size to see more waiting

## 📞 Support

For detailed explanations:
- Read **README.md** for comprehensive documentation
- Read **SYNCHRONIZATION.md** for synchronization concepts
- Check source code comments for implementation details

---

**Ready to start?** Run `./run-demo.sh` and choose option 1!
