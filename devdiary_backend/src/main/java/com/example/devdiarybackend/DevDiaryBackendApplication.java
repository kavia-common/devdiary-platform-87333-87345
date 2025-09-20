package com.example.devdiarybackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "DevDiary Backend API",
                version = "0.1.0",
                description = "APIs for logs, summaries, users, integrations, activity feed, and analytics.",
                contact = @Contact(name = "DevDiary", email = "support@devdiary.app")
        ),
        tags = {
                @Tag(name = "Auth", description = "Authentication and JWT issuance"),
                @Tag(name = "Users", description = "User management"),
                @Tag(name = "Log Entries", description = "Diary log entries"),
                @Tag(name = "Summaries", description = "Stand-up summaries"),
                @Tag(name = "Integrations", description = "Integration catalog and user connections"),
                @Tag(name = "Activity", description = "Activity feed"),
                @Tag(name = "Analytics", description = "Dashboard analytics")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class DevDiaryBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevDiaryBackendApplication.class, args);
    }
}
