package com.example.kafka.controller;

import com.example.kafka.model.BookEvent;
import com.example.kafka.model.BorrowEvent;
import com.example.kafka.producer.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Send a book event manually
     */
    @PostMapping("/book-event")
    public ResponseEntity<Map<String, String>> sendBookEvent(@RequestBody BookEventRequest request) {
        BookEvent event = new BookEvent(
            request.getEventType(),
            request.getBookId(),
            request.getTitle(),
            request.getAuthor()
        );
        
        kafkaProducerService.sendBookEvent(event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Book event sent to Kafka topic");
        response.put("eventType", request.getEventType());
        response.put("bookTitle", request.getTitle());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Send a borrow event manually
     */
    @PostMapping("/borrow-event")
    public ResponseEntity<Map<String, String>> sendBorrowEvent(@RequestBody BorrowEventRequest request) {
        BorrowEvent event = new BorrowEvent(
            request.getEventType(),
            request.getBookTitle(),
            request.getBorrowCount(),
            request.getUserId()
        );
        
        kafkaProducerService.sendBorrowEvent(event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Borrow event sent to Kafka topic");
        response.put("bookTitle", request.getBookTitle());
        response.put("borrowCount", String.valueOf(request.getBorrowCount()));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate partition-specific message sending
     */
    @PostMapping("/partition/{partition}")
    public ResponseEntity<Map<String, String>> sendToPartition(
            @PathVariable int partition,
            @RequestParam String message) {
        
        kafkaProducerService.sendToSpecificPartition(message, partition);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Message sent to specific partition");
        response.put("partition", String.valueOf(partition));
        response.put("content", message);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate custom key-based partitioning
     */
    @PostMapping("/send-with-key")
    public ResponseEntity<Map<String, String>> sendWithKey(
            @RequestParam String topic,
            @RequestParam String key,
            @RequestParam String message) {
        
        kafkaProducerService.sendWithCustomKey(topic, key, message);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Message sent with custom key");
        response.put("topic", topic);
        response.put("key", key);
        response.put("content", message);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate batch message sending
     */
    @PostMapping("/batch/{topic}")
    public ResponseEntity<Map<String, String>> sendBatchMessages(
            @PathVariable String topic,
            @RequestParam(defaultValue = "10") int count) {
        
        kafkaProducerService.sendBatchMessages(topic, count);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Batch messages sent");
        response.put("topic", topic);
        response.put("count", String.valueOf(count));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Kafka cluster and topic information endpoint
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getKafkaInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // Topics used in this application
        Map<String, String> topics = new HashMap<>();
        topics.put("book-events", "Events related to book management (add, update, remove)");
        topics.put("borrow-events", "Events related to book borrowing");
        topics.put("partition-demo", "Topic used for partition and consumer group demonstrations");
        
        // Consumer groups
        Map<String, String> consumerGroups = new HashMap<>();
        consumerGroups.put("library-group", "Main consumer group for general processing");
        consumerGroups.put("library-group-1", "Analytics consumer group for data analysis");
        consumerGroups.put("library-group-2", "Notification consumer group for alerts and notifications");
        consumerGroups.put("library-group-record-listener", "Record-level consumer for detailed message inspection");
        
        info.put("topics", topics);
        info.put("consumerGroups", consumerGroups);
        info.put("features", new String[]{
            "Producer with acknowledgment callbacks",
            "Multiple consumer groups",
            "Partition-specific consumption",
            "Offset-specific consumption",
            "Manual acknowledgment",
            "Concurrent consumers",
            "Custom key-based partitioning",
            "Batch message processing"
        });
        
        return ResponseEntity.ok(info);
    }

    // Request DTOs
    public static class BookEventRequest {
        private String eventType;
        private String bookId;
        private String title;
        private String author;

        // Getters and setters
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        
        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
    }

    public static class BorrowEventRequest {
        private String eventType;
        private String bookTitle;
        private int borrowCount;
        private String userId;

        // Getters and setters
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        
        public int getBorrowCount() { return borrowCount; }
        public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}
