package utilities;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates Collections, Generics, Lambdas, and Streams
 */
public class DataProcessor {
    
    // Generics: Type-safe collections
    public static <T> void printList(List<T> list) {
        System.out.println("List contents: " + list);
    }
    
    // Collections: List, Set, Map
    public static void demonstrateCollections() {
        System.out.println("\n=== Collections Demo ===");
        
        // List: ordered, allows duplicates
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Apple");
        System.out.println("List (with duplicates): " + fruits);
        
        // Set: unique elements
        Set<String> uniqueFruits = new HashSet<>(fruits);
        System.out.println("Set (unique): " + uniqueFruits);
        
        // Map: key-value pairs
        Map<String, Integer> fruitPrices = new HashMap<>();
        fruitPrices.put("Apple", 50);
        fruitPrices.put("Banana", 30);
        fruitPrices.put("Orange", 40);
        System.out.println("Map (prices): " + fruitPrices);
    }
    
    // Lambdas: Anonymous functions
    public static void demonstrateLambdas() {
        System.out.println("\n=== Lambdas Demo ===");
        
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);
        
        // Lambda for sorting
        numbers.sort((a, b) -> a - b);
        System.out.println("Sorted numbers: " + numbers);
        
        // Lambda with functional interface
        Comparator<String> lengthComparator = (s1, s2) -> s1.length() - s2.length();
        List<String> words = Arrays.asList("Java", "Stream", "Lambda");
        words.sort(lengthComparator);
        System.out.println("Sorted by length: " + words);
    }
    
    // Streams: Functional pipeline operations
    public static void demonstrateStreams() {
        System.out.println("\n=== Streams Demo ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter even numbers, square them, collect to list
        List<Integer> evenSquares = numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Even numbers squared: " + evenSquares);
        
        // Count elements matching condition
        long count = numbers.stream()
                .filter(n -> n > 5)
                .count();
        System.out.println("Numbers greater than 5: " + count);
        
        // Sum using reduce
        int sum = numbers.stream()
                .reduce(0, (a, b) -> a + b);
        System.out.println("Sum of all numbers: " + sum);
        
        // Map to uppercase strings
        List<String> words = Arrays.asList("java", "streams", "learning");
        List<String> uppercase = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Uppercase words: " + uppercase);
        
        // Group by length
        Map<Integer, List<String>> grouped = words.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("Grouped by length: " + grouped);
    }
}
