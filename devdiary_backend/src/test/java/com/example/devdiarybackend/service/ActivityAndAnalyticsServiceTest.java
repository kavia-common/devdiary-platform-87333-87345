package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.ActivityEvent;
import com.example.devdiarybackend.domain.AnalyticsDaily;
import com.example.devdiarybackend.domain.User;
import com.example.devdiarybackend.repository.ActivityEventRepository;
import com.example.devdiarybackend.repository.AnalyticsDailyRepository;
import com.example.devdiarybackend.repository.UserRepository;
import com.example.devdiarybackend.web.dto.ActivityEventResponse;
import com.example.devdiarybackend.web.dto.AnalyticsDailyResponse;
import com.example.devdiarybackend.web.dto.PagedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ActivityService and AnalyticsService.
 */
class ActivityAndAnalyticsServiceTest {

    @Test
    void feed_returnsPagedItems() {
        ActivityEventRepository aer = mock(ActivityEventRepository.class);
        UserRepository ur = mock(UserRepository.class);
        ActivityService svc = new ActivityService(aer, ur);

        User u = new User(); u.setId(1L); u.setEmail("u@e.com");
        when(ur.findByEmail("u@e.com")).thenReturn(Optional.of(u));

        ActivityEvent a1 = new ActivityEvent(); a1.setType("commit");
        Page<ActivityEvent> page = new PageImpl<>(List.of(a1), PageRequest.of(0, 20), 1);
        when(aer.findByUserIdOrderByOccurredAtDesc(eq(1L), any())).thenReturn(page);

        PagedResponse<ActivityEventResponse> resp = svc.feed("u@e.com", 0, 20);
        assertEquals(1, resp.items().size());
        assertEquals("commit", resp.items().get(0).type());
    }

    @Test
    void range_returnsAnalytics() {
        AnalyticsDailyRepository adr = mock(AnalyticsDailyRepository.class);
        UserRepository ur = mock(UserRepository.class);
        AnalyticsService svc = new AnalyticsService(adr, ur);

        User u = new User(); u.setId(1L); u.setEmail("u@e.com");
        when(ur.findByEmail("u@e.com")).thenReturn(Optional.of(u));

        AnalyticsDaily ad = new AnalyticsDaily();
        ad.setDate(LocalDate.now()); ad.setEntriesCount(3); ad.setCommits(2); ad.setPrs(1); ad.setMeetings(0);
        when(adr.findByUserIdAndDateBetween(eq(1L), any(), any())).thenReturn(List.of(ad));

        List<AnalyticsDailyResponse> res = svc.range("u@e.com", LocalDate.now().minusDays(1), LocalDate.now());
        assertEquals(1, res.size());
        assertEquals(3, res.get(0).entriesCount());
        assertEquals(2, res.get(0).commits());
    }
}
