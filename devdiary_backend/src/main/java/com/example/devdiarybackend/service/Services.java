package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.*;
import com.example.devdiarybackend.repository.*;
import com.example.devdiarybackend.security.JwtUtilProvider;
import com.example.devdiarybackend.web.dto.*;
import io.jsonwebtoken.Claims;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Service
class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    UserDetailsServiceImpl(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPasswordHash())
                .roles("USER")
                .disabled(!u.isActive())
                .build();
    }
}

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtilProvider jwtUtilProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, JwtUtilProvider jwtUtilProvider) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtilProvider = jwtUtilProvider;
    }

    // PUBLIC_INTERFACE
    public User register(RegisterRequest req) {
        userRepository.findByEmail(req.email()).ifPresent(u -> { throw new IllegalArgumentException("Email exists"); });
        User u = new User();
        u.setEmail(req.email().toLowerCase(Locale.ROOT).trim());
        u.setDisplayName(req.displayName());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setActive(true);
        return userRepository.save(u);
    }

    // PUBLIC_INTERFACE
    public String login(LoginRequest req) {
        User u = userRepository.findByEmail(req.email()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        Map<String, Object> claims = Map.of("uid", u.getId(), "roles", List.of("USER"), "name", u.getDisplayName());
        return jwtUtilProvider.get().generateToken(u.getEmail(), claims);
    }

    public Optional<User> currentUserFromToken(String token) {
        try {
            Claims c = jwtUtilProvider.get().parse(token);
            String email = c.getSubject();
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    // PUBLIC_INTERFACE
    public UserProfileResponse getProfile(String email) {
        User u = userRepository.findByEmail(email).orElseThrow();
        return new UserProfileResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.isActive());
    }
}

@Service
public class LogEntryService {
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;

    public LogEntryService(LogEntryRepository logEntryRepository, UserRepository userRepository) {
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
    }

    // PUBLIC_INTERFACE
    public LogEntryResponse create(String email, CreateLogEntryRequest req) {
        User u = userRepository.findByEmail(email).orElseThrow();
        LogEntry entry = new LogEntry();
        entry.setUser(u);
        entry.setContent(req.content());
        entry.setCategory(req.category());
        entry.setTags(req.tags());
        entry.setCreatedAt(OffsetDateTime.now());
        LogEntry saved = logEntryRepository.save(entry);
        return new LogEntryResponse(saved.getId(), saved.getContent(), saved.getCategory(), saved.getTags(), saved.getCreatedAt());
    }

    // PUBLIC_INTERFACE
    public PagedResponse<LogEntryResponse> listMine(String email, int page, int size) {
        User u = userRepository.findByEmail(email).orElseThrow();
        Page<LogEntry> p = logEntryRepository.findByUserIdOrderByCreatedAtDesc(u.getId(), PageRequest.of(page, size));
        List<LogEntryResponse> items = p.getContent().stream()
                .map(e -> new LogEntryResponse(e.getId(), e.getContent(), e.getCategory(), e.getTags(), e.getCreatedAt()))
                .toList();
        return new PagedResponse<>(items, page, size, p.getTotalElements(), p.getTotalPages());
    }
}

@Service
public class SummaryService {
    private final SummaryRepository summaryRepository;
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;

    public SummaryService(SummaryRepository summaryRepository, LogEntryRepository logEntryRepository, UserRepository userRepository) {
        this.summaryRepository = summaryRepository;
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
    }

    // PUBLIC_INTERFACE
    public SummaryResponse generateOrGet(String email, LocalDate date) {
        User u = userRepository.findByEmail(email).orElseThrow();
        return summaryRepository.findByUserIdAndSummaryDate(u.getId(), date)
                .map(s -> new SummaryResponse(date, s.getContent()))
                .orElseGet(() -> {
                    // Stub NLP-based summary: concatenate categories and counts
                    OffsetDateTime start = date.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
                    OffsetDateTime end = start.plusDays(1);
                    var entries = logEntryRepository.findByUserIdAndCreatedAtBetween(u.getId(), start, end);
                    Map<String, Long> counts = new HashMap<>();
                    for (LogEntry e : entries) {
                        String key = Optional.ofNullable(e.getCategory()).orElse("general");
                        counts.put(key, counts.getOrDefault(key, 0L) + 1);
                    }
                    StringBuilder sb = new StringBuilder("Daily Summary: ");
                    if (entries.isEmpty()) sb.append("No entries.");
                    else counts.forEach((k, v) -> sb.append(k).append("=").append(v).append(" "));
                    Summary s = new Summary();
                    s.setUser(u);
                    s.setSummaryDate(date);
                    s.setContent(sb.toString().trim());
                    summaryRepository.save(s);
                    return new SummaryResponse(date, s.getContent());
                });
    }
}

@Service
public class IntegrationService {
    private final IntegrationRepository integrationRepository;
    private final UserIntegrationRepository userIntegrationRepository;
    private final UserRepository userRepository;

    public IntegrationService(IntegrationRepository integrationRepository, UserIntegrationRepository userIntegrationRepository, UserRepository userRepository) {
        this.integrationRepository = integrationRepository;
        this.userIntegrationRepository = userIntegrationRepository;
        this.userRepository = userRepository;
    }

    // PUBLIC_INTERFACE
    public List<IntegrationResponse> listAll() {
        return integrationRepository.findAll().stream()
                .map(i -> new IntegrationResponse(i.getId(), i.getKey(), i.getName(), i.getDescription()))
                .toList();
    }

    // PUBLIC_INTERFACE
    public void connect(String email, ConnectIntegrationRequest req) {
        User u = userRepository.findByEmail(email).orElseThrow();
        Integration i = integrationRepository.findByKey(req.key()).orElseThrow();
        UserIntegration ui = userIntegrationRepository.findByUserIdAndIntegrationId(u.getId(), i.getId()).orElseGet(UserIntegration::new);
        ui.setUser(u);
        ui.setIntegration(i);
        ui.setStatus("connected");
        ui.setConfigJson(req.configJson());
        userIntegrationRepository.save(ui);
    }
}

@Service
public class ActivityService {
    private final ActivityEventRepository activityEventRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityEventRepository activityEventRepository, UserRepository userRepository) {
        this.activityEventRepository = activityEventRepository;
        this.userRepository = userRepository;
    }

    // PUBLIC_INTERFACE
    public PagedResponse<ActivityEventResponse> feed(String email, int page, int size) {
        User u = userRepository.findByEmail(email).orElseThrow();
        Page<ActivityEvent> p = activityEventRepository.findByUserIdOrderByOccurredAtDesc(u.getId(), PageRequest.of(page, size));
        List<ActivityEventResponse> items = p.getContent().stream()
                .map(a -> new ActivityEventResponse(a.getId(), a.getType(), a.getDetails(), a.getOccurredAt()))
                .toList();
        return new PagedResponse<>(items, page, size, p.getTotalElements(), p.getTotalPages());
    }
}

@Service
public class AnalyticsService {
    private final AnalyticsDailyRepository analyticsDailyRepository;
    private final UserRepository userRepository;

    public AnalyticsService(AnalyticsDailyRepository analyticsDailyRepository, UserRepository userRepository) {
        this.analyticsDailyRepository = analyticsDailyRepository;
        this.userRepository = userRepository;
    }

    // PUBLIC_INTERFACE
    public List<AnalyticsDailyResponse> range(String email, LocalDate start, LocalDate end) {
        User u = userRepository.findByEmail(email).orElseThrow();
        return analyticsDailyRepository.findByUserIdAndDateBetween(u.getId(), start, end)
                .stream()
                .map(a -> new AnalyticsDailyResponse(a.getDate(), a.getEntriesCount(), a.getCommits(), a.getPrs(), a.getMeetings()))
                .toList();
    }
}
