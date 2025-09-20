package com.example.devdiarybackend.integrations;

/**
 * Adapter interface for external integrations (e.g., GitHub, Jira, Slack).
 * Implementations should handle OAuth/config, event fetching, and data mapping.
 */
public interface IntegrationAdapter {
    // PUBLIC_INTERFACE
    String getKey(); // e.g., "github"

    // PUBLIC_INTERFACE
    void connect(String configJson); // store credentials/perform handshake

    // PUBLIC_INTERFACE
    void disconnect();

    // PUBLIC_INTERFACE
    void sync(); // pull latest activity and push to activity_events, analytics, etc.
}
