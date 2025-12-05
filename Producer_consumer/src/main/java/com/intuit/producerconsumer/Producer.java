package com.intuit.producerconsumer;

/**
 * Producer thread that reads items from a source container 
 * and places them into a shared buffer.
 * 
 * The Producer implements the Runnable interface to run as a separate thread.
 * It continuously reads items from the source container and produces them
 * to the shared buffer until all items are processed.
 * 
 * Thread Safety: All synchronization is handled by the SharedBuffer class.
 */
public class Producer<T> implements Runnable {
    // Source container from which items are read
    private final Container<T> sourceContainer;
    
    // Shared buffer where items are placed (thread-safe)
    private final SharedBuffer<T> sharedBuffer;
    
    // Name of this producer (for logging/identification)
    private final String name;
    
    // Delay in milliseconds between producing items (simulates processing time)
    private final int delayMs;
    
    /**
     * Constructs a new Producer.
     * 
     * @param name Name of this producer thread
     * @param sourceContainer Container to read items from
     * @param sharedBuffer Shared buffer to place items into
     * @param delayMs Delay in milliseconds between producing items (0 = no delay)
     */
    public Producer(String name, Container<T> sourceContainer, 
                   SharedBuffer<T> sharedBuffer, int delayMs) {
        this.name = name;
        this.sourceContainer = sourceContainer;
        this.sharedBuffer = sharedBuffer;
        this.delayMs = delayMs;
    }
    
    /**
     * Main execution method of the producer thread.
     * Runs when thread.start() is called.
     * 
     * Process:
     * 1. Read item from source container
     * 2. Produce item to shared buffer (may block if buffer is full)
     * 3. Sleep for configured delay (simulates processing time)
     * 4. Repeat until all items are processed
     */
    @Override
    public void run() {
        try {
            System.out.println(name + " started producing...");
            
            // Index to track current position in source container
            int index = 0;
            
            // Continue until all items from source are processed
            while (index < sourceContainer.size()) {
                // Read item at current index
                T item = sourceContainer.get(index);
                
                if (item != null) {
                    // Produce item to shared buffer
                    // This call may BLOCK if buffer is full (wait/notify mechanism)
                    sharedBuffer.produce(item);
                    
                    // Move to next item
                    index++;
                    
                    // Simulate processing time (e.g., reading from disk, API call, etc.)
                    if (delayMs > 0) {
                        Thread.sleep(delayMs);
                    }
                }
            }
            
            System.out.println(name + " finished producing all items.");
            
        } catch (InterruptedException e) {
            // Thread was interrupted (e.g., by shutdown request)
            // Restore interrupt status
            Thread.currentThread().interrupt();
            System.err.println(name + " was interrupted: " + e.getMessage());
        }
    }
    
    /**
     * Returns the name of this producer.
     * @return Producer name
     */
    public String getProducerName() {
        return name;
    }
}
