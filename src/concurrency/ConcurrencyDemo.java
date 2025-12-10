package concurrency;

import java.util.concurrent.*;

/**
 * Demonstrates concurrency concepts:
 * - Threads
 * - ExecutorService / Thread Pools
 * - Futures
 * - CompletableFuture
 */
public class ConcurrencyDemo {
    
    /**
     * Thread creation and execution
     */
    public static void demonstrateThreads() throws InterruptedException {
        System.out.println("\n=== Threads Demo ===");
        
        // Create and start threads
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Thread 1: " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Thread 2: " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        });
        
        t1.start();
        t2.start();
        
        // Wait for threads to complete
        t1.join();
        t2.join();
        System.out.println("All threads completed");
    }
    
    /**
     * ExecutorService and Thread Pools
     */
    public static void demonstrateExecutorService() throws ExecutionException, InterruptedException {
        System.out.println("\n=== ExecutorService Demo ===");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Submit tasks
        Future<Integer> future1 = executor.submit(() -> {
            Thread.sleep(500);
            return 10 + 20;
        });
        
        Future<String> future2 = executor.submit(() -> {
            Thread.sleep(300);
            return "Task 2 completed";
        });
        
        // Get results
        System.out.println("Result 1: " + future1.get());
        System.out.println("Result 2: " + future2.get());
        
        executor.shutdown();
        System.out.println("ExecutorService shutdown");
    }
    
    /**
     * CompletableFuture for async programming
     */
    public static void demonstrateCompletableFuture() throws ExecutionException, InterruptedException {
        System.out.println("\n=== CompletableFuture Demo ===");
        
        // Create async computation
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            return 42;
        })
        .thenApply(n -> n * 2)  // Transform result
        .thenApply(n -> n + 10); // Chain another operation
        
        System.out.println("Result: " + cf.get());
        
        // Multiple async operations
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "World");
        
        CompletableFuture<String> combined = cf1.thenCombine(cf2, (s1, s2) -> s1 + " " + s2);
        System.out.println("Combined: " + combined.get());
    }
    
    /**
     * Thread-safe counter demonstration
     */
    public static void demonstrateThreadSafeCounter() throws InterruptedException {
        System.out.println("\n=== Thread-Safe Counter Demo ===");
        
        Counter counter = new Counter();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Increment counter from multiple threads
        for (int i = 0; i < 1000; i++) {
            executor.execute(counter::incrementAtomic);
        }
        
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        
        System.out.println("Counter value after 1000 increments: " + counter.getAtomicValue());
    }
}
