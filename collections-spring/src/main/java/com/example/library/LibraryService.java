package com.example.library;

import com.example.kafka.model.BookEvent;
import com.example.kafka.model.BorrowEvent;
import com.example.kafka.producer.KafkaProducerService;
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

    public synchronized boolean addBook(Book book) {
        if (!uniqueBooks.add(book)) {
            return false; // duplicate
        }
        books.add(book);
        borrowCounts.put(book, 0);
        
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

    public synchronized int borrow(String title) {
        Optional<Book> opt = findByTitle(title);
        if (opt.isEmpty()) return -1;
        Book b = opt.get();
        int count = borrowCounts.getOrDefault(b, 0) + 1;
        borrowCounts.put(b, count);
        
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
    
    public synchronized boolean removeBook(String title) {
        Optional<Book> opt = findByTitle(title);
        if (opt.isEmpty()) return false;
        
        Book book = opt.get();
        books.remove(book);
        uniqueBooks.remove(book);
        borrowCounts.remove(book);
        
        // Send Kafka event for book removal (if Kafka is enabled)
        if (kafkaProducerService != null) {
            BookEvent event = new BookEvent("BOOK_REMOVED", 
                generateBookId(book), book.getTitle(), book.getAuthor());
            kafkaProducerService.sendBookEvent(event);
        }
        
        return true;
    }
    
    private String generateBookId(Book book) {
        return book.getTitle().replaceAll("\\s+", "-").toLowerCase() + "-" + book.getAuthor().replaceAll("\\s+", "-").toLowerCase();
    }
}
