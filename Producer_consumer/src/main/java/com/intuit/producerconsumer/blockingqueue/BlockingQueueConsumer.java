package com.intuit.producerconsumer.blockingqueue;

import com.intuit.producerconsumer.Container;
import java.util.concurrent.BlockingQueue;

/**
 * Consumer implementation using Java's BlockingQueue.
 * BlockingQueue handles thread synchronization internally.
 */
public class BlockingQueueConsumer<T> implements Runnable {
    private final BlockingQueue<T> blockingQueue;
    private final Container<T> destinationContainer;
    private final String name;
    private final int itemsToConsume;
    private final int delayMs;
    
    public BlockingQueueConsumer(String name, BlockingQueue<T> blockingQueue,
                                Container<T> destinationContainer, 
                                int itemsToConsume, int delayMs) {
        this.name = name;
        this.blockingQueue = blockingQueue;
        this.destinationContainer = destinationContainer;
        this.itemsToConsume = itemsToConsume;
        this.delayMs = delayMs;
    }
    
    @Override
    public void run() {
        try {
            System.out.println(name + " started consuming...");
            
            int consumed = 0;
            while (consumed < itemsToConsume) {
                // take() blocks if queue is empty
                T item = blockingQueue.take();
                
                destinationContainer.add(item);
                System.out.println(name + " - Consumed: " + item + 
                    " | Queue size: " + blockingQueue.size());
                consumed++;
                
                if (delayMs > 0) {
                    Thread.sleep(delayMs);
                }
            }
            
            System.out.println(name + " finished consuming " + consumed + " items.");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(name + " was interrupted: " + e.getMessage());
        }
    }
}
