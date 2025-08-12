package com.example.kafka.consumer;

import com.example.kafka.model.BookEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ConsumerGroup2Service {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerGroup2Service.class);

    /**
     * Consumer Group 2 - Notification Processing
     * This consumer group focuses on sending notifications and alerts
     */
    @KafkaListener(
        topics = "book-events", 
        groupId = "library-group-2",
        containerFactory = "group2ListenerFactory"
    )
    public void consumeForNotifications(@Payload BookEvent event,
                                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                        @Header(KafkaHeaders.OFFSET) long offset,
                                        Acknowledgment acknowledgment) {
        
        logger.info("[NOTIFICATION-GROUP] Consumer Group: library-group-2, Partition: {}, Offset: {}, Event: {}", 
                partition, offset, event);
        
        try {
            // Simulate notification processing
            processForNotifications(event);
            
            // Simulate processing time
            Thread.sleep(100);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("[NOTIFICATION-GROUP] Error processing event: {}", e.getMessage());
        }
    }

    @KafkaListener(
        topics = "borrow-events", 
        groupId = "library-group-2",
        containerFactory = "group2ListenerFactory"
    )
    public void consumeBorrowForNotifications(@Payload Object event,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                              @Header(KafkaHeaders.OFFSET) long offset,
                                              Acknowledgment acknowledgment) {
        
        logger.info("[NOTIFICATION-GROUP] Borrow Event - Partition: {}, Offset: {}, Event: {}", 
                partition, offset, event);
        
        // Process borrow events for notifications
        acknowledgment.acknowledge();
    }

    /**
     * Demonstrating concurrent consumers within the same consumer group
     */
    @KafkaListener(
        topics = "partition-demo", 
        groupId = "library-group-2",
        containerFactory = "group2ListenerFactory",
        concurrency = "2"
    )
    public void consumePartitionDemo(@Payload String message,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     Acknowledgment acknowledgment) {
        
        logger.info("[NOTIFICATION-GROUP] Partition Demo - Thread: {}, Partition: {}, Offset: {}, Message: {}", 
                Thread.currentThread().getName(), partition, offset, message);
        
        acknowledgment.acknowledge();
    }

    private void processForNotifications(BookEvent event) {
        logger.info("[NOTIFICATIONS] Processing book event for notifications: {}", event.getTitle());
        
        // Simulate notification operations
        switch (event.getEventType()) {
            case "BOOK_ADDED":
                sendNotification("NEW_BOOK", "New book available: " + event.getTitle() + " by " + event.getAuthor());
                break;
            case "BOOK_UPDATED":
                sendNotification("BOOK_UPDATE", "Book updated: " + event.getTitle());
                break;
            case "BOOK_REMOVED":
                sendNotification("BOOK_REMOVED", "Book no longer available: " + event.getTitle());
                break;
        }
        
        logger.info("[NOTIFICATIONS] Notification processing completed for book: {}", event.getTitle());
    }

    private void sendNotification(String type, String message) {
        // Simulate sending notification (email, SMS, push notification, etc.)
        logger.info("[NOTIFICATIONS] Sending {} notification: {}", type, message);
        
        // Could integrate with email service, SMS service, push notification service, etc.
        try {
            Thread.sleep(50); // Simulate network call
            logger.info("[NOTIFICATIONS] {} notification sent successfully", type);
        } catch (InterruptedException e) {
            logger.error("[NOTIFICATIONS] Error sending notification: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
