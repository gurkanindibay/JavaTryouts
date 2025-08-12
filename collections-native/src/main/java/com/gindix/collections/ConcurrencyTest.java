package com.gindix.collections;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.stream.IntStream;

public class ConcurrencyTest {
    
    public static void testAllConcurrencyMechanisms() {
        System.out.println("🔒 COMPREHENSIVE JAVA CONCURRENCY & SYNCHRONIZATION TESTING 🔒\n");
        
        testBasicThreadSafety();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testSynchronizationMechanisms();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testAtomicClasses();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testLocksAndConditions();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testConcurrentCollections();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testExecutorServices();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testCompletableFuture();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        testThreadLocalAndVolatile();
    }
    
    // 1. Basic Thread Safety Issues
    private static void testBasicThreadSafety() {
        System.out.println("⚠️ === BASIC THREAD SAFETY ISSUES ===");
        System.out.println("Purpose: Demonstrate race conditions and the need for synchronization\n");
        
        // Unsafe counter (race condition)
        class UnsafeCounter {
            private int count = 0;
            public void increment() { count++; }
            public int getCount() { return count; }
        }
        
        System.out.println("🔸 Race Condition Example (Unsafe Counter):");
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        
        // Create multiple threads that increment the counter
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    unsafeCounter.increment();
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("   Expected count: 10,000");
        System.out.println("   Actual count: " + unsafeCounter.getCount());
        System.out.println("   Result: " + (unsafeCounter.getCount() == 10000 ? "✅ Correct (lucky!)" : "❌ Race condition occurred"));
        System.out.println("   Problem: Multiple threads accessing shared state without synchronization");
    }
    
    // 2. Synchronization Mechanisms
    private static void testSynchronizationMechanisms() {
        System.out.println("🔐 === SYNCHRONIZATION MECHANISMS ===");
        System.out.println("Purpose: Various ways to achieve thread safety\n");
        
        // Synchronized methods
        class SynchronizedCounter {
            private int count = 0;
            public synchronized void increment() { count++; }
            public synchronized int getCount() { return count; }
        }
        
        System.out.println("🔸 Synchronized Methods:");
        SynchronizedCounter syncCounter = new SynchronizedCounter();
        testCounter(syncCounter::increment, syncCounter::getCount, "Synchronized methods");
        
        // Synchronized blocks
        class SynchronizedBlockCounter {
            private int count = 0;
            private final Object lock = new Object();
            
            public void increment() {
                synchronized(lock) {
                    count++;
                }
            }
            
            public int getCount() {
                synchronized(lock) {
                    return count;
                }
            }
        }
        
        System.out.println("\n🔸 Synchronized Blocks:");
        SynchronizedBlockCounter blockCounter = new SynchronizedBlockCounter();
        testCounter(blockCounter::increment, blockCounter::getCount, "Synchronized blocks");
        
        // Class-level synchronization
        System.out.println("\n🔸 Class-level (Static) Synchronization:");
        StaticSyncCounter.reset();
        testCounter(StaticSyncCounter::increment, StaticSyncCounter::getCount, "Static synchronization");
    }
    
    static class StaticSyncCounter {
        private static int count = 0;
        public static synchronized void increment() { count++; }
        public static synchronized int getCount() { return count; }
        public static synchronized void reset() { count = 0; }
    }
    
    // 3. Atomic Classes
    private static void testAtomicClasses() {
        System.out.println("⚛️ === ATOMIC CLASSES ===");
        System.out.println("Purpose: Lock-free thread-safe operations using compare-and-swap\n");
        
        // AtomicInteger
        System.out.println("🔸 AtomicInteger:");
        AtomicInteger atomicInt = new AtomicInteger(0);
        testCounter(atomicInt::incrementAndGet, atomicInt::get, "AtomicInteger");
        
        // AtomicReference
        System.out.println("\n🔸 AtomicReference:");
        AtomicReference<String> atomicRef = new AtomicReference<>("Initial");
        
        // Demonstrate compare-and-set
        boolean updated = atomicRef.compareAndSet("Initial", "Updated");
        System.out.println("   Compare-and-set result: " + updated);
        System.out.println("   New value: " + atomicRef.get());
        
        // AtomicStampedReference (ABA problem solution)
        System.out.println("\n🔸 AtomicStampedReference (ABA Problem Solution):");
        AtomicStampedReference<String> stampedRef = new AtomicStampedReference<>("Value", 1);
        int[] stampHolder = new int[1];
        String currentValue = stampedRef.get(stampHolder);
        System.out.println("   Value: " + currentValue + ", Stamp: " + stampHolder[0]);
        
        boolean stampedUpdate = stampedRef.compareAndSet("Value", "NewValue", 1, 2);
        System.out.println("   Stamped update result: " + stampedUpdate);
        
        // AtomicFieldUpdater
        System.out.println("\n🔸 AtomicFieldUpdater:");
        AtomicIntegerFieldUpdater<AtomicFieldExample> fieldUpdater = 
            AtomicIntegerFieldUpdater.newUpdater(AtomicFieldExample.class, "volatileField");
        
        AtomicFieldExample example = new AtomicFieldExample();
        int oldValue = fieldUpdater.getAndIncrement(example);
        System.out.println("   Field updater - old value: " + oldValue + ", new value: " + example.volatileField);
    }
    
