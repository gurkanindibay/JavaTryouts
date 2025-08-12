package com.example.library;

import com.example.kafka.producer.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAndBorrowFlow() {
        assertTrue(libraryService.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald")));
        assertFalse(libraryService.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald"))); // duplicate
        assertEquals(1, libraryService.borrow("The Great Gatsby"));
        assertEquals(2, libraryService.borrow("The Great Gatsby"));
        assertEquals(-1, libraryService.borrow("Unknown"));
    }
}
