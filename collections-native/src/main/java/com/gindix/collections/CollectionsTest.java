package com.gindix.collections;

import java.util.*;
import java.util.concurrent.*;

public class CollectionsTest {
    public static void testAllCollections() {
        System.out.println("🚀 COMPREHENSIVE JAVA COLLECTIONS TESTING 🚀\n");
        
        testList();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testSet();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testMap();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testQueue();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testDeque();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testConcurrentCollections();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testCollectionOperations();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        testPerformanceCharacteristics();
    }

    private static void testList() {
        System.out.println("📋 === LIST COLLECTIONS: Ordered, Indexed, Allow Duplicates ===");
        System.out.println("Purpose: Store elements in a specific order with index-based access\n");

        // ArrayList - Dynamic array implementation
        System.out.println("🔸 ArrayList (Dynamic Array - Best for random access):");
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Apple"); // Duplicates allowed
        arrayList.add(1, "Cherry"); // Insert at index
        System.out.println("   After adds: " + arrayList);
        System.out.println("   Element at index 0: " + arrayList.get(0));
        System.out.println("   Size: " + arrayList.size());
        System.out.println("   Contains 'Apple': " + arrayList.contains("Apple"));

        // LinkedList - Doubly linked list implementation
        System.out.println("\n🔸 LinkedList (Doubly Linked - Best for frequent insertions/deletions):");
        List<String> linkedList = new LinkedList<>(arrayList);
        linkedList.remove("Banana");
        linkedList.addFirst("First"); // LinkedList specific method
        linkedList.addLast("Last");   // LinkedList specific method
        System.out.println("   After operations: " + linkedList);
        System.out.println("   First element: " + ((LinkedList<String>)linkedList).getFirst());
        System.out.println("   Last element: " + ((LinkedList<String>)linkedList).getLast());

        // Vector - Synchronized ArrayList (legacy)
        System.out.println("\n🔸 Vector (Synchronized ArrayList - Legacy, thread-safe):");
        Vector<Integer> vector = new Vector<>();
        vector.add(10);
        vector.add(20);
        vector.add(30);
        System.out.println("   Vector: " + vector);
        System.out.println("   Capacity: " + vector.capacity());
        System.out.println("   Size: " + vector.size());
        
        // Vector alternatives explanation
        System.out.println("\n🔸 IS VECTOR DEPRECATED? What should you use instead?");
        testVectorAlternatives();
    }
    
