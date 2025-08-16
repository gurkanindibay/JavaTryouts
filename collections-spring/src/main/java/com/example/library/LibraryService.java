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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LibraryService {
    private final BookRepository bookRepository;

    @Autowired(required = false)
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private ObservabilityMetricsService observabilityMetricsService;
    
    private final MeterRegistry meterRegistry;

    @Autowired
    public LibraryService(BookRepository bookRepository, MeterRegistry meterRegistry) {
        this.bookRepository = bookRepository;
        this.meterRegistry = meterRegistry;
        
        // Register business data gauges that derive from actual database state
        meterRegistry.gauge("library.books.total", this, service -> service.bookRepository.count());
        meterRegistry.gauge("library.books.unique", this, service -> service.bookRepository.count());
    }

    @Timed(value = "library.add.book.duration", description = "Time taken to add a book")
    @Counted(value = "library.books.added.total", description = "Total number of books added to the library")
    public boolean addBook(Book book) {
        // Check if book already exists by title (case insensitive)
        Optional<BookEntity> existingBook = bookRepository.findByTitleIgnoreCase(book.getTitle());
        if (existingBook.isPresent()) {
            // Record duplicate attempt - this is meaningful observability data
            observabilityMetricsService.recordDuplicateBookAttempt();
            return false; // duplicate
        }
        
        // Create new BookEntity from Book
        BookEntity bookEntity = new BookEntity(book.getTitle(), book.getAuthor());
        bookRepository.save(bookEntity);
        
        // Send Kafka event for book addition (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BookEvent event = new BookEvent("BOOK_ADDED", 
                generateBookId(book), book.getTitle(), book.getAuthor());
            kafkaProducerService.sendBookEvent(event);
        }
        
        return true;
    }

    public Optional<Book> findByTitle(String title) {
        Optional<BookEntity> bookEntity = bookRepository.findByTitleIgnoreCase(title);
        return bookEntity.map(entity -> new Book(entity.getTitle(), entity.getAuthor()));
    }

    public List<Book> listAll() {
        return bookRepository.findAll().stream()
                .map(entity -> new Book(entity.getTitle(), entity.getAuthor()))
                .collect(Collectors.toList());
    }

    @Timed(value = "library.borrow.duration", description = "Time taken to process book borrow operations")
    @Counted(value = "library.books.borrowed.total", description = "Total number of books borrowed")
    public int borrow(String title) {
        Optional<BookEntity> bookEntityOpt = bookRepository.findByTitleIgnoreCase(title);
        if (bookEntityOpt.isEmpty()) return -1;
        
        BookEntity bookEntity = bookEntityOpt.get();
        int currentCount = bookEntity.getBorrowCount() != null ? bookEntity.getBorrowCount() : 0;
        int newCount = currentCount + 1;
        bookEntity.setBorrowCount(newCount);
        bookRepository.save(bookEntity);
        
        // Send Kafka event for book borrowing (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BorrowEvent borrowEvent = new BorrowEvent("BOOK_BORROWED", 
                title, newCount, "user-" + System.currentTimeMillis());
            kafkaProducerService.sendBorrowEvent(borrowEvent);
        }
        
        return newCount;
    }
    
    public boolean updateBook(String title, Book updatedBook) {
        Optional<BookEntity> bookEntityOpt = bookRepository.findByTitleIgnoreCase(title);
        if (bookEntityOpt.isEmpty()) return false;
        
        // Check if the updated title would create a duplicate
        if (!title.equalsIgnoreCase(updatedBook.getTitle())) {
            Optional<BookEntity> duplicateCheck = bookRepository.findByTitleIgnoreCase(updatedBook.getTitle());
            if (duplicateCheck.isPresent()) {
                return false; // Would create duplicate
            }
        }
        
        BookEntity bookEntity = bookEntityOpt.get();
        bookEntity.setTitle(updatedBook.getTitle());
        bookEntity.setAuthor(updatedBook.getAuthor());
        bookRepository.save(bookEntity);
        
        // Send Kafka event for book update (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BookEvent event = new BookEvent("BOOK_UPDATED", 
                generateBookId(updatedBook), updatedBook.getTitle(), updatedBook.getAuthor());
            kafkaProducerService.sendBookEvent(event);
        }
        
        return true;
    }
    
    @Counted(value = "library.books.removed.total", description = "Total number of books removed from the library")
    public boolean removeBook(String title) {
        Optional<BookEntity> bookEntityOpt = bookRepository.findByTitleIgnoreCase(title);
        if (bookEntityOpt.isEmpty()) return false;
        
        BookEntity bookEntity = bookEntityOpt.get();
        Book book = new Book(bookEntity.getTitle(), bookEntity.getAuthor());
        bookRepository.delete(bookEntity);
        
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
        return (int) bookRepository.count();
    }
    
    private int getUniqueBooks() {
        return (int) bookRepository.count();
    }
    
    public int getTotalBorrowCount() {
        return bookRepository.findAll().stream()
                .mapToInt(entity -> entity.getBorrowCount() != null ? entity.getBorrowCount() : 0)
                .sum();
    }
    
    public Map<String, Integer> getBorrowCountsByTitle() {
        Map<String, Integer> result = new HashMap<>();
        bookRepository.findAll().forEach(entity -> {
            result.put(entity.getTitle(), entity.getBorrowCount() != null ? entity.getBorrowCount() : 0);
        });
        return result;
    }
    
    private String generateBookId(Book book) {
        return book.getTitle().replaceAll("\\s+", "-").toLowerCase() + "-" + book.getAuthor().replaceAll("\\s+", "-").toLowerCase();
    }
}
