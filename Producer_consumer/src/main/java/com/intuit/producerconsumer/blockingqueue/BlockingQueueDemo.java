package com.intuit.producerconsumer.blockingqueue;

import com.intuit.producerconsumer.Container;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Demonstration of Producer-Consumer pattern using Java's BlockingQueue.
 * BlockingQueue provides built-in thread synchronization.
 */
public class BlockingQueueDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== BlockingQueue Producer-Consumer Demo ===\n");
        
        // Create source container with initial data
        Container<Integer> sourceContainer = new Container<>("Source");
        for (int i = 1; i <= 15; i++) {
            sourceContainer.add(i * 10);
        }
        System.out.println("Initial " + sourceContainer + "\n");
        
        // Create ArrayBlockingQueue with capacity
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(5);
        
        // Create destination container
        Container<Integer> destinationContainer = new Container<>("Destination");
        
        // Create producer
        BlockingQueueProducer<Integer> producer = new BlockingQueueProducer<>(
            "BQ-Producer", 
            sourceContainer, 
            blockingQueue, 
            200
        );
        
        // Create consumer
        BlockingQueueConsumer<Integer> consumer = new BlockingQueueConsumer<>(
            "BQ-Consumer", 
            blockingQueue, 
            destinationContainer, 
            15, 
            400
        );
        
        // Start threads
        Thread producerThread = new Thread(producer, "BQ-Producer");
        Thread consumerThread = new Thread(consumer, "BQ-Consumer");
        
        long startTime = System.currentTimeMillis();
        
        producerThread.start();
        consumerThread.start();
        
        // Wait for completion
        producerThread.join();
        consumerThread.join();
        
        long endTime = System.currentTimeMillis();
        
        // Print results
        System.out.println("\n=== Final Results ===");
        System.out.println(destinationContainer);
        System.out.println("Total time: " + (endTime - startTime) + "ms");
        System.out.println("\nBlockingQueue demo completed successfully!");
    }
}
