package com.example.devdiarybackend.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a platform user.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false, length = 120)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserSettings settings;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<ApiKey> apiKeys = new HashSet<>();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public UserSettings getSettings() { return settings; }
    public void setSettings(UserSettings settings) { this.settings = settings; }
    public Set<ApiKey> getApiKeys() { return apiKeys; }
    public void setApiKeys(Set<ApiKey> apiKeys) { this.apiKeys = apiKeys; }
}
