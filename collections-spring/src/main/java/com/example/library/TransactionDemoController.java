package com.example.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionDemoController {
    
    @Autowired
    private TransactionDemoService transactionDemoService;
    
    @Autowired
    private BookRepository bookRepository;
    
    /**
     * Demonstrate transaction propagation
     */
    @PostMapping("/demo/propagation")
    public ResponseEntity<Map<String, String>> demonstratePropagation(
            @RequestParam String title, 
            @RequestParam String author) {
        
        try {
            transactionDemoService.demonstratePropagation(title, author);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Propagation demonstration completed. Check console logs for details.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Demonstrate transaction isolation
     */
    @PostMapping("/demo/isolation")
    public ResponseEntity<Map<String, String>> demonstrateIsolation(@RequestParam String title) {
        
        try {
            transactionDemoService.demonstrateIsolation(title);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Isolation demonstration completed. Check console logs for details.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Test pessimistic locking
     */
    @PostMapping("/demo/pessimistic-lock")
    public ResponseEntity<BookEntity> testPessimisticLock(@RequestParam String title) {
        
        try {
            BookEntity book = transactionDemoService.borrowBookWithPessimisticLock(title);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Test optimistic locking
     */
    @PostMapping("/demo/optimistic-lock")
    public ResponseEntity<BookEntity> testOptimisticLock(@RequestParam String title) {
        
        try {
            BookEntity book = transactionDemoService.borrowBookWithOptimisticLock(title);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Add a sample book for testing
     */
    @PostMapping("/demo/add-sample-book")
    public ResponseEntity<BookEntity> addSampleBook(
            @RequestParam String title, 
            @RequestParam String author) {
        
        BookEntity book = new BookEntity(title, author);
        BookEntity saved = bookRepository.save(book);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * Get all books
     */
    @GetMapping("/demo/books")
    public ResponseEntity<List<BookEntity>> getAllBooks() {
        List<BookEntity> books = transactionDemoService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    /**
     * Test MANDATORY propagation (should fail when called directly)
     */
    @PostMapping("/demo/mandatory-fail")
    public ResponseEntity<Map<String, String>> testMandatoryFail(@RequestParam Long bookId) {
        
        try {
            transactionDemoService.validateBookInMandatoryTransaction(bookId);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "unexpected_success");
            response.put("message", "This should have failed!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "expected_failure");
            response.put("message", "MANDATORY propagation correctly failed: " + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Test NEVER propagation (should succeed when called directly)
     */
    @PostMapping("/demo/never-success")
    public ResponseEntity<Map<String, String>> testNeverSuccess() {
        
        try {
            transactionDemoService.performNonTransactionalOperation();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "NEVER propagation succeeded (no transaction present)");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