    static class AtomicFieldExample {
        volatile int volatileField = 10;
    }
    
    // 4. Locks and Conditions
    private static void testLocksAndConditions() {
        System.out.println("🔐 === LOCKS AND CONDITIONS ===");
        System.out.println("Purpose: Advanced locking mechanisms with more control than synchronized\n");
        
        // ReentrantLock
        System.out.println("🔸 ReentrantLock:");
        class ReentrantLockCounter {
            private int count = 0;
            private final ReentrantLock lock = new ReentrantLock();
            
            public void increment() {
                lock.lock();
                try {
                    count++;
                } finally {
                    lock.unlock();
                }
            }
            
            public int getCount() {
                lock.lock();
                try {
                    return count;
                } finally {
                    lock.unlock();
                }
            }
        }
        
        ReentrantLockCounter lockCounter = new ReentrantLockCounter();
        testCounter(lockCounter::increment, lockCounter::getCount, "ReentrantLock");
        
        // ReadWriteLock
        System.out.println("\n🔸 ReadWriteLock:");
        class ReadWriteLockExample {
            private String data = "Initial Data";
            private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
            private final Lock readLock = rwLock.readLock();
            private final Lock writeLock = rwLock.writeLock();
            
            public String read() {
                readLock.lock();
                try {
                    System.out.println("     Reading: " + data + " (Thread: " + Thread.currentThread().getName() + ")");
                    return data;
                } finally {
                    readLock.unlock();
                }
            }
            
            public void write(String newData) {
                writeLock.lock();
                try {
                    System.out.println("     Writing: " + newData + " (Thread: " + Thread.currentThread().getName() + ")");
                    data = newData;
                } finally {
                    writeLock.unlock();
                }
            }
        }
        
        ReadWriteLockExample rwExample = new ReadWriteLockExample();
        
        // Multiple readers can access simultaneously
        Thread reader1 = new Thread(() -> rwExample.read(), "Reader-1");
        Thread reader2 = new Thread(() -> rwExample.read(), "Reader-2");
        Thread writer = new Thread(() -> rwExample.write("Updated Data"), "Writer");
        
        try {
            reader1.start();
            reader2.start();
            Thread.sleep(100);
            writer.start();
            
            reader1.join();
            reader2.join();
            writer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Condition Variables
        System.out.println("\n🔸 Condition Variables (Producer-Consumer):");
        testProducerConsumer();
    }
    
    private static void testProducerConsumer() {
        class BoundedBuffer<T> {
            private final Queue<T> buffer = new LinkedList<>();
            private final int capacity;
            private final ReentrantLock lock = new ReentrantLock();
            private final Condition notFull = lock.newCondition();
            private final Condition notEmpty = lock.newCondition();
            
            public BoundedBuffer(int capacity) {
                this.capacity = capacity;
            }
            
            public void put(T item) throws InterruptedException {
                lock.lock();
                try {
                    while (buffer.size() == capacity) {
                        notFull.await();
                    }
                    buffer.offer(item);
                    System.out.println("     Produced: " + item + " (Buffer size: " + buffer.size() + ")");
                    notEmpty.signal();
                } finally {
                    lock.unlock();
                }
            }
            
            public T take() throws InterruptedException {
                lock.lock();
                try {
                    while (buffer.isEmpty()) {
                        notEmpty.await();
                    }
                    T item = buffer.poll();
                    System.out.println("     Consumed: " + item + " (Buffer size: " + buffer.size() + ")");
                    notFull.signal();
                    return item;
                } finally {
                    lock.unlock();
                }
            }
        }
        
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(3);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.put(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.take();
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        try {
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // 5. Concurrent Collections
    private static void testConcurrentCollections() {
        System.out.println("🗃️ === CONCURRENT COLLECTIONS ===");
        System.out.println("Purpose: Thread-safe collections optimized for concurrent access\n");
        
        // ConcurrentHashMap
        System.out.println("🔸 ConcurrentHashMap:");
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        
        // Multiple threads updating the map
        Thread[] mapThreads = new Thread[5];
        for (int i = 0; i < mapThreads.length; i++) {
            final int threadId = i;
            mapThreads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    String key = "Thread-" + threadId + "-Key-" + j;
                    concurrentMap.put(key, threadId * 10 + j);
                }
            });
        }
        
        // Start and wait for all threads
        for (Thread thread : mapThreads) thread.start();
        try {
            for (Thread thread : mapThreads) thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("   ConcurrentHashMap entries: " + concurrentMap.size());
        System.out.println("   Sample entries: " + concurrentMap.entrySet().stream().limit(3).toList());
        
        // Atomic operations
        System.out.println("   Atomic compute: " + concurrentMap.compute("Thread-0-Key-0", (k, v) -> v != null ? v * 2 : 0));
        
        // BlockingQueue
        System.out.println("\n🔸 BlockingQueue (ArrayBlockingQueue):");
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(5);
        
        Thread queueProducer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String item = "Item-" + i;
                    blockingQueue.put(item);
                    System.out.println("   Queued: " + item);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread queueConsumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String item = blockingQueue.take();
                    System.out.println("   Dequeued: " + item);
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        try {
            queueProducer.start();
            queueConsumer.start();
            queueProducer.join();
            queueConsumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // ConcurrentLinkedQueue
        System.out.println("\n🔸 ConcurrentLinkedQueue (Lock-free):");
        ConcurrentLinkedQueue<Integer> lockFreeQueue = new ConcurrentLinkedQueue<>();
        
        // Multiple threads adding elements
        Thread[] queueThreads = new Thread[3];
        for (int i = 0; i < queueThreads.length; i++) {
            final int threadId = i;
            queueThreads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    lockFreeQueue.offer(threadId * 10 + j);
                }
            });
        }
        
        for (Thread thread : queueThreads) thread.start();
        try {
            for (Thread thread : queueThreads) thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("   Lock-free queue elements: " + lockFreeQueue);
        System.out.println("   Queue size: " + lockFreeQueue.size());
    }
    
    // 6. Executor Services
    private static void testExecutorServices() {
        System.out.println("⚡ === EXECUTOR SERVICES ===");
        System.out.println("Purpose: High-level thread management and task execution\n");
        
        // Fixed Thread Pool
        System.out.println("🔸 FixedThreadPool:");
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            fixedPool.submit(() -> {
                System.out.println("   Task " + taskId + " executing on thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        fixedPool.shutdown();
        try {
            fixedPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Scheduled Executor
        System.out.println("\n🔸 ScheduledExecutorService:");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        
        // Schedule one-time task
        ScheduledFuture<?> oneTime = scheduler.schedule(() -> {
            System.out.println("   One-time task executed after delay");
        }, 200, TimeUnit.MILLISECONDS);
        
        // Schedule repeating task
        ScheduledFuture<?> repeating = scheduler.scheduleAtFixedRate(() -> {
            System.out.println("   Repeating task execution");
        }, 100, 200, TimeUnit.MILLISECONDS);
        
        try {
            oneTime.get();
            Thread.sleep(500);
            repeating.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        
        scheduler.shutdown();
        
        // ForkJoinPool
        System.out.println("\n🔸 ForkJoinPool (Parallel Processing):");
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            class SumTask extends RecursiveTask<Long> {
                private final int start, end;
                private static final int THRESHOLD = 1000;
                
                SumTask(int start, int end) {
                    this.start = start;
                    this.end = end;
                }
                
                @Override
                protected Long compute() {
                    if (end - start <= THRESHOLD) {
                        // Base case: compute directly
                        return IntStream.range(start, end).mapToLong(i -> i).sum();
                    } else {
                        // Divide and conquer
                        int mid = (start + end) / 2;
                        SumTask leftTask = new SumTask(start, mid);
                        SumTask rightTask = new SumTask(mid, end);
                        
                        leftTask.fork();
                        long rightResult = rightTask.compute();
                        long leftResult = leftTask.join();
                        
                        return leftResult + rightResult;
                    }
                }
            }
            
            SumTask task = new SumTask(1, 10000);
            Long result = forkJoinPool.invoke(task);
            System.out.println("   ForkJoin sum of 1-9999: " + result);
        }
    }
    
    // 7. CompletableFuture
    private static void testCompletableFuture() {
        System.out.println("🔮 === COMPLETABLE FUTURE ===");
        System.out.println("Purpose: Asynchronous programming and composition of async operations\n");
        
        // Simple async computation
        System.out.println("🔸 Basic CompletableFuture:");
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello";
        });
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "World";
        });
        
        // Combine futures
        CompletableFuture<String> combined = future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2);
        
        try {
            System.out.println("   Combined result: " + combined.get());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Chaining operations
        System.out.println("\n🔸 Chaining Operations:");
        CompletableFuture<String> chainedFuture = CompletableFuture
            .supplyAsync(() -> "Input")
            .thenApply(s -> s.toUpperCase())
            .thenApply(s -> s + " PROCESSED")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " ASYNC"));
        
