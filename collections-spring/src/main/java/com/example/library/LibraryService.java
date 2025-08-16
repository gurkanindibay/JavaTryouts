package com.example.library;

import com.example.config.ObservabilityMetricsService;
import com.example.kafka.model.BookEvent;
import com.example.kafka.model.BorrowEvent;
import com.example.kafka.producer.KafkaProducerService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LibraryService {
    private final List<Book> books = new ArrayList<>();
    private final Set<Book> uniqueBooks = new HashSet<>();
    private final Map<Book, Integer> borrowCounts = new HashMap<>();

    @Autowired(required = false)
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private ObservabilityMetricsService observabilityMetricsService;
    
    private final MeterRegistry meterRegistry;

    @Autowired
    public LibraryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Register business data gauges that derive from actual database/memory state
        // These don't need persistence since they can be recalculated from business data
        meterRegistry.gauge("library.books.total", books, List::size);
        meterRegistry.gauge("library.books.unique", uniqueBooks, Set::size);
    }

    @Timed(value = "library.add.book.duration", description = "Time taken to add a book")
    @Counted(value = "library.books.added.total", description = "Total number of books added to the library")
    public synchronized boolean addBook(Book book) {
        if (!uniqueBooks.add(book)) {
            // Record duplicate attempt - this is meaningful observability data
            observabilityMetricsService.recordDuplicateBookAttempt();
            return false; // duplicate
        }
        books.add(book);
        borrowCounts.put(book, 0);
        
        // No need to update persistent metrics - the gauges will automatically reflect current state
        
        // Send Kafka event for book addition (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BookEvent event = new BookEvent("BOOK_ADDED", 
                generateBookId(book), book.getTitle(), book.getAuthor());
            kafkaProducerService.sendBookEvent(event);
        }
        
        return true;
    }

    public synchronized Optional<Book> findByTitle(String title) {
        return books.stream().filter(b -> b.getTitle().equalsIgnoreCase(title)).findFirst();
    }

    public synchronized List<Book> listAll() {
        return Collections.unmodifiableList(new ArrayList<>(books));
    }

    @Timed(value = "library.borrow.duration", description = "Time taken to process book borrow operations")
    @Counted(value = "library.books.borrowed.total", description = "Total number of books borrowed")
    public synchronized int borrow(String title) {
        Optional<Book> opt = findByTitle(title);
        if (opt.isEmpty()) return -1;
        Book b = opt.get();
        int count = borrowCounts.getOrDefault(b, 0) + 1;
        borrowCounts.put(b, count);
        
        // No need to persist borrow count - this is business data that can be derived from database
        
        // Send Kafka event for book borrowing (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BorrowEvent borrowEvent = new BorrowEvent("BOOK_BORROWED", 
                title, count, "user-" + System.currentTimeMillis());
            kafkaProducerService.sendBorrowEvent(borrowEvent);
        }
        
        return count;
    }
    
    public synchronized boolean updateBook(String title, Book updatedBook) {
        Optional<Book> opt = findByTitle(title);
        if (opt.isEmpty()) return false;
        
        Book existingBook = opt.get();
        int index = books.indexOf(existingBook);
        
        // Remove from unique set and add updated book
        uniqueBooks.remove(existingBook);
        if (!uniqueBooks.add(updatedBook)) {
            // Rollback if updated book would create duplicate
            uniqueBooks.add(existingBook);
            return false;
        }
        
        // Update the book in the list
        books.set(index, updatedBook);
        
        // Transfer borrow count
        Integer borrowCount = borrowCounts.remove(existingBook);
        borrowCounts.put(updatedBook, borrowCount != null ? borrowCount : 0);
        
        // Send Kafka event for book update (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BookEvent event = new BookEvent("BOOK_UPDATED", 
                generateBookId(updatedBook), updatedBook.getTitle(), updatedBook.getAuthor());
            kafkaProducerService.sendBookEvent(event);
        }
        
        return true;
    }
    
    @Counted(value = "library.books.removed.total", description = "Total number of books removed from the library")
    public synchronized boolean removeBook(String title) {
        Optional<Book> opt = findByTitle(title);
        if (opt.isEmpty()) return false;
        
        Book book = opt.get();
        books.remove(book);
        uniqueBooks.remove(book);
        borrowCounts.remove(book);
        
        // No need to update persistent metrics - the gauges will automatically reflect current state
        
        // Send Kafka event for book removal (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BookEvent event = new BookEvent("BOOK_REMOVED", 
                generateBookId(book), book.getTitle(), book.getAuthor());
            kafkaProducerService.sendBookEvent(event);
        }
        
        return true;
    }
    
    // Helper methods for gauges
    private int getTotalBooks() {
        return books.size();
    }
    
    private int getUniqueBooks() {
        return uniqueBooks.size();
    }
    
    public synchronized int getTotalBorrowCount() {
        return borrowCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public synchronized Map<String, Integer> getBorrowCountsByTitle() {
        Map<String, Integer> result = new HashMap<>();
        borrowCounts.forEach((book, count) -> result.put(book.getTitle(), count));
        return result;
    }
    
    private String generateBookId(Book book) {
        return book.getTitle().replaceAll("\\s+", "-").toLowerCase() + "-" + book.getAuthor().replaceAll("\\s+", "-").toLowerCase();
    }
}
