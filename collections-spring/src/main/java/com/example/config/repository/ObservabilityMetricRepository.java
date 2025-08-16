package com.example.config.repository;

import com.example.config.entity.ObservabilityMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for ObservabilityMetricEntity
 * Enhanced with atomic operations for better concurrency handling
 */
@Repository
public interface ObservabilityMetricRepository extends JpaRepository<ObservabilityMetricEntity, String> {
    
    /**
     * Find metric by name
     */
    Optional<ObservabilityMetricEntity> findByMetricName(String metricName);
    
    /**
     * Find all metrics by type
     */
    List<ObservabilityMetricEntity> findByMetricType(String metricType);
    
    /**
     * Find all counter metrics
     */
    @Query("SELECT m FROM ObservabilityMetricEntity m WHERE m.metricType = 'COUNTER' ORDER BY m.metricName")
    List<ObservabilityMetricEntity> findAllCounters();
    
    /**
     * Check if metric exists
     */
    boolean existsByMetricName(String metricName);
    
    /**
     * Atomic increment operation for better concurrency
     * This prevents race conditions by doing the increment at the database level
     */
    @Modifying
    @Query("UPDATE ObservabilityMetricEntity m SET m.metricValue = m.metricValue + 1, m.lastUpdated = CURRENT_TIMESTAMP WHERE m.metricName = :metricName")
    int incrementMetricValue(@Param("metricName") String metricName);
    
    /**
     * Atomic set operation with timestamp update
     */
    @Modifying
    @Query("UPDATE ObservabilityMetricEntity m SET m.metricValue = :value, m.lastUpdated = CURRENT_TIMESTAMP WHERE m.metricName = :metricName")
    int setMetricValue(@Param("metricName") String metricName, @Param("value") Long value);
    
    /**
     * Atomic reset operation with timestamp update
     */
    @Modifying
    @Query("UPDATE ObservabilityMetricEntity m SET m.metricValue = 0, m.lastUpdated = CURRENT_TIMESTAMP WHERE m.metricName = :metricName")
    int resetMetricValue(@Param("metricName") String metricName);
}