    private static void testVectorAlternatives() {
        System.out.println("   📋 Vector Status & Modern Alternatives:");
        
        System.out.println("\n   ❌ Vector Issues (Why it's discouraged):");
        System.out.println("     • Not officially deprecated, but discouraged");
        System.out.println("     • ALL methods are synchronized (performance overhead)");
        System.out.println("     • Synchronization is often unnecessary");
        System.out.println("     • Legacy design from Java 1.0");
        System.out.println("     • Poor performance in single-threaded scenarios");
        System.out.println("     • Capacity grows by 100% (vs ArrayList's 50%)");
        
        // Demonstrate Vector performance overhead
        Vector<String> vector = new Vector<>();
        vector.add("Vector");
        vector.add("Performance");
        vector.add("Overhead");
        System.out.println("     Example Vector: " + vector);
        System.out.println("     All operations synchronized even when not needed!");
        
        System.out.println("\n   ✅ MODERN ALTERNATIVES:");
        
        // 1. ArrayList for single-threaded
        System.out.println("\n   1. ArrayList (Single-threaded scenarios):");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Better");
        arrayList.add("Performance");
        arrayList.add("Choice");
        System.out.println("     ArrayList: " + arrayList);
        System.out.println("     • Faster: No synchronization overhead");
        System.out.println("     • Same functionality as Vector");
        System.out.println("     • Preferred for 99% of use cases");
        
        // 2. Collections.synchronizedList for thread-safety
        System.out.println("\n   2. Collections.synchronizedList (When thread-safety needed):");
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        syncList.add("Thread");
        syncList.add("Safe");
        syncList.add("Alternative");
        System.out.println("     Synchronized ArrayList: " + syncList);
        System.out.println("     • Thread-safe when needed");
        System.out.println("     • More flexible than Vector");
        System.out.println("     • Can wrap any List implementation");
        
        // 3. CopyOnWriteArrayList for concurrent scenarios
        System.out.println("\n   3. CopyOnWriteArrayList (High-concurrency reads):");
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        cowList.add("Concurrent");
        cowList.add("Read");
        cowList.add("Optimized");
        System.out.println("     CopyOnWriteArrayList: " + cowList);
        System.out.println("     • Best for many readers, few writers");
        System.out.println("     • Lock-free reads, thread-safe");
        System.out.println("     • Modern concurrent collection");
        
        System.out.println("\n   📊 PERFORMANCE COMPARISON:");
        System.out.println("     Vector:                 Synchronized (slow)");
        System.out.println("     ArrayList:              Unsynchronized (fast)");
        System.out.println("     Collections.synchronizedList: Synchronized when needed");
        System.out.println("     CopyOnWriteArrayList:   Lock-free reads (fastest for reads)");
        
        System.out.println("\n   💡 RECOMMENDATION:");
        System.out.println("     • Single-threaded: Use ArrayList");
        System.out.println("     • Multi-threaded (occasional): Use Collections.synchronizedList");
        System.out.println("     • Multi-threaded (many reads): Use CopyOnWriteArrayList");
        System.out.println("     • AVOID: Vector (unless maintaining legacy code)");
        
        System.out.println("\n   🔄 Migration Pattern:");
        System.out.println("     OLD: Vector<String> list = new Vector<>();");
        System.out.println("     NEW: List<String> list = new ArrayList<>();");
        System.out.println("     OR:  List<String> list = Collections.synchronizedList(new ArrayList<>());");
    }

    private static void testSet() {
        System.out.println("🔗 === SET COLLECTIONS: Unique Elements Only ===");
        System.out.println("Purpose: Store unique elements without duplicates\n");

        // HashSet - Hash table implementation
        System.out.println("🔸 HashSet (Hash Table - Fast O(1) operations, no order guarantee):");
        Set<String> hashSet = new HashSet<>();
        hashSet.add("X");
        hashSet.add("Y");
        hashSet.add("X"); // Duplicate ignored
        hashSet.add("Z");
        hashSet.add("A");
        System.out.println("   HashSet (unordered): " + hashSet);
        System.out.println("   Size after adding duplicates: " + hashSet.size());
        System.out.println("   Contains 'X': " + hashSet.contains("X"));

        // LinkedHashSet - Maintains insertion order
        System.out.println("\n🔸 LinkedHashSet (Hash + Linked List - Maintains insertion order):");
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("Third");
        linkedHashSet.add("First");
        linkedHashSet.add("Second");
        linkedHashSet.add("First"); // Duplicate ignored
        System.out.println("   LinkedHashSet (insertion order): " + linkedHashSet);

        // TreeSet - Sorted set implementation
        System.out.println("\n🔸 TreeSet (Red-Black Tree - Always sorted, O(log n) operations):");
        Set<String> treeSet = new TreeSet<>(hashSet);
        treeSet.add("B");
        treeSet.add("M");
        System.out.println("   TreeSet (naturally sorted): " + treeSet);
        System.out.println("   First element: " + ((TreeSet<String>)treeSet).first());
        System.out.println("   Last element: " + ((TreeSet<String>)treeSet).last());

        // EnumSet - Specialized for enums
        System.out.println("\n🔸 EnumSet (Optimized for enum types):");
        // Note: EnumSet requires a top-level enum, demonstrated conceptually
        System.out.println("   EnumSet provides efficient operations for enum constants");
        System.out.println("   Example: EnumSet.of(DAY.MONDAY, DAY.FRIDAY) for enum DAY");
    }

