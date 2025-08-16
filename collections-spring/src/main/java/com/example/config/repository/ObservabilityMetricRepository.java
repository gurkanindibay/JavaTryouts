package com.example.config.repository;

import com.example.config.entity.ObservabilityMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for ObservabilityMetricEntity
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
}