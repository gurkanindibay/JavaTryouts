package com.example.kafka.producer;

import com.example.kafka.model.BookEvent;
import com.example.kafka.model.BorrowEvent;
import com.example.kafka.model.ObservabilityMetricEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public static final String BOOK_EVENTS_TOPIC = "book-events";
    public static final String BORROW_EVENTS_TOPIC = "borrow-events";
    public static final String PARTITION_DEMO_TOPIC = "partition-demo";
    public static final String OBSERVABILITY_METRICS_TOPIC = "observability-metrics";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send book event to Kafka topic
     */
    public void sendBookEvent(BookEvent event) {
        logger.info("Sending book event: {}", event);
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(BOOK_EVENTS_TOPIC, event.getBookId(), event);
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Sent book event=[{}] with offset=[{}] to partition=[{}]", 
                    event, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            } else {
                logger.error("Unable to send book event=[{}] due to: {}", event, exception.getMessage());
            }
        });
    }

    /**
     * Send borrow event to Kafka topic
     */
    public void sendBorrowEvent(BorrowEvent event) {
        logger.info("Sending borrow event: {}", event);
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(BORROW_EVENTS_TOPIC, event.getBookTitle(), event);
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Sent borrow event=[{}] with offset=[{}] to partition=[{}]", 
                    event, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            } else {
                logger.error("Unable to send borrow event=[{}] due to: {}", event, exception.getMessage());
            }
        });
    }

    /**
     * Send message to specific partition for demonstration
     */
    public void sendToSpecificPartition(String message, int partition) {
        logger.info("Sending message to partition {}: {}", partition, message);
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(PARTITION_DEMO_TOPIC, partition, "partition-key-" + partition, message);
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Sent message=[{}] to partition=[{}] with offset=[{}]", 
                    message, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] to partition=[{}] due to: {}", 
                    message, partition, exception.getMessage());
            }
        });
    }

    /**
     * Send message with custom key for partition assignment
     */
    public void sendWithCustomKey(String topic, String key, Object message) {
        logger.info("Sending message with key '{}' to topic '{}': {}", key, topic, message);
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Sent message=[{}] with key=[{}] to partition=[{}] with offset=[{}]", 
                    message, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] with key=[{}] due to: {}", 
                    message, key, exception.getMessage());
            }
        });
    }

    /**
     * Demonstrate batch sending
     */
    public void sendBatchMessages(String topic, int count) {
        logger.info("Sending {} batch messages to topic: {}", count, topic);
        
        for (int i = 0; i < count; i++) {
            String message = "Batch message " + i;
            String key = "batch-key-" + (i % 3); // Distribute across 3 different keys
            sendWithCustomKey(topic, key, message);
        }
    }

    /**
     * Send observability metric event to Kafka topic for async processing
     */
    public void sendObservabilityMetricEvent(ObservabilityMetricEvent event) {
        logger.debug("Sending observability metric event: {}", event);
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(OBSERVABILITY_METRICS_TOPIC, event.getMetricName(), event);
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.debug("Sent observability metric event=[{}] with offset=[{}] to partition=[{}]", 
                    event.getMetricName(), result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            } else {
                logger.error("Unable to send observability metric event=[{}] due to: {}", event, exception.getMessage());
            }
        });
    }
}
