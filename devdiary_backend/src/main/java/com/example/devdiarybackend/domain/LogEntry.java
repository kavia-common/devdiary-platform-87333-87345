package com.example.devdiarybackend.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "log_entries", indexes = {
        @Index(name = "idx_logentry_user_created", columnList = "user_id,created_at")
})
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional reference to a log/day
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private Log log;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(length = 100)
    private String category; // e.g., coding, meeting, review, bugfix

    @Column(length = 200)
    private String tags; // comma-separated for simplicity

    @Column(nullable = false, columnDefinition = "timestamp with time zone", name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public Log getLog() { return log; }
    public void setLog(Log log) { this.log = log; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
