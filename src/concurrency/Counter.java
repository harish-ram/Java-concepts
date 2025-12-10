package concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe counter demonstration
 * Demonstrates synchronization and atomic operations
 */
public class Counter {
    
    // Method 1: Using synchronized keyword
    private int syncCounter = 0;
    
    public synchronized void incrementSync() {
        syncCounter++;
    }
    
    public synchronized int getSyncValue() {
        return syncCounter;
    }
    
    // Method 2: Using AtomicInteger (preferred for simple cases)
    private final AtomicInteger atomicCounter = new AtomicInteger(0);
    
    public void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }
    
    public int getAtomicValue() {
        return atomicCounter.get();
    }
    
    // Reset counters
    public synchronized void resetSync() {
        syncCounter = 0;
    }
    
    public void resetAtomic() {
        atomicCounter.set(0);
    }
}