    private static void testMap() {
        System.out.println("🗂️ === MAP COLLECTIONS: Key-Value Pairs ===");
        System.out.println("Purpose: Store key-value associations for fast lookups\n");

        // HashMap - Hash table implementation
        System.out.println("🔸 HashMap (Hash Table - Fast O(1) operations, no order guarantee):");
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Apple", 5);
        hashMap.put("Banana", 3);
        hashMap.put("Cherry", 8);
        hashMap.put("Apple", 7); // Overwrites previous value
        System.out.println("   HashMap: " + hashMap);
        System.out.println("   Value for 'Apple': " + hashMap.get("Apple"));
        System.out.println("   Contains key 'Banana': " + hashMap.containsKey("Banana"));
        System.out.println("   Contains value 3: " + hashMap.containsValue(3));
        System.out.println("   KeySet: " + hashMap.keySet());
        System.out.println("   Values: " + hashMap.values());

        // LinkedHashMap - Maintains insertion order
        System.out.println("\n🔸 LinkedHashMap (Hash + Linked List - Maintains insertion order):");
        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("first", "A");
        linkedHashMap.put("second", "B");
        linkedHashMap.put("third", "C");
        System.out.println("   LinkedHashMap (insertion order): " + linkedHashMap);

        // TreeMap - Sorted by keys
        System.out.println("\n🔸 TreeMap (Red-Black Tree - Always sorted by keys):");
        Map<String, Integer> treeMap = new TreeMap<>(hashMap);
        treeMap.put("Date", 12);
        System.out.println("   TreeMap (sorted by keys): " + treeMap);
        System.out.println("   First key: " + ((TreeMap<String, Integer>)treeMap).firstKey());
        System.out.println("   Last key: " + ((TreeMap<String, Integer>)treeMap).lastKey());

        // Hashtable - Synchronized HashMap (legacy)
        System.out.println("\n🔸 Hashtable (Synchronized HashMap - Legacy, thread-safe):");
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("key1", "value1");
        hashtable.put("key2", "value2");
        System.out.println("   Hashtable: " + hashtable);
    }

    private static void testQueue() {
        System.out.println("🚶 === QUEUE COLLECTIONS: FIFO (First In, First Out) ===");
        System.out.println("Purpose: Process elements in order they were added\n");

        // PriorityQueue - Heap-based priority queue
        System.out.println("🔸 PriorityQueue (Binary Heap - Elements ordered by priority):");
        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(5);
        priorityQueue.add(1);
        priorityQueue.add(3);
        priorityQueue.add(9);
        priorityQueue.add(2);
        System.out.println("   PriorityQueue (heap structure): " + priorityQueue);
        System.out.println("   Peek (min element): " + priorityQueue.peek());
        System.out.println("   Poll (remove min): " + priorityQueue.poll());
        System.out.println("   After poll: " + priorityQueue);
        System.out.println("   Size: " + priorityQueue.size());

        // LinkedList as Queue
        System.out.println("\n🔸 LinkedList as Queue (FIFO behavior):");
        Queue<String> linkedQueue = new LinkedList<>();
        linkedQueue.offer("First");
        linkedQueue.offer("Second");
        linkedQueue.offer("Third");
        System.out.println("   Queue: " + linkedQueue);
        System.out.println("   Poll: " + linkedQueue.poll());
        System.out.println("   After poll: " + linkedQueue);

        // ArrayDeque as Queue
        System.out.println("\n🔸 ArrayDeque as Queue (Resizable array implementation):");
        Queue<String> arrayQueue = new ArrayDeque<>();
        arrayQueue.add("A");
        arrayQueue.add("B");
        arrayQueue.add("C");
        System.out.println("   ArrayDeque as Queue: " + arrayQueue);
        System.out.println("   Remove: " + arrayQueue.remove());
        System.out.println("   After remove: " + arrayQueue);
        
        // Performance comparison demonstration
        System.out.println("\n🔸 WHY ArrayDeque vs LinkedList for Queue/Deque?");
        testQueuePerformanceComparison();
    }
    
