package com.example.devdiarybackend.config;

import com.example.devdiarybackend.domain.Integration;
import com.example.devdiarybackend.repository.IntegrationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds default integrations to the database on startup (idempotent).
 */
@Configuration
public class SeedConfig {
    @Bean
    CommandLineRunner seedIntegrations(IntegrationRepository repo) {
        return args -> {
            saveIfMissing(repo, "github", "GitHub", "Pull commits, PRs, and issues");
            saveIfMissing(repo, "jira", "Jira", "Track tickets and sprints");
            saveIfMissing(repo, "slack", "Slack", "Bring channel events and reminders");
        };
    }

    private void saveIfMissing(IntegrationRepository repo, String key, String name, String desc) {
        repo.findByKey(key).orElseGet(() -> {
            Integration i = new Integration();
            i.setKey(key);
            i.setName(name);
            i.setDescription(desc);
            return repo.save(i);
        });
    }
}
