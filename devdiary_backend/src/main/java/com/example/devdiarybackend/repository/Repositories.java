package com.example.devdiarybackend.repository;

import com.example.devdiarybackend.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {}

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByUserIdAndLogDate(Long userId, LocalDate logDate);
}

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<LogEntry> findByUserIdAndCreatedAtBetween(Long userId, OffsetDateTime start, OffsetDateTime end);
}

public interface IntegrationRepository extends JpaRepository<Integration, Long> {
    Optional<Integration> findByKey(String key);
}

public interface UserIntegrationRepository extends JpaRepository<UserIntegration, Long> {
    List<UserIntegration> findByUserId(Long userId);
    Optional<UserIntegration> findByUserIdAndIntegrationId(Long userId, Long integrationId);
}

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, Long> {
    Page<ActivityEvent> findByUserIdOrderByOccurredAtDesc(Long userId, Pageable pageable);
}

public interface AnalyticsDailyRepository extends JpaRepository<AnalyticsDaily, Long> {
    List<AnalyticsDaily> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findByUserIdAndSummaryDate(Long userId, LocalDate date);
    List<Summary> findByUserIdOrderBySummaryDateDesc(Long userId);
}

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByPrefixAndActiveTrue(String prefix);
}
