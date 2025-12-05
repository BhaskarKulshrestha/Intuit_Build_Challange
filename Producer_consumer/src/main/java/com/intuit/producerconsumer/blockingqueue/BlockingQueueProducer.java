package com.intuit.producerconsumer.blockingqueue;

import com.intuit.producerconsumer.Container;
import java.util.concurrent.BlockingQueue;

/**
 * Producer implementation using Java's BlockingQueue.
 * BlockingQueue handles thread synchronization internally.
 */
public class BlockingQueueProducer<T> implements Runnable {
    private final Container<T> sourceContainer;
    private final BlockingQueue<T> blockingQueue;
    private final String name;
    private final int delayMs;
    
    public BlockingQueueProducer(String name, Container<T> sourceContainer, 
                                 BlockingQueue<T> blockingQueue, int delayMs) {
        this.name = name;
        this.sourceContainer = sourceContainer;
        this.blockingQueue = blockingQueue;
        this.delayMs = delayMs;
    }
    
    @Override
    public void run() {
        try {
            System.out.println(name + " started producing...");
            
            int index = 0;
            while (index < sourceContainer.size()) {
                T item = sourceContainer.get(index);
                
                if (item != null) {
                    // put() blocks if queue is full
                    blockingQueue.put(item);
                    System.out.println(name + " - Produced: " + item + 
                        " | Queue size: " + blockingQueue.size());
                    index++;
                    
                    if (delayMs > 0) {
                        Thread.sleep(delayMs);
                    }
                }
            }
            
            System.out.println(name + " finished producing all items.");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(name + " was interrupted: " + e.getMessage());
        }
    }
}
