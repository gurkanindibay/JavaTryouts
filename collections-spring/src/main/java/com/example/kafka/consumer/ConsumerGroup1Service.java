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
public class ConsumerGroup1Service {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerGroup1Service.class);

    /**
     * Consumer Group 1 - Analytics Processing
     * This consumer group focuses on analytics and reporting
     */
    @KafkaListener(
        topics = "book-events", 
        groupId = "library-group-1",
        containerFactory = "group1ListenerFactory"
    )
    public void consumeForAnalytics(@Payload BookEvent event,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                    @Header(KafkaHeaders.OFFSET) long offset,
                                    Acknowledgment acknowledgment) {
        
        logger.info("[ANALYTICS-GROUP] Consumer Group: library-group-1, Partition: {}, Offset: {}, Event: {}", 
                partition, offset, event);
        
        try {
            // Simulate analytics processing
            processForAnalytics(event);
            
            // Simulate processing time
            Thread.sleep(150);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("[ANALYTICS-GROUP] Error processing event: {}", e.getMessage());
        }
    }

    @KafkaListener(
        topics = "borrow-events", 
        groupId = "library-group-1",
        containerFactory = "group1ListenerFactory"
    )
    public void consumeBorrowForAnalytics(@Payload Object event,
                                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                          @Header(KafkaHeaders.OFFSET) long offset,
                                          Acknowledgment acknowledgment) {
        
        logger.info("[ANALYTICS-GROUP] Borrow Event - Partition: {}, Offset: {}, Event: {}", 
                partition, offset, event);
        
        // Process borrow events for analytics
        acknowledgment.acknowledge();
    }

    private void processForAnalytics(BookEvent event) {
        logger.info("[ANALYTICS] Processing book event for analytics: {}", event.getTitle());
        
        // Simulate analytics operations
        switch (event.getEventType()) {
            case "BOOK_ADDED":
                logger.info("[ANALYTICS] Recording new book addition for trending analysis");
                break;
            case "BOOK_UPDATED":
                logger.info("[ANALYTICS] Recording book update for change tracking");
                break;
            case "BOOK_REMOVED":
                logger.info("[ANALYTICS] Recording book removal for inventory analysis");
                break;
        }
        
        // Could update analytics database, send to data warehouse, etc.
        logger.info("[ANALYTICS] Analytics processing completed for book: {}", event.getTitle());
    }
}
