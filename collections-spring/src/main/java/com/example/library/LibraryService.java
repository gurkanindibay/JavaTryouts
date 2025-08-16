package com.example.library;

import com.example.kafka.model.BookEvent;
import com.example.kafka.model.BorrowEvent;
import com.example.kafka.producer.KafkaProducerService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
    
    // Micrometer metrics
    private final MeterRegistry meterRegistry;
    private final Counter booksAddedCounter;
    private final Counter booksBorrowedCounter;
    private final Counter booksRemovedCounter;
    private final Counter duplicateBookAttemptsCounter;
    private final Timer borrowOperationTimer;
    private final Timer addBookTimer;

    @Autowired
    public LibraryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.booksAddedCounter = Counter.builder("library.books.added.total")
                .description("Total number of books added to the library")
                .register(meterRegistry);
                
        this.booksBorrowedCounter = Counter.builder("library.books.borrowed.total")
                .description("Total number of books borrowed")
                .register(meterRegistry);
                
        this.booksRemovedCounter = Counter.builder("library.books.removed.total")
                .description("Total number of books removed from the library")
                .register(meterRegistry);
                
        this.duplicateBookAttemptsCounter = Counter.builder("library.books.duplicate.attempts.total")
                .description("Total number of duplicate book addition attempts")
                .register(meterRegistry);
                
        // Initialize timers
        this.borrowOperationTimer = Timer.builder("library.borrow.duration")
                .description("Time taken to process book borrow operations")
                .register(meterRegistry);
                
        this.addBookTimer = Timer.builder("library.add.book.duration")
                .description("Time taken to add a book")
                .register(meterRegistry);
        
        // Initialize gauges
        Gauge.builder("library.books.total", this, LibraryService::getTotalBooks)
                .description("Current total number of books in the library")
                .register(meterRegistry);
                
        Gauge.builder("library.books.unique", this, LibraryService::getUniqueBooks)
                .description("Current number of unique books in the library")
                .register(meterRegistry);
                
        Gauge.builder("library.borrow.count.total", this, LibraryService::getTotalBorrowCount)
                .description("Current total borrow count across all books")
                .register(meterRegistry);
    }

    public synchronized boolean addBook(Book book) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (!uniqueBooks.add(book)) {
                duplicateBookAttemptsCounter.increment();
                return false; // duplicate
            }
            books.add(book);
            borrowCounts.put(book, 0);
            booksAddedCounter.increment();
            
            // Send Kafka event for book addition (if Kafka is enabled)
            if (kafkaProducerService != null) {
                BookEvent event = new BookEvent("BOOK_ADDED", 
                    generateBookId(book), book.getTitle(), book.getAuthor());
                kafkaProducerService.sendBookEvent(event);
            }
            
            return true;
        } finally {
            sample.stop(addBookTimer);
        }
    }

    public synchronized Optional<Book> findByTitle(String title) {
        return books.stream().filter(b -> b.getTitle().equalsIgnoreCase(title)).findFirst();
    }

    public synchronized List<Book> listAll() {
        return Collections.unmodifiableList(new ArrayList<>(books));
    }

    public synchronized int borrow(String title) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Optional<Book> opt = findByTitle(title);
            if (opt.isEmpty()) return -1;
            Book b = opt.get();
            int count = borrowCounts.getOrDefault(b, 0) + 1;
            borrowCounts.put(b, count);
            booksBorrowedCounter.increment();
            
            // Send Kafka event for book borrowing (if Kafka is enabled)
            if (kafkaProducerService != null) {
                BorrowEvent borrowEvent = new BorrowEvent("BOOK_BORROWED", 
                    title, count, "user-" + System.currentTimeMillis());
                kafkaProducerService.sendBorrowEvent(borrowEvent);
            }
            
            return count;
        } finally {
            sample.stop(borrowOperationTimer);
        }
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
    
    public synchronized boolean removeBook(String title) {
        Optional<Book> opt = findByTitle(title);
        if (opt.isEmpty()) return false;
        
        Book book = opt.get();
        books.remove(book);
        uniqueBooks.remove(book);
        borrowCounts.remove(book);
        booksRemovedCounter.increment();
        
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
