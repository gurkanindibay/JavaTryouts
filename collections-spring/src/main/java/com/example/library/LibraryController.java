package com.example.library;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Library Management", description = "Operations for managing library books")
public class LibraryController {
    private final LibraryService service;

    public LibraryController(LibraryService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Add a new book", description = "Add a new book to the library collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added successfully"),
            @ApiResponse(responseCode = "400", description = "Duplicate book - book already exists")
    })
    public ResponseEntity<String> add(@RequestBody Book book) {
        boolean added = service.addBook(book);
        return added ? ResponseEntity.ok("Book added") : ResponseEntity.badRequest().body("Duplicate book");
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve a list of all books in the library")
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully")
    public List<Book> all() {
        return service.listAll();
    }

    @PostMapping("/borrow/{title}")
    @Operation(summary = "Borrow a book", description = "Borrow a book by title and increment borrow count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<String> borrow(
            @Parameter(description = "Title of the book to borrow", required = true)
            @PathVariable String title) {
        int count = service.borrow(title);
        if (count < 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Borrowed '" + title + "' times: " + count);
    }
}
