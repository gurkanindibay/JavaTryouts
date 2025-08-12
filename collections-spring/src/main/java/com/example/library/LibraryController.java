package com.example.library;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class LibraryController {
    private final LibraryService service;

    public LibraryController(LibraryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Book book) {
        boolean added = service.addBook(book);
        return added ? ResponseEntity.ok("Book added") : ResponseEntity.badRequest().body("Duplicate book");
    }

    @GetMapping
    public List<Book> all() {
        return service.listAll();
    }

    @PostMapping("/borrow/{title}")
    public ResponseEntity<String> borrow(@PathVariable String title) {
        int count = service.borrow(title);
        if (count < 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Borrowed '" + title + "' times: " + count);
    }
}