    private static void testQueuePerformanceComparison() {
        System.out.println("   📊 Performance & Memory Comparison:");
        
        // Create both types
        Queue<Integer> arrayQueue = new ArrayDeque<>();
        Queue<Integer> linkedQueue = new LinkedList<>();
        
        // Add elements to both
        for (int i = 1; i <= 5; i++) {
            arrayQueue.offer(i);
            linkedQueue.offer(i);
        }
        
        System.out.println("   ArrayDeque: " + arrayQueue);
        System.out.println("   LinkedList: " + linkedQueue);
        
        System.out.println("\n   🔍 Key Differences:");
        System.out.println("   ArrayDeque advantages:");
        System.out.println("     • Better cache locality (contiguous memory)");
        System.out.println("     • Lower memory overhead (no node pointers)");
        System.out.println("     • Faster for most queue/deque operations");
        System.out.println("     • Resizable circular buffer implementation");
        System.out.println("     • No extra object allocation per element");
        
        System.out.println("\n   LinkedList advantages:");
        System.out.println("     • Also implements List interface (indexed access)");
        System.out.println("     • Constant time insertion anywhere if you have reference");
        System.out.println("     • No capacity limitations (grows as needed)");
        System.out.println("     • Better for very large datasets with memory constraints");
        
        System.out.println("\n   💡 Recommendation:");
        System.out.println("     • Use ArrayDeque for pure queue/deque operations");
        System.out.println("     • Use LinkedList when you need List operations too");
        System.out.println("     • ArrayDeque is the preferred Deque implementation");
    }

    private static void testDeque() {
        System.out.println("↔️ === DEQUE COLLECTIONS: Double-Ended Queue ===");
        System.out.println("Purpose: Add/remove elements from both ends efficiently\n");

        // ArrayDeque - Resizable array implementation
        System.out.println("🔸 ArrayDeque (Resizable Array - Best general-purpose deque):");
        Deque<String> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst("Middle");
        arrayDeque.addFirst("First");
        arrayDeque.addLast("Last");
        arrayDeque.add("AfterLast"); // Same as addLast
        System.out.println("   ArrayDeque: " + arrayDeque);
        System.out.println("   PeekFirst: " + arrayDeque.peekFirst());
        System.out.println("   PeekLast: " + arrayDeque.peekLast());
        System.out.println("   RemoveFirst: " + arrayDeque.removeFirst());
        System.out.println("   RemoveLast: " + arrayDeque.removeLast());
        System.out.println("   After removals: " + arrayDeque);

        // LinkedList as Deque
        System.out.println("\n🔸 LinkedList as Deque (Doubly linked list):");
        Deque<Integer> linkedDeque = new LinkedList<>();
        linkedDeque.addFirst(2);
        linkedDeque.addFirst(1);
        linkedDeque.addLast(3);
        linkedDeque.addLast(4);
        System.out.println("   LinkedList as Deque: " + linkedDeque);
        System.out.println("   pollFirst: " + linkedDeque.pollFirst());
        System.out.println("   pollLast: " + linkedDeque.pollLast());
        System.out.println("   Remaining: " + linkedDeque);

        // Demonstrate Stack behavior using Deque
        System.out.println("\n🔸 Deque as Stack (LIFO - Last In, First Out):");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("Bottom");
        stack.push("Middle");
        stack.push("Top");
        System.out.println("   Stack: " + stack);
        System.out.println("   Pop: " + stack.pop());
        System.out.println("   Peek: " + stack.peek());
        System.out.println("   After pop: " + stack);
        
        // Comprehensive Stack comparison
        System.out.println("\n🔸 STACK IMPLEMENTATIONS COMPARISON:");
        testStackImplementations();
    }
    
