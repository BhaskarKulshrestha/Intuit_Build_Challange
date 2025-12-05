package com.intuit.producerconsumer;

/**
 * Demonstration with multiple producers and consumers.
 * Shows concurrent thread interaction and synchronization.
 */
public class MultipleProducersConsumersDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Multiple Producers and Consumers Demo ===\n");
        
        // Create source containers for each producer
        Container<String> sourceContainer1 = new Container<>("Source-1");
        Container<String> sourceContainer2 = new Container<>("Source-2");
        
        for (int i = 1; i <= 5; i++) {
            sourceContainer1.add("P1-Item-" + i);
            sourceContainer2.add("P2-Item-" + i);
        }
        
        System.out.println("Initial " + sourceContainer1);
        System.out.println("Initial " + sourceContainer2 + "\n");
        
        // Create shared buffer
        SharedBuffer<String> sharedBuffer = new SharedBuffer<>(3);
        
        // Create destination containers
        Container<String> destContainer1 = new Container<>("Destination-1");
        Container<String> destContainer2 = new Container<>("Destination-2");
        
        // Create producers
        Producer<String> producer1 = new Producer<>(
            "Producer-1", sourceContainer1, sharedBuffer, 200
        );
        Producer<String> producer2 = new Producer<>(
            "Producer-2", sourceContainer2, sharedBuffer, 250
        );
        
        // Create consumers
        Consumer<String> consumer1 = new Consumer<>(
            "Consumer-1", sharedBuffer, destContainer1, 5, 300
        );
        Consumer<String> consumer2 = new Consumer<>(
            "Consumer-2", sharedBuffer, destContainer2, 5, 350
        );
        
        // Start all threads
        Thread p1Thread = new Thread(producer1, "Producer-1");
        Thread p2Thread = new Thread(producer2, "Producer-2");
        Thread c1Thread = new Thread(consumer1, "Consumer-1");
        Thread c2Thread = new Thread(consumer2, "Consumer-2");
        
        long startTime = System.currentTimeMillis();
        
        p1Thread.start();
        p2Thread.start();
        c1Thread.start();
        c2Thread.start();
        
        // Wait for all threads to complete
        p1Thread.join();
        p2Thread.join();
        c1Thread.join();
        c2Thread.join();
        
        long endTime = System.currentTimeMillis();
        
        // Print final results
        System.out.println("\n=== Final Results ===");
        System.out.println(destContainer1);
        System.out.println(destContainer2);
        System.out.println("Total items consumed: " + 
            (destContainer1.size() + destContainer2.size()));
        System.out.println("Total time: " + (endTime - startTime) + "ms");
    }
}
