package com.intuit.producerconsumer;

/**
 * Consumer thread that reads items from a shared buffer 
 * and stores them in a destination container.
 * 
 * The Consumer implements the Runnable interface to run as a separate thread.
 * It continuously consumes items from the shared buffer and stores them
 * in the destination container until the specified number of items are consumed.
 * 
 * Thread Safety: All synchronization is handled by the SharedBuffer class.
 */
public class Consumer<T> implements Runnable {
    // Shared buffer from which items are consumed (thread-safe)
    private final SharedBuffer<T> sharedBuffer;
    
    // Destination container where consumed items are stored
    private final Container<T> destinationContainer;
    
    // Name of this consumer (for logging/identification)
    private final String name;
    
    // Number of items this consumer should consume before stopping
    private final int itemsToConsume;
    
    // Delay in milliseconds between consuming items (simulates processing time)
    private final int delayMs;
    
    /**
     * Constructs a new Consumer.
     * 
     * @param name Name of this consumer thread
     * @param sharedBuffer Shared buffer to consume items from
     * @param destinationContainer Container to store consumed items
     * @param itemsToConsume Number of items to consume before stopping
     * @param delayMs Delay in milliseconds between consuming items (0 = no delay)
     */
    public Consumer(String name, SharedBuffer<T> sharedBuffer, 
                   Container<T> destinationContainer, int itemsToConsume, int delayMs) {
        this.name = name;
        this.sharedBuffer = sharedBuffer;
        this.destinationContainer = destinationContainer;
        this.itemsToConsume = itemsToConsume;
        this.delayMs = delayMs;
    }
    
    /**
     * Main execution method of the consumer thread.
     * Runs when thread.start() is called.
     * 
     * Process:
     * 1. Consume item from shared buffer (may block if buffer is empty)
     * 2. Store item in destination container
     * 3. Sleep for configured delay (simulates processing time)
     * 4. Repeat until specified number of items are consumed
     */
    @Override
    public void run() {
        try {
            System.out.println(name + " started consuming...");
            
            // Track number of items consumed so far
            int consumed = 0;
            
            // Continue until we've consumed the specified number of items
            while (consumed < itemsToConsume) {
                // Consume item from shared buffer
                // This call may BLOCK if buffer is empty (wait/notify mechanism)
                T item = sharedBuffer.consume();
                
                // Store consumed item in destination container
                destinationContainer.add(item);
                
                // Increment consumed count
                consumed++;
                
                // Simulate processing time (e.g., writing to database, file I/O, etc.)
                if (delayMs > 0) {
                    Thread.sleep(delayMs);
                }
            }
            
            System.out.println(name + " finished consuming " + consumed + " items.");
            
        } catch (InterruptedException e) {
            // Thread was interrupted (e.g., by shutdown request)
            // Restore interrupt status
            Thread.currentThread().interrupt();
            System.err.println(name + " was interrupted: " + e.getMessage());
        }
    }
    
    /**
     * Returns the name of this consumer.
     * @return Consumer name
     */
    public String getConsumerName() {
        return name;
    }
}