    private static void testStackImplementations() {
        System.out.println("   📚 What should you use for Stack operations?");
        
        // 1. ArrayDeque (RECOMMENDED)
        System.out.println("\n   ✅ ArrayDeque (RECOMMENDED for Stack):");
        Deque<String> arrayStack = new ArrayDeque<>();
        arrayStack.push("First");
        arrayStack.push("Second");
        arrayStack.push("Third");
        System.out.println("     ArrayDeque as Stack: " + arrayStack);
        System.out.println("     Pop: " + arrayStack.pop());
        System.out.println("     Peek: " + arrayStack.peek());
        System.out.println("     Size: " + arrayStack.size());
        
        // 2. Legacy Stack class (NOT RECOMMENDED)
        System.out.println("\n   ❌ Legacy Stack class (NOT RECOMMENDED):");
        Stack<String> legacyStack = new Stack<>();
        legacyStack.push("First");
        legacyStack.push("Second"); 
        legacyStack.push("Third");
        System.out.println("     Legacy Stack: " + legacyStack);
        System.out.println("     Pop: " + legacyStack.pop());
        System.out.println("     Peek: " + legacyStack.peek());
        System.out.println("     Size: " + legacyStack.size());
        System.out.println("     Why avoid? Extends Vector (synchronized overhead)");
        
        // 3. LinkedList as Stack
        System.out.println("\n   ⚠️ LinkedList as Stack (Alternative):");
        Deque<String> linkedStack = new LinkedList<>();
        linkedStack.push("First");
        linkedStack.push("Second");
        linkedStack.push("Third");
        System.out.println("     LinkedList as Stack: " + linkedStack);
        System.out.println("     Pop: " + linkedStack.pop());
        System.out.println("     Peek: " + linkedStack.peek());
        
        System.out.println("\n   📋 Stack Implementation Recommendations:");
        System.out.println("     1st Choice: ArrayDeque - Best performance, modern API");
        System.out.println("     2nd Choice: LinkedList - If you also need List operations");
        System.out.println("     Avoid: Legacy Stack class - Synchronized overhead, extends Vector");
        
        System.out.println("\n   🚀 Performance Comparison:");
        System.out.println("     ArrayDeque: O(1) push/pop, better memory locality");
        System.out.println("     LinkedList: O(1) push/pop, node allocation overhead");
        System.out.println("     Legacy Stack: O(1) push/pop, but synchronized (slow)");
        
        System.out.println("\n   💡 Java Best Practice:");
        System.out.println("     Use: Deque<Type> stack = new ArrayDeque<>();");
        System.out.println("     Methods: push(), pop(), peek(), isEmpty(), size()");
    }

