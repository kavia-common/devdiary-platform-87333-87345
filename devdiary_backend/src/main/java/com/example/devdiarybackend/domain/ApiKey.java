package com.example.devdiarybackend.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "api_keys", indexes = {
        @Index(name = "idx_apikey_user_prefix", columnList = "user_id,prefix", unique = true)
})
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Not storing raw key, store hashed and a short prefix for lookup UX
    @Column(nullable = false, unique = true, length = 80)
    private String hash;

    @Column(nullable = false, length = 12)
    private String prefix;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
