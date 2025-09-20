package com.example.devdiarybackend.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "activity_events", indexes = {
        @Index(name = "idx_activity_user_time", columnList = "user_id,occurred_at")
})
public class ActivityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 60, nullable = false)
    private String type; // commit, pr_opened, ticket_moved, etc.

    @Column(columnDefinition = "text")
    private String details;

    @Column(name = "occurred_at", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime occurredAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}