    private static void testConcurrentCollections() {
        System.out.println("🔒 === CONCURRENT COLLECTIONS: Thread-Safe Collections ===");
        System.out.println("Purpose: Safe access from multiple threads without external synchronization\n");

        // CopyOnWriteArrayList - Thread-safe list
        System.out.println("🔸 CopyOnWriteArrayList (Copy-on-write semantics for reads):");
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>(List.of("A", "B", "C"));
        cowList.add("D");
        System.out.println("   CopyOnWriteArrayList: " + cowList);
        System.out.println("   Size: " + cowList.size());
        System.out.println("   Best for: Many reads, few writes (iterators never throw ConcurrentModificationException)");

        // CopyOnWriteArraySet - Thread-safe set
        System.out.println("\n🔸 CopyOnWriteArraySet (Thread-safe set with copy-on-write):");
        CopyOnWriteArraySet<String> cowSet = new CopyOnWriteArraySet<>(Set.of("X", "Y"));
        cowSet.add("Z");
        cowSet.add("X"); // Duplicate ignored
        System.out.println("   CopyOnWriteArraySet: " + cowSet);
        System.out.println("   Contains 'Y': " + cowSet.contains("Y"));

        // ConcurrentHashMap - Thread-safe map
        System.out.println("\n🔸 ConcurrentHashMap (Segment-based locking for high concurrency):");
        ConcurrentHashMap<String, Integer> chMap = new ConcurrentHashMap<>();
        chMap.put("One", 1);
        chMap.put("Two", 2);
        chMap.put("Three", 3);
        chMap.putIfAbsent("One", 11); // Won't overwrite
        System.out.println("   ConcurrentHashMap: " + chMap);
        System.out.println("   Compute new value: " + chMap.compute("Two", (k, v) -> v * 10));
        System.out.println("   After compute: " + chMap);

        // ConcurrentLinkedQueue - Thread-safe queue
        System.out.println("\n🔸 ConcurrentLinkedQueue (Lock-free queue implementation):");
        ConcurrentLinkedQueue<Integer> clQueue = new ConcurrentLinkedQueue<>(List.of(1, 2, 3));
        clQueue.add(4);
        clQueue.offer(5);
        System.out.println("   ConcurrentLinkedQueue: " + clQueue);
        System.out.println("   Poll: " + clQueue.poll());
        System.out.println("   After poll: " + clQueue);

        // LinkedBlockingQueue - Blocking queue
        System.out.println("\n🔸 LinkedBlockingQueue (Blocking operations for producer-consumer):");
        LinkedBlockingQueue<String> lbQueue = new LinkedBlockingQueue<>(3); // Bounded
        lbQueue.add("Hello");
        lbQueue.add("World");
        System.out.println("   LinkedBlockingQueue: " + lbQueue);
        System.out.println("   Remaining capacity: " + lbQueue.remainingCapacity());
        System.out.println("   Take (blocking): " + lbQueue.poll()); // Non-blocking version

        // ConcurrentSkipListSet - Thread-safe sorted set
        System.out.println("\n🔸 ConcurrentSkipListSet (Thread-safe sorted set):");
        ConcurrentSkipListSet<Integer> skipSet = new ConcurrentSkipListSet<>();
        skipSet.add(5);
        skipSet.add(1);
        skipSet.add(3);
        skipSet.add(9);
        System.out.println("   ConcurrentSkipListSet (sorted): " + skipSet);
        System.out.println("   First: " + skipSet.first());
        System.out.println("   Last: " + skipSet.last());
    }

