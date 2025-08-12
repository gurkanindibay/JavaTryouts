package com.gindix.collections;

import java.util.function.Consumer;

public class ConsumerTest {
    public static void testConsumer() {
       // Functional Interface
        Consumer<String> consumer;

        // Anonymous Class
        consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("Anonymous class: " + s);
            }
        };
        consumer.accept("Hello");

        // Lambda Expression (Anonymous Method)
        consumer = s -> System.out.println("Lambda: " + s);
        consumer.accept("Hello");

        // Method Reference (Simplified Lambda)
        consumer = System.out::println;
        consumer.accept("Hello");
    }
}
