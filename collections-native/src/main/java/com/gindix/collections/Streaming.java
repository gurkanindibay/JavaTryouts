package com.gindix.collections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.function.Function;

/**
 * Comprehensive demonstration of Java Stream API capabilities
 * This class showcases various stream operations with detailed examples
 */
public class Streaming {
    private List<String> strings;
    private List<Integer> numbers;

    public Streaming(List<String> strings) {
        this.strings = strings;
        this.numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30);
    }

    /**
     * Demonstrates basic forEach operation
     */
    public void testPrintAll() {
        System.out.println("\n=== BASIC FOREACH OPERATION ===");
        System.out.println("Original strings:");
        strings.stream().forEach(System.out::println);
    }

    /**
     * Demonstrates filtering with various conditions
     */
    public void testPrintAllWithFilter() {
        System.out.println("\n=== FILTERING OPERATIONS ===");
        
        // Filter by substring
        System.out.println("Strings containing 'A':");
        strings.stream()
                .filter(s -> s.contains("A"))
                .forEach(System.out::println);

        // Filter by length
        System.out.println("\nStrings with length > 5:");
        strings.stream()
                .filter(s -> s.length() > 5)
                .forEach(System.out::println);

        // Filter numbers - even numbers
        System.out.println("\nEven numbers:");
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .forEach(System.out::println);

        System.out.println("\nFunctional Interface Demo:");
        MyFunctionalInterface myFunc = () -> System.out.println("Executing custom functional interface...");
        myFunc.execute();
    }

    /**
     * Demonstrates mapping operations - transforming elements
     */
    public void testMapping() {
        System.out.println("\n=== MAPPING OPERATIONS ===");
        
        // Transform to uppercase
        System.out.println("Strings in uppercase:");
        strings.stream()
                .map(String::toUpperCase)
                .forEach(System.out::println);

        // Transform to string lengths
        System.out.println("\nString lengths:");
        strings.stream()
                .map(String::length)
                .forEach(length -> System.out.println("Length: " + length));

        // Transform numbers to squares
        System.out.println("\nSquares of numbers:");
        numbers.stream()
                .map(n -> n * n)
                .forEach(square -> System.out.println("Square: " + square));

        // flatMap - flattening nested structures
        System.out.println("\nFlatMap example - characters from strings:");
        strings.stream()
                .flatMap(s -> s.chars().mapToObj(c -> (char) c))
                .forEach(character -> System.out.print(character + " "));
        System.out.println();
    }

    /**
     * Demonstrates sorting operations
     */
    public void testSorting() {
        System.out.println("\n=== SORTING OPERATIONS ===");
        
        // Sort strings alphabetically
        System.out.println("Strings sorted alphabetically:");
        strings.stream()
                .sorted()
                .forEach(System.out::println);

        // Sort by length
        System.out.println("\nStrings sorted by length:");
        strings.stream()
                .sorted(Comparator.comparing(String::length))
                .forEach(System.out::println);

        // Sort numbers in descending order
        System.out.println("\nNumbers in descending order:");
        numbers.stream()
                .sorted(Collections.reverseOrder())
                .forEach(System.out::println);
    }

    /**
     * Demonstrates limiting and skipping operations
     */
    public void testLimitAndSkip() {
        System.out.println("\n=== LIMIT AND SKIP OPERATIONS ===");
        
        // First 3 strings
        System.out.println("First 3 strings:");
        strings.stream()
                .limit(3)
                .forEach(System.out::println);

        // Skip first 2, then take next 3
        System.out.println("\nSkip first 2, take next 3:");
        strings.stream()
                .skip(2)
                .limit(3)
                .forEach(System.out::println);

        // First 5 even numbers
        System.out.println("\nFirst 5 even numbers:");
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .limit(5)
                .forEach(System.out::println);
    }

    /**
     * Demonstrates distinct operation
     */
    public void testDistinct() {
        System.out.println("\n=== DISTINCT OPERATIONS ===");
        
        // Create list with duplicates
        List<String> duplicates = Arrays.asList("Apple", "Banana", "Apple", "Cherry", "Banana", "Date");
        System.out.println("Original list with duplicates:");
        duplicates.forEach(System.out::println);

        System.out.println("\nAfter removing duplicates:");
        duplicates.stream()
                .distinct()
                .forEach(System.out::println);

        // Distinct by length
        System.out.println("\nStrings with distinct lengths:");
        strings.stream()
                .collect(Collectors.toMap(String::length, Function.identity(), (s1, s2) -> s1))
                .values()
                .forEach(System.out::println);
    }

    /**
     * Demonstrates reduction operations
     */
    public void testReduction() {
        System.out.println("\n=== REDUCTION OPERATIONS ===");
        
        // Sum of numbers
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("Sum of numbers: " + sum);

        // Product of numbers (first 5 to avoid overflow)
        int product = numbers.stream()
                .limit(5)
                .reduce(1, (a, b) -> a * b);
        System.out.println("Product of first 5 numbers: " + product);

        // Concatenate strings
        String concatenated = strings.stream()
                .reduce("", (s1, s2) -> s1 + " " + s2);
        System.out.println("Concatenated strings:" + concatenated);

        // Find longest string
        Optional<String> longest = strings.stream()
                .reduce((s1, s2) -> s1.length() > s2.length() ? s1 : s2);
        System.out.println("Longest string: " + longest.orElse("None"));
    }

    /**
     * Demonstrates collectors - collecting to different data structures
     */
    public void testCollectors() {
        System.out.println("\n=== COLLECTORS OPERATIONS ===");
        
        // Collect to List
        List<String> uppercaseList = strings.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Uppercase list: " + uppercaseList);

        // Collect to Set
        Set<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toSet());
        System.out.println("Even numbers set: " + evenNumbers);

        // Collect to Map (string -> length)
        Map<String, Integer> stringLengths = strings.stream()
                .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("String lengths map: " + stringLengths);

        // Group by length
        Map<Integer, List<String>> groupedByLength = strings.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("Grouped by length: " + groupedByLength);

        // Partition by condition (even/odd numbers)
        Map<Boolean, List<Integer>> partitioned = numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Partitioned numbers (even/odd): " + partitioned);
    }

    /**
     * Demonstrates finding operations
     */
    public void testFinding() {
        System.out.println("\n=== FINDING OPERATIONS ===");
        
        // Find first
        Optional<String> first = strings.stream()
                .filter(s -> s.startsWith("A"))
                .findFirst();
        System.out.println("First string starting with 'A': " + first.orElse("Not found"));

        // Find any
        Optional<String> any = strings.stream()
                .filter(s -> s.length() > 5)
                .findAny();
        System.out.println("Any string with length > 5: " + any.orElse("Not found"));

        // Check if all match
        boolean allStartWithLetter = strings.stream()
                .allMatch(s -> Character.isLetter(s.charAt(0)));
        System.out.println("All strings start with a letter: " + allStartWithLetter);

        // Check if any match
        boolean anyContainsE = strings.stream()
                .anyMatch(s -> s.contains("e"));
        System.out.println("Any string contains 'e': " + anyContainsE);

        // Check if none match
        boolean noneContainsZ = strings.stream()
                .noneMatch(s -> s.contains("Z"));
        System.out.println("No string contains 'Z': " + noneContainsZ);
    }

    /**
     * Demonstrates counting and statistics
     */
    public void testStatistics() {
        System.out.println("\n=== STATISTICS OPERATIONS ===");
        
        // Count elements
        long count = strings.stream()
                .filter(s -> s.length() > 4)
                .count();
        System.out.println("Count of strings with length > 4: " + count);

        // Statistics for numbers
        IntSummaryStatistics stats = numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        System.out.println("Number statistics:");
        System.out.println("  Count: " + stats.getCount());
        System.out.println("  Sum: " + stats.getSum());
        System.out.println("  Min: " + stats.getMin());
        System.out.println("  Max: " + stats.getMax());
        System.out.println("  Average: " + stats.getAverage());

        // Min and Max for strings by length
        Optional<String> shortest = strings.stream()
                .min(Comparator.comparing(String::length));
        Optional<String> longest = strings.stream()
                .max(Comparator.comparing(String::length));
        System.out.println("Shortest string: " + shortest.orElse("None"));
        System.out.println("Longest string: " + longest.orElse("None"));
    }

    /**
     * Demonstrates parallel streams
     */
    public void testParallelStreams() {
        System.out.println("\n=== PARALLEL STREAMS ===");
        
        // Sequential vs Parallel processing
        System.out.println("Sequential processing:");
        long startTime = System.currentTimeMillis();
        strings.stream()
                .map(s -> {
                    // Simulate some processing time
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                    return s.toUpperCase();
                })
                .forEach(s -> System.out.println("Sequential: " + s));
        long sequentialTime = System.currentTimeMillis() - startTime;

        System.out.println("\nParallel processing:");
        startTime = System.currentTimeMillis();
        strings.parallelStream()
                .map(s -> {
                    // Simulate some processing time
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                    return s.toUpperCase();
                })
                .forEach(s -> System.out.println("Parallel: " + s));
        long parallelTime = System.currentTimeMillis() - startTime;

        System.out.println("Sequential time: " + sequentialTime + "ms");
        System.out.println("Parallel time: " + parallelTime + "ms");
    }

    /**
     * Demonstrates creating streams from various sources
     */
    public void testStreamCreation() {
        System.out.println("\n=== STREAM CREATION METHODS ===");
        
        // From array
        String[] array = {"Stream", "from", "array"};
        System.out.println("Stream from array:");
        Arrays.stream(array).forEach(System.out::println);

        // From range
        System.out.println("\nStream from range (1 to 5):");
        IntStream.range(1, 6).forEach(System.out::println);

        // From builder
        System.out.println("\nStream from builder:");
        Stream.<String>builder()
                .add("Built")
                .add("with")
                .add("builder")
                .build()
                .forEach(System.out::println);

        // From generate (infinite stream, limited)
        System.out.println("\nGenerated stream (first 5 random numbers):");
        Stream.generate(Math::random)
                .limit(5)
                .forEach(n -> System.out.println("Random: " + n));

        // From iterate
        System.out.println("\nIterated stream (powers of 2, first 6):");
        Stream.iterate(1, n -> n * 2)
                .limit(6)
                .forEach(n -> System.out.println("Power of 2: " + n));
    }

    /**
     * Main test method that runs all stream operations
     */
    public void testAllStreamOperations() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        JAVA STREAM API DEMO           ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        testPrintAll();
        testPrintAllWithFilter();
        testMapping();
        testSorting();
        testLimitAndSkip();
        testDistinct();
        testReduction();
        testCollectors();
        testFinding();
        testStatistics();
        testStreamCreation();
        testParallelStreams();
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     STREAM API DEMO COMPLETED         ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}
