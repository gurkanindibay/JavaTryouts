package com.gindix.collections;

// 1️⃣ Sealed class hierarchy
sealed interface Shape permits Circle, Rectangle, Square { }

// 2️⃣ Records for immutable data
record Circle(double radius) implements Shape { }
record Rectangle(double width, double height) implements Shape { }
record Square(double side) implements Shape { }

// 3️⃣ Utility class with modern switch
public class ShapeDemo {

    public static String describeShape(Shape shape) {
        // Pattern matching + switch expression + exhaustive check
        return switch (shape) {
            case Circle c -> "A circle with radius " + c.radius();
            case Rectangle r -> {
                double area = r.width() * r.height();
                yield "A rectangle " + r.width() + " x " + r.height() +
                      " (area: " + area + ")";
            }
            case Square s -> "A square with side " + s.side();
        };
        // No default needed — compiler knows we've covered all Shape types
    }

    public static void testFeature() {
        System.out.println("=== Shape Demo ===");
        Shape s1 = new Circle(5);
        Shape s2 = new Rectangle(3, 4);
        Shape s3 = new Square(2);

        System.out.println(describeShape(s1));
        System.out.println(describeShape(s2));
        System.out.println(describeShape(s3));
    }
}
