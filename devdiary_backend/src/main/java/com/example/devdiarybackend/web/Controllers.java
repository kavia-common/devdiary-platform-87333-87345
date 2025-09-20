package com.example.devdiarybackend.web;

import com.example.devdiarybackend.service.*;
import com.example.devdiarybackend.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 PUBLIC_INTERFACE
 AuthController: Manages user registration and login to obtain JWT tokens.
*/
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Creates a user account.")
    public ResponseEntity<UserProfileResponse> register(@RequestBody RegisterRequest req) {
        var user = authService.register(req);
        return ResponseEntity.ok(new UserProfileResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.isActive()));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates and returns JWT.")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}

/**
 PUBLIC_INTERFACE
 UserController: Provides current user profile and info.
*/
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
class UserController {
    private final UserService userService;

    UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    @Operation(summary = "My profile", description = "Returns current user profile.")
    public ResponseEntity<UserProfileResponse> me(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth.getName()));
    }
}

/**
 PUBLIC_INTERFACE
 LogEntryController: CRUD-lite endpoints for log entries.
*/
@RestController
@RequestMapping("/api/log-entries")
@Tag(name = "Log Entries")
class LogEntryController {
    private final LogEntryService logEntryService;

    LogEntryController(LogEntryService logEntryService) { this.logEntryService = logEntryService; }

    @PostMapping
    @Operation(summary = "Create log entry", description = "Creates a new log entry for the current user.")
    public ResponseEntity<LogEntryResponse> create(Authentication auth, @RequestBody CreateLogEntryRequest req) {
        return ResponseEntity.ok(logEntryService.create(auth.getName(), req));
    }

    @GetMapping
    @Operation(summary = "List my log entries", description = "Returns paginated log entries for the current user.")
    public ResponseEntity<PagedResponse<LogEntryResponse>> list(Authentication auth,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logEntryService.listMine(auth.getName(), page, size));
    }
}

/**
 PUBLIC_INTERFACE
 SummaryController: Stand-up summaries based on entries.
*/
@RestController
@RequestMapping("/api/summaries")
@Tag(name = "Summaries")
class SummaryController {
    private final SummaryService summaryService;

    SummaryController(SummaryService summaryService) { this.summaryService = summaryService; }

    @PostMapping
    @Operation(summary = "Generate/Get summary", description = "Returns NLP-based stand-up summary (stubbed).")
    public ResponseEntity<SummaryResponse> get(Authentication auth, @RequestBody SummaryRequest req) {
        return ResponseEntity.ok(summaryService.generateOrGet(auth.getName(), req.date()));
    }
}

/**
 PUBLIC_INTERFACE
 IntegrationController: Lists available integrations and connects a user to an integration.
*/
@RestController
@RequestMapping("/api/integrations")
@Tag(name = "Integrations")
class IntegrationController {
    private final IntegrationService integrationService;

    IntegrationController(IntegrationService integrationService) { this.integrationService = integrationService; }

    @GetMapping
    @Operation(summary = "List integrations", description = "Returns catalog of available integrations.")
    public ResponseEntity<List<IntegrationResponse>> list() {
        return ResponseEntity.ok(integrationService.listAll());
    }

    @PostMapping("/connect")
    @Operation(summary = "Connect integration", description = "Associates current user with an integration (adapter-stub).")
    public ResponseEntity<Void> connect(Authentication auth, @RequestBody ConnectIntegrationRequest req) {
        integrationService.connect(auth.getName(), req);
        return ResponseEntity.ok().build();
    }
}

/**
 PUBLIC_INTERFACE
 ActivityController: Passive feed.
*/
@RestController
@RequestMapping("/api/activity")
@Tag(name = "Activity")
class ActivityController {
    private final ActivityService activityService;

    ActivityController(ActivityService activityService) { this.activityService = activityService; }

    @GetMapping
    @Operation(summary = "Activity feed", description = "Returns a paginated activity feed.")
    public ResponseEntity<PagedResponse<ActivityEventResponse>> feed(Authentication auth,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(activityService.feed(auth.getName(), page, size));
    }
}

/**
 PUBLIC_INTERFACE
 AnalyticsController: Dashboard analytics and trends.
*/
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics")
class AnalyticsController {
    private final AnalyticsService analyticsService;

    AnalyticsController(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }

    @GetMapping("/daily")
    @Operation(summary = "Daily analytics", description = "Returns daily analytics records for a date range.")
    public ResponseEntity<List<AnalyticsDailyResponse>> daily(Authentication auth,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(analyticsService.range(auth.getName(), start, end));
    }
}
