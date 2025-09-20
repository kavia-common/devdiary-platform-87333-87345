package com.example.devdiarybackend.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_integrations", uniqueConstraints = @UniqueConstraint(name = "uniq_user_integration", columnNames = {"user_id", "integration_id"}))
public class UserIntegration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id")
    private Integration integration;

    @Column(length = 40)
    private String status; // connected, revoked, error, pending

    @Column(columnDefinition = "text")
    private String configJson; // encrypted/serialized token/config

    @Column(nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integration getIntegration() { return integration; }
    public void setIntegration(Integration integration) { this.integration = integration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
