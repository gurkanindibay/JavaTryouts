package com.example.kafka.consumer;

import com.example.config.entity.ObservabilityMetricEntity;
import com.example.config.repository.ObservabilityMetricRepository;
import com.example.kafka.model.ObservabilityMetricEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ObservabilityMetricsConsumer
 * Tests the persistence logic and error handling
 */
class ObservabilityMetricsConsumerTest {

    @Mock
    private ObservabilityMetricRepository observabilityMetricRepository;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private ObservabilityMetricsConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIncrementMetric_NewMetric() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent(
            "test.metric", 1L, "COUNTER", "INCREMENT", "Test metric"
        );
        
        when(observabilityMetricRepository.findByMetricName("test.metric"))
            .thenReturn(Optional.empty());
        
        ObservabilityMetricEntity savedEntity = new ObservabilityMetricEntity(
            "test.metric", 1L, "COUNTER", "Test metric"
        );
        savedEntity.setVersion(1L);
        
        when(observabilityMetricRepository.save(any(ObservabilityMetricEntity.class)))
            .thenReturn(savedEntity);
        
        when(observabilityMetricRepository.count()).thenReturn(1L);

        // When
        consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);

        // Then
        verify(observabilityMetricRepository).save(argThat(entity -> 
            entity.getMetricName().equals("test.metric") &&
            entity.getMetricValue().equals(1L) &&
            entity.getLastUpdated() != null
        ));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void testIncrementMetric_ExistingMetric() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent(
            "test.metric", 1L, "COUNTER", "INCREMENT", "Test metric"
        );
        
        ObservabilityMetricEntity existingEntity = new ObservabilityMetricEntity(
            "test.metric", 5L, "COUNTER", "Test metric"
        );
        existingEntity.setVersion(1L);
        existingEntity.setLastUpdated(LocalDateTime.now().minusHours(1));
        
        when(observabilityMetricRepository.findByMetricName("test.metric"))
            .thenReturn(Optional.of(existingEntity));
        
        ObservabilityMetricEntity savedEntity = new ObservabilityMetricEntity(
            "test.metric", 6L, "COUNTER", "Test metric"
        );
        savedEntity.setVersion(2L);
        
        when(observabilityMetricRepository.save(any(ObservabilityMetricEntity.class)))
            .thenReturn(savedEntity);
        
        when(observabilityMetricRepository.count()).thenReturn(1L);

        // When
        consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);

        // Then
        verify(observabilityMetricRepository).save(argThat(entity -> 
            entity.getMetricName().equals("test.metric") &&
            entity.getMetricValue().equals(6L) &&
            entity.getLastUpdated().isAfter(existingEntity.getLastUpdated())
        ));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void testIncrementMetric_OptimisticLockingFailure() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent(
            "test.metric", 1L, "COUNTER", "INCREMENT", "Test metric"
        );
        
        ObservabilityMetricEntity existingEntity = new ObservabilityMetricEntity(
            "test.metric", 5L, "COUNTER", "Test metric"
        );
        existingEntity.setVersion(1L);
        
        when(observabilityMetricRepository.findByMetricName("test.metric"))
            .thenReturn(Optional.of(existingEntity));
        
        when(observabilityMetricRepository.count()).thenReturn(1L);
        
        // First save attempt fails with optimistic locking
        when(observabilityMetricRepository.save(any(ObservabilityMetricEntity.class)))
            .thenThrow(new ObjectOptimisticLockingFailureException("Version conflict", null))
            .thenReturn(existingEntity); // Second attempt succeeds

        // When
        consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);

        // Then
        verify(observabilityMetricRepository, times(2)).findByMetricName("test.metric");
        verify(observabilityMetricRepository, times(2)).save(any(ObservabilityMetricEntity.class));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void testSetMetric() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent(
            "test.metric", 100L, "GAUGE", "SET", "Test metric"
        );
        
        when(observabilityMetricRepository.findByMetricName("test.metric"))
            .thenReturn(Optional.empty());
        
        ObservabilityMetricEntity savedEntity = new ObservabilityMetricEntity(
            "test.metric", 100L, "GAUGE", "Test metric"
        );
        savedEntity.setVersion(1L);
        
        when(observabilityMetricRepository.save(any(ObservabilityMetricEntity.class)))
            .thenReturn(savedEntity);
        
        when(observabilityMetricRepository.count()).thenReturn(1L);

        // When
        consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);

        // Then
        verify(observabilityMetricRepository).save(argThat(entity -> 
            entity.getMetricName().equals("test.metric") &&
            entity.getMetricValue().equals(100L) &&
            entity.getLastUpdated() != null
        ));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void testInvalidEvent_NullMetricName() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent();
        event.setMetricName(null);
        event.setOperation("INCREMENT");
        event.setMetricValue(1L);
        
        when(observabilityMetricRepository.count()).thenReturn(1L);

        // When
        consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);

        // Then
        verify(observabilityMetricRepository, never()).save(any());
        verify(acknowledgment).acknowledge(); // Invalid events are acknowledged to skip them
    }

    @Test
    void testDatabaseUnhealthy() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent(
            "test.metric", 1L, "COUNTER", "INCREMENT", "Test metric"
        );
        
        when(observabilityMetricRepository.count())
            .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);
        });
        
        verify(observabilityMetricRepository, never()).save(any());
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void testPersistenceFailure() {
        // Given
        ObservabilityMetricEvent event = new ObservabilityMetricEvent(
            "test.metric", 1L, "COUNTER", "INCREMENT", "Test metric"
        );
        
        when(observabilityMetricRepository.count()).thenReturn(1L);
        when(observabilityMetricRepository.findByMetricName("test.metric"))
            .thenReturn(Optional.empty());
        when(observabilityMetricRepository.save(any(ObservabilityMetricEntity.class)))
            .thenThrow(new RuntimeException("Database save failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            consumer.consumeObservabilityMetricEvent(event, 0, 0L, acknowledgment);
        });
        
        verify(acknowledgment, never()).acknowledge();
    }
}