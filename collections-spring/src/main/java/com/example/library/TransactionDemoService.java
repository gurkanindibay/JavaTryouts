package com.example.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

/**
 * Service demonstrating Spring Transaction Propagation and Isolation levels
 */
@Service
public class TransactionDemoService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    // Get self proxy for internal method calls
    private TransactionDemoService getSelf() {
        return applicationContext.getBean(TransactionDemoService.class);
    }
    
    // ========== PROPAGATION EXAMPLES ==========
    
    /**
     * REQUIRED (Default): Join existing transaction or create new one
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public BookEntity addBookWithRequired(String title, String author) {
        System.out.println("addBookWithRequired - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        
        BookEntity book = new BookEntity(title, author);
        BookEntity saved = bookRepository.save(book);
        
        // This will join the same transaction
        getSelf().updateBookCountInSameTransaction(saved.getId());
        
        return saved;
    }
    
    /**
     * REQUIRES_NEW: Always create a new transaction, suspending current one
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBookCountInNewTransaction(Long bookId) {
        System.out.println("updateBookCountInNewTransaction - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        
        Optional<BookEntity> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            BookEntity entity = book.get();
            entity.setBorrowCount(entity.getBorrowCount() + 1);
            bookRepository.save(entity);
            System.out.println("Updated book count in NEW transaction");
        }
    }
    
    /**
     * REQUIRED: Join existing transaction (helper method)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateBookCountInSameTransaction(Long bookId) {
        System.out.println("updateBookCountInSameTransaction - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        
        Optional<BookEntity> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            BookEntity entity = book.get();
            entity.setBorrowCount(entity.getBorrowCount() + 1);
            bookRepository.save(entity);
            System.out.println("Updated book count in SAME transaction");
        }
    }
    
    /**
     * SUPPORTS: Join existing transaction if present, otherwise execute non-transactionally
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<BookEntity> getAllBooks() {
        System.out.println("getAllBooks - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        return bookRepository.findAll();
    }
    
    /**
     * NOT_SUPPORTED: Execute non-transactionally, suspending current transaction
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void logBookOperation(String operation) {
        System.out.println("logBookOperation - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("Logging operation: " + operation + " (executed without transaction)");
    }
    
    /**
     * MANDATORY: Must have an existing transaction, throw exception if none
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void validateBookInMandatoryTransaction(Long bookId) {
        System.out.println("validateBookInMandatoryTransaction - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        System.out.println("Book validation completed in mandatory transaction");
    }
    
    /**
     * NEVER: Execute non-transactionally, throw exception if transaction exists
     */
    @Transactional(propagation = Propagation.NEVER)
    public void performNonTransactionalOperation() {
        System.out.println("performNonTransactionalOperation - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("This operation must never run in a transaction");
    }
    
    /**
     * NESTED: Execute within nested transaction if supported
     */
    @Transactional(propagation = Propagation.NESTED)
    public void updateBookInNestedTransaction(Long bookId, String newTitle) {
        System.out.println("updateBookInNestedTransaction - Transaction active: " + 
            TransactionSynchronizationManager.isActualTransactionActive());
        
        Optional<BookEntity> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            BookEntity entity = book.get();
            entity.setTitle(newTitle);
            bookRepository.save(entity);
            System.out.println("Updated book in nested transaction");
        }
    }
    
    // ========== ISOLATION EXAMPLES ==========
    
    /**
     * READ_UNCOMMITTED: Allows dirty reads, non-repeatable reads, and phantom reads
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public BookEntity readUncommittedExample(String title) {
        System.out.println("Reading with READ_UNCOMMITTED isolation");
        Optional<BookEntity> book = bookRepository.findByTitleIgnoreCase(title);
        
        // This might read uncommitted changes from other transactions
        return book.orElse(null);
    }
    
    /**
     * READ_COMMITTED: Prevents dirty reads, allows non-repeatable reads and phantom reads
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookEntity readCommittedExample(String title) {
        System.out.println("Reading with READ_COMMITTED isolation");
        Optional<BookEntity> book = bookRepository.findByTitleIgnoreCase(title);
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Re-read - might get different result (non-repeatable read)
        Optional<BookEntity> book2 = bookRepository.findByTitleIgnoreCase(title);
        
        return book.orElse(book2.orElse(null));
    }
    
    /**
     * REPEATABLE_READ: Prevents dirty reads and non-repeatable reads, allows phantom reads
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookEntity repeatableReadExample(String title) {
        System.out.println("Reading with REPEATABLE_READ isolation");
        Optional<BookEntity> book = bookRepository.findByTitleIgnoreCase(title);
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Re-read - should get same result (repeatable read)
        Optional<BookEntity> book2 = bookRepository.findByTitleIgnoreCase(title);
        
        System.out.println("First read result: " + (book.isPresent() ? book.get().getTitle() : "null"));
        System.out.println("Second read result: " + (book2.isPresent() ? book2.get().getTitle() : "null"));
        
        return book.orElse(null);
    }
    
    /**
     * SERIALIZABLE: Prevents all above phenomena (highest isolation)
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookEntity serializableExample(String title) {
        System.out.println("Reading with SERIALIZABLE isolation");
        Optional<BookEntity> book = bookRepository.findByTitleIgnoreCase(title);
        
        // Count all books
        long count1 = bookRepository.count();
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Count again - should be same (no phantom reads)
        long count2 = bookRepository.count();
        
        System.out.println("First count: " + count1 + ", Second count: " + count2);
        
        return book.orElse(null);
    }
    
    // ========== LOCKING EXAMPLES ==========
    
    /**
     * Pessimistic locking example
     */
    @Transactional
    public BookEntity borrowBookWithPessimisticLock(String title) {
        System.out.println("Borrowing book with pessimistic lock");
        
        Optional<BookEntity> book = bookRepository.findByTitleWithPessimisticLock(title);
        if (book.isPresent()) {
            BookEntity entity = book.get();
            entity.setBorrowCount(entity.getBorrowCount() + 1);
            return bookRepository.save(entity);
        }
        
        throw new IllegalArgumentException("Book not found: " + title);
    }
    
    /**
     * Optimistic locking example
     */
    @Transactional
    public BookEntity borrowBookWithOptimisticLock(String title) {
        System.out.println("Borrowing book with optimistic lock");
        
        Optional<BookEntity> book = bookRepository.findByTitleWithOptimisticLock(title);
        if (book.isPresent()) {
            BookEntity entity = book.get();
            entity.setBorrowCount(entity.getBorrowCount() + 1);
            return bookRepository.save(entity); // Will throw OptimisticLockingFailureException if version changed
        }
        
        throw new IllegalArgumentException("Book not found: " + title);
    }
    
    // ========== DEMONSTRATION METHODS ==========
    
    /**
     * Demonstrates different propagation behaviors
     */
    @Transactional
    public void demonstratePropagation(String title, String author) {
        System.out.println("\n=== PROPAGATION DEMONSTRATION ===");
        
        // Add book with REQUIRED
        BookEntity book = getSelf().addBookWithRequired(title, author);
        
        // Update in new transaction (REQUIRES_NEW)
        getSelf().updateBookCountInNewTransaction(book.getId());
        
        // Log without transaction (NOT_SUPPORTED)
        getSelf().logBookOperation("Book added and updated");
        
        // Validate in mandatory transaction (MANDATORY)
        try {
            getSelf().validateBookInMandatoryTransaction(book.getId());
        } catch (Exception e) {
            System.out.println("Mandatory validation failed: " + e.getMessage());
        }
        
        // Update in nested transaction (NESTED)
        getSelf().updateBookInNestedTransaction(book.getId(), title + " (Updated)");
        
        System.out.println("=== PROPAGATION DEMONSTRATION COMPLETE ===\n");
    }
    
    /**
     * Demonstrates different isolation levels
     */
    @Transactional
    public void demonstrateIsolation(String title) {
        System.out.println("\n=== ISOLATION DEMONSTRATION ===");
        
        // Test different isolation levels
        getSelf().readUncommittedExample(title);
        getSelf().readCommittedExample(title);
        getSelf().repeatableReadExample(title);
        getSelf().serializableExample(title);
        
        System.out.println("=== ISOLATION DEMONSTRATION COMPLETE ===\n");
    }
}
