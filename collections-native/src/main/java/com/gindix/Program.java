package com.gindix;

import java.util.Arrays;
import java.util.stream.Stream;

import com.gindix.collections.CollectionsTest;
import com.gindix.collections.ConcurrencyTest;
import com.gindix.collections.ConsumerTest;
import com.gindix.collections.ShapeDemo;
import com.gindix.collections.Streaming;

/**
 * Main program to demonstrate various Java features
 * Usage: java com.gindix.Program [feature1] [feature2] ... or "all"
 * 
 * Available features:
 * - streaming: Java Stream API demonstrations
 * - consumer: Consumer functional interface tests
 * - collections: Collections framework tests
 * - concurrency: Concurrency and threading tests
 * - shapes: Shape inheritance and polymorphism demo
 * - all: Run all features
 */
public class Program {
    public static void main(String[] args) {
        // Print usage if no arguments provided
        if (args.length == 0) {
            printUsage();
            return;
        }

        // Initialize common test data
        Streaming streaming = new Streaming(
                Stream.of("Apple", "Banana", "Avocado", "Apricot", "Açaí", "Cherry", "Date").toList());

        // Process command line arguments
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "streaming":
                    runStreamingDemo(streaming);
                    break;
                case "consumer":
                    runConsumerDemo();
                    break;
                case "collections":
                    runCollectionsDemo();
                    break;
                case "concurrency":
                    runConcurrencyDemo();
                    break;
                case "shapes":
                    runShapesDemo();
                    break;
                case "all":
                    runAllDemos(streaming);
                    break;
                default:
                    System.out.println("Unknown feature: " + arg);
                    printUsage();
                    break;
            }
        }
    }

    private static void printUsage() {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    JAVA FEATURES DEMO                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Usage: java com.gindix.Program [feature1] [feature2] ... or   ║");
        System.out.println("║        mvn exec:java -Dexec.args=\"[feature1] [feature2] ...\"   ║");
        System.out.println("║                                                                ║");
        System.out.println("║ Available features:                                            ║");
        System.out.println("║  • streaming   - Java Stream API demonstrations               ║");
        System.out.println("║  • consumer    - Consumer functional interface tests          ║");
        System.out.println("║  • collections - Collections framework tests                  ║");
        System.out.println("║  • concurrency - Concurrency and threading tests             ║");
        System.out.println("║  • shapes      - Shape inheritance and polymorphism demo     ║");
        System.out.println("║  • all         - Run all features                             ║");
        System.out.println("║                                                                ║");
        System.out.println("║ Examples:                                                      ║");
        System.out.println("║  mvn exec:java -Dexec.args=\"streaming\"                        ║");
        System.out.println("║  mvn exec:java -Dexec.args=\"streaming collections\"            ║");
        System.out.println("║  mvn exec:java -Dexec.args=\"all\"                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private static void runStreamingDemo(Streaming streaming) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🌊 STREAMING API DEMONSTRATION");
        System.out.println("=".repeat(80));
        streaming.testAllStreamOperations();
    }

    private static void runConsumerDemo() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔧 CONSUMER FUNCTIONAL INTERFACE DEMONSTRATION");
        System.out.println("=".repeat(80));
        ConsumerTest.testConsumer();
    }

    private static void runCollectionsDemo() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📚 COLLECTIONS FRAMEWORK DEMONSTRATION");
        System.out.println("=".repeat(80));
        CollectionsTest.testAllCollections();
    }

    private static void runConcurrencyDemo() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("⚡ CONCURRENCY AND THREADING DEMONSTRATION");
        System.out.println("=".repeat(80));
        ConcurrencyTest.testAllConcurrencyMechanisms();
        System.out.println("✅ Concurrency test completed successfully!");
    }

    private static void runShapesDemo() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔺 SHAPE INHERITANCE AND POLYMORPHISM DEMONSTRATION");
        System.out.println("=".repeat(80));
        ShapeDemo.testFeature();
    }

    private static void runAllDemos(Streaming streaming) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                 RUNNING ALL JAVA FEATURES                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        
        runStreamingDemo(streaming);
        runConsumerDemo();
        runCollectionsDemo();
        runConcurrencyDemo();
        runShapesDemo();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              🎉 ALL DEMONSTRATIONS COMPLETED! 🎉              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}