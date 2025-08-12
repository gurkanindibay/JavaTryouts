package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BorrowEvent {
    
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("bookTitle")
    private String bookTitle;
    
    @JsonProperty("borrowCount")
    private int borrowCount;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    @JsonProperty("userId")
    private String userId;

    public BorrowEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public BorrowEvent(String eventType, String bookTitle, int borrowCount, String userId) {
        this();
        this.eventType = eventType;
        this.bookTitle = bookTitle;
        this.borrowCount = borrowCount;
        this.userId = userId;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "BorrowEvent{" +
                "eventType='" + eventType + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", borrowCount=" + borrowCount +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                '}';
    }
}
