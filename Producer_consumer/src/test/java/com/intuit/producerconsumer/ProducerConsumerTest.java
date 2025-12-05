package com.intuit.producerconsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for Producer-Consumer pattern implementation.
 * Tests thread synchronization, blocking behavior, and data integrity.
 */
class ProducerConsumerTest {
    
    private Container<String> sourceContainer;
    private Container<String> destinationContainer;
    private SharedBuffer<String> sharedBuffer;
    
    @BeforeEach
    void setUp() {
        sourceContainer = new Container<>("TestSource");
        destinationContainer = new Container<>("TestDestination");
        sharedBuffer = new SharedBuffer<>(5);
    }
    
    @Test
    void testSingleProducerSingleConsumer() throws InterruptedException {
        // Arrange
        int itemCount = 10;
        for (int i = 1; i <= itemCount; i++) {
            sourceContainer.add("Item-" + i);
        }
        
        Producer<String> producer = new Producer<>(
            "TestProducer", sourceContainer, sharedBuffer, 10
        );
        Consumer<String> consumer = new Consumer<>(
            "TestConsumer", sharedBuffer, destinationContainer, itemCount, 10
        );
        
        // Act
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        producerThread.start();
        consumerThread.start();
        
        producerThread.join(5000);
        consumerThread.join(5000);
        
        // Assert
        assertFalse(producerThread.isAlive(), "Producer should have completed");
        assertFalse(consumerThread.isAlive(), "Consumer should have completed");
        assertEquals(itemCount, destinationContainer.size(), 
            "All items should be in destination");
        
        // Verify all items transferred correctly
        List<String> expected = sourceContainer.getAll();
        List<String> actual = destinationContainer.getAll();
        assertEquals(expected, actual, "Items should match in order");
    }
    
    @Test
    void testMultipleProducersMultipleConsumers() throws InterruptedException {
        // Arrange
        int itemsPerProducer = 5;
        Container<String> source1 = new Container<>("Source1");
        Container<String> source2 = new Container<>("Source2");
        Container<String> dest1 = new Container<>("Dest1");
        Container<String> dest2 = new Container<>("Dest2");
        
        for (int i = 1; i <= itemsPerProducer; i++) {
            source1.add("P1-" + i);
            source2.add("P2-" + i);
        }
        
        Producer<String> producer1 = new Producer<>("P1", source1, sharedBuffer, 10);
        Producer<String> producer2 = new Producer<>("P2", source2, sharedBuffer, 10);
        Consumer<String> consumer1 = new Consumer<>("C1", sharedBuffer, dest1, itemsPerProducer, 10);
        Consumer<String> consumer2 = new Consumer<>("C2", sharedBuffer, dest2, itemsPerProducer, 10);
        
        // Act
        Thread p1 = new Thread(producer1);
        Thread p2 = new Thread(producer2);
        Thread c1 = new Thread(consumer1);
        Thread c2 = new Thread(consumer2);
        
        p1.start();
        p2.start();
        c1.start();
        c2.start();
        
        p1.join(5000);
        p2.join(5000);
        c1.join(5000);
        c2.join(5000);
        
        // Assert
        assertEquals(itemsPerProducer * 2, dest1.size() + dest2.size(), 
            "Total consumed items should equal total produced");
    }
    
    @Test
    void testSharedBufferBlockingWhenFull() throws InterruptedException {
        // Arrange
        SharedBuffer<Integer> buffer = new SharedBuffer<>(2);
        CountDownLatch latch = new CountDownLatch(1);
        
        // Fill buffer to capacity
        buffer.produce(1);
        buffer.produce(2);
        
        // Act - try to produce when full (should block)
        Thread producer = new Thread(() -> {
            try {
                latch.countDown(); // Signal that thread is about to block
                buffer.produce(3); // This should block
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        
        // Wait for producer to start blocking
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Latch should count down");
        Thread.sleep(100); // Give time for produce call to block
        
        // Assert - producer should be waiting
        assertEquals(Thread.State.WAITING, producer.getState(), 
            "Producer should be waiting when buffer is full");
        
        // Cleanup
        buffer.consume(); // Unblock the producer
        producer.join(1000);
    }
    
    @Test
    void testSharedBufferBlockingWhenEmpty() throws InterruptedException {
        // Arrange
        SharedBuffer<Integer> buffer = new SharedBuffer<>(5);
        CountDownLatch latch = new CountDownLatch(1);
        
        // Act - try to consume from empty buffer (should block)
        Thread consumer = new Thread(() -> {
            try {
                latch.countDown(); // Signal that thread is about to block
                buffer.consume(); // This should block
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        consumer.start();
        
        // Wait for consumer to start blocking
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Latch should count down");
        Thread.sleep(100); // Give time for consume call to block
        
        // Assert - consumer should be waiting
        assertEquals(Thread.State.WAITING, consumer.getState(), 
            "Consumer should be waiting when buffer is empty");
        
        // Cleanup
        buffer.produce(1); // Unblock the consumer
        consumer.join(1000);
    }
    
    @Test
    void testContainerThreadSafety() throws InterruptedException {
        // Arrange
        Container<Integer> container = new Container<>("ThreadSafeTest");
        int threadCount = 10;
        int itemsPerThread = 100;
        List<Thread> threads = new ArrayList<>();
        
        // Act - multiple threads adding to container
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    container.add(threadId * 1000 + j);
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Assert
        assertEquals(threadCount * itemsPerThread, container.size(), 
            "All items should be added without race conditions");
    }
    
    @Test
    void testProducerStopsWhenSourceEmpty() throws InterruptedException {
        // Arrange
        sourceContainer.add("OnlyItem");
        
        Producer<String> producer = new Producer<>(
            "TestProducer", sourceContainer, sharedBuffer, 10
        );
        
        // Act
        Thread producerThread = new Thread(producer);
        producerThread.start();
        producerThread.join(2000);
        
        // Assert
        assertFalse(producerThread.isAlive(), "Producer should stop after source is empty");
        assertEquals(1, sharedBuffer.size(), "Buffer should have one item");
    }
    
    @Test
    void testConsumerStopsWhenItemCountReached() throws InterruptedException {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            sharedBuffer.produce("Item-" + i);
        }
        
        Consumer<String> consumer = new Consumer<>(
            "TestConsumer", sharedBuffer, destinationContainer, 3, 10
        );
        
        // Act
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        consumerThread.join(2000);
        
        // Assert
        assertFalse(consumerThread.isAlive(), "Consumer should stop after consuming specified items");
        assertEquals(3, destinationContainer.size(), "Should consume exactly 3 items");
        assertEquals(2, sharedBuffer.size(), "Buffer should have 2 remaining items");
    }
}
