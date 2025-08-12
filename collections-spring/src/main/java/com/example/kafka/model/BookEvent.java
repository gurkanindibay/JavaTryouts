package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookEvent {
    
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("bookId")
    private String bookId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("author")
    private String author;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    @JsonProperty("partition")
    private Integer partition;
    
    @JsonProperty("offset")
    private Long offset;

    public BookEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public BookEvent(String eventType, String bookId, String title, String author) {
        this();
        this.eventType = eventType;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "BookEvent{" +
                "eventType='" + eventType + '\'' +
                ", bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                ", partition=" + partition +
                ", offset=" + offset +
                '}';
    }
}
