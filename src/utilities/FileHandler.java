package utilities;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Demonstrates NIO, File I/O, and Exception Handling
 */
public class FileHandler {
    
    /**
     * Write data to file using try-with-resources
     */
    public static void writeToFile(String filename, List<String> lines) {
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("File written successfully: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
    
    /**
     * Read data from file using try-with-resources
     */
    public static List<String> readFromFile(String filename) {
        Path path = Paths.get(filename);
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(path);
            System.out.println("File read successfully: " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
    
    /**
     * Append to file
     */
    public static void appendToFile(String filename, String content) {
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            writer.write(content);
            writer.newLine();
            System.out.println("Content appended to file: " + filename);
        } catch (IOException e) {
            System.err.println("Error appending to file: " + e.getMessage());
        }
    }
    
    /**
     * Delete file if exists
     */
    public static void deleteFile(String filename) {
        try {
            if (Files.deleteIfExists(Paths.get(filename))) {
                System.out.println("File deleted: " + filename);
            } else {
                System.out.println("File not found: " + filename);
            }
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }
    
    /**
     * Check file properties
     */
    public static void checkFileProperties(String filename) {
        Path path = Paths.get(filename);
        try {
            if (Files.exists(path)) {
                System.out.println("\nFile: " + filename);
                System.out.println("  Size: " + Files.size(path) + " bytes");
                System.out.println("  Is Directory: " + Files.isDirectory(path));
                System.out.println("  Is Regular File: " + Files.isRegularFile(path));
                System.out.println("  Is Readable: " + Files.isReadable(path));
                System.out.println("  Is Writable: " + Files.isWritable(path));
            } else {
                System.out.println("File not found: " + filename);
            }
        } catch (IOException e) {
            System.err.println("Error checking file properties: " + e.getMessage());
        }
    }
}
