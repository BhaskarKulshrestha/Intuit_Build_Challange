package com.intuit.producerconsumer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * SharedBuffer implements a thread-safe buffer using wait/notify mechanism.
 * This demonstrates classic producer-consumer synchronization.
 * 
 * Key Concepts:
 * - synchronized: Ensures only one thread can execute produce() or consume() at a time
 * - wait(): Releases the lock and puts thread in WAITING state until notified
 * - notifyAll(): Wakes up all threads waiting on this object's monitor
 * - while loop: Prevents spurious wakeups by rechecking condition after wait()
 */
public class SharedBuffer<T> {
    // Internal queue to store items (FIFO - First In First Out)
    private final Queue<T> buffer;
    
    // Maximum number of items the buffer can hold
    private final int capacity;
    
    /**
     * Constructor initializes the buffer with specified capacity.
     * @param capacity Maximum number of items the buffer can hold
     */
    public SharedBuffer(int capacity) {
        this.buffer = new LinkedList<>();
        this.capacity = capacity;
    }
    
    /**
     * Producer calls this method to add items to the buffer.
     * This method will BLOCK if the buffer is full (at capacity).
     * 
     * Thread Safety: synchronized keyword ensures mutual exclusion
     * Blocking Behavior: wait() is called when buffer is full
     * 
     * @param item The item to add to the buffer
     * @throws InterruptedException if thread is interrupted while waiting
     */
    public synchronized void produce(T item) throws InterruptedException {
        // CRITICAL: Use 'while' not 'if' to handle spurious wakeups
        // Keep checking condition even after being notified
        while (buffer.size() == capacity) {
            System.out.println(Thread.currentThread().getName() + 
                " - Buffer is full. Producer waiting...");
            
            // wait() releases the lock and puts this thread in WAITING state
            // Thread will remain here until another thread calls notify()/notifyAll()
            wait();
            
            // After waking up, thread reacquires the lock and rechecks the while condition
        }
        
        // Buffer has space - add the item
        buffer.add(item);
        System.out.println(Thread.currentThread().getName() + 
            " - Produced: " + item + " | Buffer size: " + buffer.size());
        
        // Notify ALL waiting consumer threads that buffer is no longer empty
        // notifyAll() is safer than notify() as it wakes all waiting threads
        notifyAll();
    }
    
    /**
     * Consumer calls this method to remove items from the buffer.
     * This method will BLOCK if the buffer is empty.
     * 
     * Thread Safety: synchronized keyword ensures mutual exclusion
     * Blocking Behavior: wait() is called when buffer is empty
     * 
     * @return The item removed from the buffer
     * @throws InterruptedException if thread is interrupted while waiting
     */
    public synchronized T consume() throws InterruptedException {
        // CRITICAL: Use 'while' not 'if' to handle spurious wakeups
        // Keep checking condition even after being notified
        while (buffer.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + 
                " - Buffer is empty. Consumer waiting...");
            
            // wait() releases the lock and puts this thread in WAITING state
            // Thread will remain here until another thread calls notify()/notifyAll()
            wait();
            
            // After waking up, thread reacquires the lock and rechecks the while condition
        }
        
        // Buffer has items - remove and return the first item (FIFO)
        T item = buffer.poll();
        System.out.println(Thread.currentThread().getName() + 
            " - Consumed: " + item + " | Buffer size: " + buffer.size());
        
        // Notify ALL waiting producer threads that buffer is no longer full
        // notifyAll() is safer than notify() as it wakes all waiting threads
        notifyAll();
        
        return item;
    }
    
    /**
     * Returns the current number of items in the buffer.
     * Thread-safe method (synchronized).
     * @return Current buffer size
     */
    public synchronized int size() {
        return buffer.size();
    }
    
    /**
     * Checks if the buffer is empty.
     * Thread-safe method (synchronized).
     * @return true if buffer is empty, false otherwise
     */
    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }
}
