package com.intuit.producerconsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container represents a thread-safe storage for items.
 * Used as source container (for producer) and destination container (for consumer).
 * 
 * This class provides a simple thread-safe wrapper around a List.
 * All operations are synchronized to ensure thread safety when accessed
 * by multiple threads concurrently.
 */
public class Container<T> {
    // Synchronized list to store items in a thread-safe manner
    // Collections.synchronizedList wraps ArrayList with synchronized methods
    private final List<T> items;
    
    // Name of this container (for identification and logging)
    private final String name;
    
    /**
     * Constructs a new Container with the specified name.
     * @param name Name identifier for this container
     */
    public Container(String name) {
        // Create a thread-safe list using Collections.synchronizedList
        this.items = Collections.synchronizedList(new ArrayList<>());
        this.name = name;
    }
    
    /**
     * Add an item to the container.
     * Thread-safe operation.
     * @param item Item to add to the container
     */
    public synchronized void add(T item) {
        items.add(item);
    }
    
    /**
     * Remove and return an item from the container at specified index.
     * Thread-safe operation.
     * @param index Index of item to remove
     * @return The removed item, or null if index is invalid
     */
    public synchronized T remove(int index) {
        if (index >= 0 && index < items.size()) {
            return items.remove(index);
        }
        return null;
    }
    
    /**
     * Get item at specific index without removing it.
     * Thread-safe operation.
     * @param index Index of item to retrieve
     * @return The item at specified index, or null if index is invalid
     */
    public synchronized T get(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }
    
    /**
     * Returns the current number of items in the container.
     * Thread-safe operation.
     * @return Current size of the container
     */
    public synchronized int size() {
        return items.size();
    }
    
    /**
     * Checks if the container is empty.
     * Thread-safe operation.
     * @return true if container is empty, false otherwise
     */
    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Returns a copy of all items in the container.
     * Thread-safe operation - returns a new ArrayList to avoid external modification.
     * @return A new list containing all items in this container
     */
    public synchronized List<T> getAll() {
        return new ArrayList<>(items);
    }
    
    /**
     * Returns the name of this container.
     * @return Container name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns a string representation of this container.
     * Shows name, size, and all items.
     * @return String representation
     */
    @Override
    public synchronized String toString() {
        return name + " [size=" + items.size() + ", items=" + items + "]";
    }
}