    private static void testCollectionOperations() {
        System.out.println("🛠️ === COLLECTION OPERATIONS & UTILITIES ===");
        System.out.println("Purpose: Common operations available across all collections\n");

        List<String> fruits = new ArrayList<>(Arrays.asList("Apple", "Banana", "Cherry", "Date"));
        System.out.println("🔸 Original list: " + fruits);

        // Collections utility methods
        System.out.println("\n🔸 Collections Utility Methods:");
        Collections.sort(fruits);
        System.out.println("   After sort: " + fruits);
        
        Collections.reverse(fruits);
        System.out.println("   After reverse: " + fruits);
        
        Collections.shuffle(fruits);
        System.out.println("   After shuffle: " + fruits);
        
        System.out.println("   Max element: " + Collections.max(fruits));
        System.out.println("   Min element: " + Collections.min(fruits));
        System.out.println("   Frequency of 'Apple': " + Collections.frequency(fruits, "Apple"));

        // Bulk operations
        System.out.println("\n🔸 Bulk Operations:");
        List<String> citrus = Arrays.asList("Orange", "Lemon", "Lime");
        fruits.addAll(citrus);
        System.out.println("   After addAll: " + fruits);
        
        fruits.removeAll(citrus);
        System.out.println("   After removeAll: " + fruits);
        
        List<String> toKeep = Arrays.asList("Apple", "Banana");
        fruits.retainAll(toKeep);
        System.out.println("   After retainAll: " + fruits);

        // Iteration methods
        System.out.println("\n🔸 Iteration Methods:");
        System.out.print("   Enhanced for-each: ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();
        
        System.out.print("   Iterator: ");
        Iterator<String> iterator = fruits.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
        
        System.out.print("   Stream forEach: ");
        fruits.stream().forEach(fruit -> System.out.print(fruit + " "));
        System.out.println();

        // Conversion operations
        System.out.println("\n🔸 Conversion Operations:");
        String[] array = fruits.toArray(new String[0]);
        System.out.println("   To Array: " + Arrays.toString(array));
        
        Set<String> set = new HashSet<>(fruits);
        System.out.println("   To Set: " + set);
    }

    private static void testPerformanceCharacteristics() {
        System.out.println("⚡ === PERFORMANCE CHARACTERISTICS ===");
        System.out.println("Purpose: Understanding time complexity of different operations\n");

        System.out.println("🔸 LIST PERFORMANCE:");
        System.out.println("   ArrayList:");
        System.out.println("     - Access by index: O(1)");
        System.out.println("     - Add/Remove at end: O(1) amortized");
        System.out.println("     - Add/Remove at beginning/middle: O(n)");
        System.out.println("     - Search: O(n)");
        System.out.println("     - Memory: Contiguous array, good cache locality");

        System.out.println("\n   LinkedList:");
        System.out.println("     - Access by index: O(n)");
        System.out.println("     - Add/Remove at beginning/end: O(1)");
        System.out.println("     - Add/Remove in middle: O(1) if have reference");
        System.out.println("     - Search: O(n)");
        System.out.println("     - Memory: Extra overhead for node pointers");

        System.out.println("\n🔸 SET PERFORMANCE:");
        System.out.println("   HashSet:");
        System.out.println("     - Add/Remove/Contains: O(1) average, O(n) worst case");
        System.out.println("     - No ordering guarantee");
        System.out.println("     - Memory: Hash table with buckets");

        System.out.println("\n   TreeSet:");
        System.out.println("     - Add/Remove/Contains: O(log n)");
        System.out.println("     - Maintains sorted order");
        System.out.println("     - Memory: Red-black tree structure");

        System.out.println("\n🔸 MAP PERFORMANCE:");
        System.out.println("   HashMap:");
        System.out.println("     - Put/Get/Remove: O(1) average, O(n) worst case");
        System.out.println("     - No ordering guarantee");

        System.out.println("\n   TreeMap:");
        System.out.println("     - Put/Get/Remove: O(log n)");
        System.out.println("     - Maintains sorted order by keys");

        System.out.println("\n🔸 QUEUE PERFORMANCE:");
        System.out.println("   ArrayDeque (as Queue/Deque):");
        System.out.println("     - Add/Remove from ends: O(1) amortized");
        System.out.println("     - Memory: Circular buffer, better cache locality");
        System.out.println("     - Overhead: Lower memory per element");
        System.out.println("     - Best for: Pure queue/deque operations");
        
        System.out.println("\n   LinkedList (as Queue/Deque):");
        System.out.println("     - Add/Remove from ends: O(1)");
        System.out.println("     - Memory: Node-based, scattered in memory");
        System.out.println("     - Overhead: Extra pointers per element");
        System.out.println("     - Best for: When you also need List operations");
        
        System.out.println("\n   PriorityQueue:");
        System.out.println("     - Add/Remove: O(log n)");
        System.out.println("     - Peek: O(1)");
        System.out.println("     - Maintains heap property");
        System.out.println("     - Best for: Priority-based processing");

        System.out.println("\n🔸 WHEN TO USE WHICH:");
        System.out.println("   - Use ArrayList for random access and iteration (NOT Vector)");
        System.out.println("   - Use Collections.synchronizedList(new ArrayList<>()) for thread-safety");
        System.out.println("   - Use LinkedList for frequent insertions/deletions");
        System.out.println("   - Use HashSet for uniqueness checking with fast operations");
        System.out.println("   - Use TreeSet when you need sorted unique elements");
        System.out.println("   - Use HashMap for key-value associations");
        System.out.println("   - Use TreeMap for sorted key-value associations");
        System.out.println("   - Use PriorityQueue for priority-based processing");
        System.out.println("   - Use ArrayDeque for stack/queue/deque operations (preferred)");
        System.out.println("   - Use LinkedList for queue when you also need List operations");
        System.out.println("   - Use ArrayDeque for STACK operations (NOT legacy Stack class)");
        System.out.println("   - Use concurrent collections for thread-safe operations");
        System.out.println("   - AVOID Vector and legacy Stack class in new code");
    }
}
