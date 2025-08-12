package com.example.kafka.consumer;

import com.example.kafka.model.BookEvent;
import com.example.kafka.model.BorrowEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Consumer for book events - Default consumer group
     */
    @KafkaListener(topics = "book-events", groupId = "library-group")
    public void consumeBookEvents(@Payload BookEvent event,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                  Acknowledgment acknowledgment) {
        
        logger.info("Consumed book event from partition {}, offset {}: {}", partition, offset, event);
        
        // Simulate processing
        try {
            Thread.sleep(100);
            
            // Update event with partition and offset info
            event.setPartition(partition);
            event.setOffset(offset);
            
            // Process the event (could save to database, trigger other operations, etc.)
            processBookEvent(event);
            
            // Manual acknowledgment
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing book event: {}", e.getMessage());
            // In real scenarios, you might want to implement retry logic or send to DLQ
        }
    }

    /**
     * Consumer for borrow events - Default consumer group
     */
    @KafkaListener(topics = "borrow-events", groupId = "library-group")
    public void consumeBorrowEvents(@Payload BorrowEvent event,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                    @Header(KafkaHeaders.OFFSET) long offset,
                                    Acknowledgment acknowledgment) {
        
        logger.info("Consumed borrow event from partition {}, offset {}: {}", partition, offset, event);
        
        try {
            // Simulate processing
            Thread.sleep(50);
            
            // Process the event
            processBorrowEvent(event);
            
            // Manual acknowledgment
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing borrow event: {}", e.getMessage());
        }
    }

    /**
     * Consumer for partition demo - listens to specific partitions
     */
    @KafkaListener(
        topicPartitions = @TopicPartition(
            topic = "partition-demo",
            partitions = {"0", "1"}
        ),
        groupId = "library-group"
    )
    public void consumeFromSpecificPartitions(@Payload String message,
                                              @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                              @Header(KafkaHeaders.OFFSET) long offset,
                                              Acknowledgment acknowledgment) {
        
        logger.info("Consumed from specific partition {}, offset {}: {}", partition, offset, message);
        acknowledgment.acknowledge();
    }

    /**
     * Consumer that listens from a specific offset
     */
    @KafkaListener(
        topicPartitions = @TopicPartition(
            topic = "partition-demo",
            partitionOffsets = @PartitionOffset(partition = "2", initialOffset = "0")
        ),
        groupId = "library-group"
    )
    public void consumeFromSpecificOffset(@Payload String message,
                                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                          @Header(KafkaHeaders.OFFSET) long offset,
                                          Acknowledgment acknowledgment) {
        
        logger.info("Consumed from specific offset - partition {}, offset {}: {}", partition, offset, message);
        acknowledgment.acknowledge();
    }

    /**
     * Generic consumer to demonstrate ConsumerRecord usage
     */
    @KafkaListener(topics = "book-events", groupId = "library-group-record-listener")
    public void consumeBookEventsAsRecord(ConsumerRecord<String, BookEvent> record,
                                          Acknowledgment acknowledgment) {
        
        logger.info("Consumed ConsumerRecord - Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}, Timestamp: {}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value(),
                record.timestamp());
        
        acknowledgment.acknowledge();
    }

    private void processBookEvent(BookEvent event) {
        // Simulate business logic processing
        logger.info("Processing book event: {} - {}", event.getEventType(), event.getTitle());
        
        switch (event.getEventType()) {
            case "BOOK_ADDED":
                logger.info("Book added to catalog: {}", event.getTitle());
                break;
            case "BOOK_UPDATED":
                logger.info("Book updated in catalog: {}", event.getTitle());
                break;
            case "BOOK_REMOVED":
                logger.info("Book removed from catalog: {}", event.getTitle());
                break;
            default:
                logger.warn("Unknown book event type: {}", event.getEventType());
        }
    }

    private void processBorrowEvent(BorrowEvent event) {
        // Simulate business logic processing
        logger.info("Processing borrow event: {} - {} (Count: {})", 
                event.getEventType(), event.getBookTitle(), event.getBorrowCount());
        
        if ("BOOK_BORROWED".equals(event.getEventType())) {
            logger.info("Book '{}' has been borrowed {} times", event.getBookTitle(), event.getBorrowCount());
            
            // Could trigger notifications, update statistics, etc.
            if (event.getBorrowCount() > 10) {
                logger.info("Popular book alert: '{}' has been borrowed {} times!", 
                        event.getBookTitle(), event.getBorrowCount());
            }
        }
    }
}
