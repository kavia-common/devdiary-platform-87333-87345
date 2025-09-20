package com.example.devdiarybackend.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

// PUBLIC_INTERFACE
public record RegisterRequest(
        @Schema(description = "Email address") String email,
        @Schema(description = "Display name") String displayName,
        @Schema(description = "Plain password") String password
) {}

// PUBLIC_INTERFACE
public record LoginRequest(
        @Schema(description = "Email address") String email,
        @Schema(description = "Plain password") String password
) {}

// PUBLIC_INTERFACE
public record TokenResponse(
        @Schema(description = "JWT token") String token
) {}

// PUBLIC_INTERFACE
public record UserProfileResponse(
        Long id, String email, String displayName, boolean active
) {}

// PUBLIC_INTERFACE
public record CreateLogEntryRequest(
        @Schema(description = "Text content") String content,
        @Schema(description = "Category label") String category,
        @Schema(description = "CSV tags") String tags
) {}

// PUBLIC_INTERFACE
public record LogEntryResponse(
        Long id, String content, String category, String tags, OffsetDateTime createdAt
) {}

// PUBLIC_INTERFACE
public record SummaryRequest(
        @Schema(description = "Summary date") LocalDate date
) {}

// PUBLIC_INTERFACE
public record SummaryResponse(
        LocalDate date, String content
) {}

// PUBLIC_INTERFACE
public record IntegrationResponse(
        Long id, String key, String name, String description
) {}

// PUBLIC_INTERFACE
public record ConnectIntegrationRequest(
        String key, String configJson
) {}

// PUBLIC_INTERFACE
public record ActivityEventResponse(
        Long id, String type, String details, OffsetDateTime occurredAt
) {}

// PUBLIC_INTERFACE
public record AnalyticsDailyResponse(
        LocalDate date, int entriesCount, int commits, int prs, int meetings
) {}

// PUBLIC_INTERFACE
public record PagedResponse<T>(
        List<T> items, int page, int size, long totalElements, int totalPages
) {}