        try {
            System.out.println("   Chained result: " + chainedFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Exception handling
        System.out.println("\n🔸 Exception Handling:");
        CompletableFuture<String> exceptionalFuture = CompletableFuture
            .supplyAsync(() -> {
                if (Math.random() > 0.5) {
                    throw new RuntimeException("Random failure");
                }
                return "Success";
            })
            .handle((result, throwable) -> {
                if (throwable != null) {
                    return "Handled: " + throwable.getMessage();
                }
                return result;
            });
        
        try {
            System.out.println("   Exception handling result: " + exceptionalFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // AllOf and AnyOf
        System.out.println("\n🔸 AllOf and AnyOf:");
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> System.out.println("   Task 1 completed")),
            CompletableFuture.runAsync(() -> System.out.println("   Task 2 completed")),
            CompletableFuture.runAsync(() -> System.out.println("   Task 3 completed"))
        );
        
        try {
            allOf.get();
            System.out.println("   All tasks completed");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    // 8. ThreadLocal and Volatile
    private static void testThreadLocalAndVolatile() {
        System.out.println("🧵 === THREAD LOCAL AND VOLATILE ===");
        System.out.println("Purpose: Thread-specific storage and memory visibility\n");
        
        // ThreadLocal
        System.out.println("🔸 ThreadLocal:");
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("Main Thread Value");
        
        Thread thread1 = new Thread(() -> {
            threadLocal.set("Thread 1 Value");
            System.out.println("   Thread 1 sees: " + threadLocal.get());
        });
        
        Thread thread2 = new Thread(() -> {
            threadLocal.set("Thread 2 Value");
            System.out.println("   Thread 2 sees: " + threadLocal.get());
        });
        
        try {
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
            System.out.println("   Main thread sees: " + threadLocal.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // ThreadLocal with initial value
        ThreadLocal<Integer> threadLocalWithDefault = ThreadLocal.withInitial(() -> 42);
        System.out.println("   ThreadLocal with default: " + threadLocalWithDefault.get());
        
        // Volatile
        System.out.println("\n🔸 Volatile Keyword:");
        VolatileExample volatileExample = new VolatileExample();
        
        Thread writerThread = new Thread(() -> {
            try {
                Thread.sleep(100);
                volatileExample.setFlag(true);
                System.out.println("   Writer thread set flag to true");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread readerThread = new Thread(() -> {
            while (!volatileExample.isFlag()) {
                // Busy wait - this would be an infinite loop without volatile
            }
            System.out.println("   Reader thread detected flag change");
        });
        
        try {
            readerThread.start();
            writerThread.start();
            readerThread.join(1000); // Wait max 1 second
            writerThread.join();
            
            if (readerThread.isAlive()) {
                readerThread.interrupt();
                System.out.println("   Reader thread had to be interrupted (volatile not working)");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n📋 CONCURRENCY BEST PRACTICES:");
        System.out.println("   • Prefer immutable objects when possible");
        System.out.println("   • Use atomic classes for simple shared state");
        System.out.println("   • Use concurrent collections over synchronized collections");
        System.out.println("   • Prefer higher-level abstractions (ExecutorService, CompletableFuture)");
        System.out.println("   • Avoid low-level synchronization when possible");
        System.out.println("   • Use volatile for simple flags and status variables");
        System.out.println("   • Always clean up ThreadLocal variables to prevent memory leaks");
        System.out.println("   • Test concurrent code thoroughly with stress tests");
    }
    
    static class VolatileExample {
        private volatile boolean flag = false;
        
        public boolean isFlag() { return flag; }
        public void setFlag(boolean flag) { this.flag = flag; }
    }
    
    // Helper method for testing counters
    private static void testCounter(Runnable incrementOp, java.util.function.Supplier<Integer> getCountOp, String description) {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    incrementOp.run();
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        int finalCount = getCountOp.get();
        System.out.println("   " + description + " - Expected: 10,000, Actual: " + finalCount + 
                          " " + (finalCount == 10000 ? "✅" : "❌"));
    }
}
