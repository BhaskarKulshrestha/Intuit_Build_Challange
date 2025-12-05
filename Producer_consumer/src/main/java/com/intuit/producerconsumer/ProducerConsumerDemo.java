package com.intuit.producerconsumer;

/**
 * Main demonstration class for Producer-Consumer pattern using wait/notify mechanism.
 * This demonstrates classic thread synchronization with custom SharedBuffer.
 * 
 * Demo Setup:
 * - 1 Producer thread (produces with 300ms delay)
 * - 1 Consumer thread (consumes with 500ms delay)
 * - SharedBuffer with capacity of 5
 * - 10 items to transfer from source to destination
 * 
 * Expected Behavior:
 * - Producer will sometimes wait when buffer is full
 * - Consumer will sometimes wait when buffer is empty
 * - All items transferred successfully maintaining FIFO order
 */
public class ProducerConsumerDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Producer-Consumer Pattern Demo ===\n");
        
        // Step 1: Create source container with initial data
        // This simulates a data source (e.g., file, database, queue)
        Container<String> sourceContainer = new Container<>("Source");
        for (int i = 1; i <= 10; i++) {
            sourceContainer.add("Item-" + i);
        }
        System.out.println("Initial " + sourceContainer + "\n");
        
        // Step 2: Create shared buffer with bounded capacity
        // Buffer capacity = 5: Small enough to demonstrate blocking behavior
        SharedBuffer<String> sharedBuffer = new SharedBuffer<>(5);
        
        // Step 3: Create destination container (initially empty)
        // This simulates a data sink (e.g., file, database, queue)
        Container<String> destinationContainer = new Container<>("Destination");
        
        // Step 4: Create producer thread
        // Producer reads from source and produces to buffer
        // 300ms delay simulates processing time (e.g., I/O operations)
        Producer<String> producer = new Producer<>(
            "Producer-1", 
            sourceContainer, 
            sharedBuffer, 
            300 // 300ms delay between productions
        );
        
        // Step 5: Create consumer thread
        // Consumer consumes from buffer and stores in destination
        // 500ms delay (slower than producer) will cause buffer to fill up
        Consumer<String> consumer = new Consumer<>(
            "Consumer-1", 
            sharedBuffer, 
            destinationContainer, 
            10, // consume 10 items total
            500 // 500ms delay between consumptions (slower than producer)
        );
        
        // Step 6: Create Thread objects and set thread names
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        producerThread.setName("Producer-1");
        consumerThread.setName("Consumer-1");
        
        // Record start time for performance measurement
        long startTime = System.currentTimeMillis();
        
        // Step 7: Start both threads
        // Threads will run concurrently from this point
        producerThread.start();
        consumerThread.start();
        
        // Step 8: Wait for both threads to complete
        // join() blocks the main thread until the specified thread terminates
        producerThread.join();
        consumerThread.join();
        
        // Record end time
        long endTime = System.currentTimeMillis();
        
        // Step 9: Print final results
        System.out.println("\n=== Final Results ===");
        System.out.println(sourceContainer);  // Shows source items (unchanged)
        System.out.println(destinationContainer);  // Shows all items transferred
        System.out.println("Total time: " + (endTime - startTime) + "ms");
        System.out.println("\nAll items successfully transferred from source to destination!");
    }
}
