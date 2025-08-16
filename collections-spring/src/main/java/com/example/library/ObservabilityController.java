package com.example.library;

import com.example.config.ObservabilityMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/observability")
public class ObservabilityController {
    
    @Autowired
    private ObservabilityMetricsService observabilityMetricsService;
    
    @Autowired
    private LibraryService libraryService;

    /**
     * Get observability metrics summary showing the difference between 
     * persistent observability metrics vs business data metrics
     */
    @GetMapping("/metrics")
    public Map<String, Object> getObservabilityMetrics() {
        Map<String, Object> response = new HashMap<>();
        
        // Persistent observability metrics (meaningful for ops)
        Map<String, Object> observabilityMetrics = new HashMap<>();
        observabilityMetrics.put("duplicate_attempts", 
            observabilityMetricsService.getObservabilityCounterValue("library.books.duplicate.attempts.total"));
        observabilityMetrics.put("api_failures", 
            observabilityMetricsService.getObservabilityCounterValue("library.api.failures.total"));
        observabilityMetrics.put("validation_failures", 
            observabilityMetricsService.getObservabilityCounterValue("library.validation.failures.total"));
        observabilityMetrics.put("concurrent_conflicts", 
            observabilityMetricsService.getObservabilityCounterValue("library.concurrent.access.conflicts.total"));
        
        // Business data metrics (can be derived from database)
        Map<String, Object> businessMetrics = new HashMap<>();
        businessMetrics.put("total_books", libraryService.listAll().size());
        businessMetrics.put("total_borrow_count", libraryService.getTotalBorrowCount());
        
        response.put("observability_metrics", observabilityMetrics);
        response.put("business_metrics", businessMetrics);
        response.put("explanation", 
            "Observability metrics are persisted because they represent operational events that can't be " +
            "reconstructed from business data. Business metrics are NOT persisted because they can be " +
            "derived from the database at any time.");
        
        return response;
    }
    
    /**
     * Simulate some observability events for testing
     */
    @PostMapping("/simulate-events")
    public Map<String, String> simulateObservabilityEvents() {
        // Simulate various observability events
        observabilityMetricsService.recordApiFailure("book-lookup");
        observabilityMetricsService.recordValidationFailure("title-validation");
        observabilityMetricsService.recordConcurrentAccessConflict();
        
        return Map.of("message", "Simulated observability events recorded");
    }
}