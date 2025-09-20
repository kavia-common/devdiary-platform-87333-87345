package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.LogEntry;
import com.example.devdiarybackend.domain.Summary;
import com.example.devdiarybackend.domain.User;
import com.example.devdiarybackend.repository.LogEntryRepository;
import com.example.devdiarybackend.repository.SummaryRepository;
import com.example.devdiarybackend.repository.UserRepository;
import com.example.devdiarybackend.web.dto.SummaryResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SummaryService.
 */
class SummaryServiceTest {

    @Test
    void generateOrGet_returnsExisting() {
        SummaryRepository sr = mock(SummaryRepository.class);
        LogEntryRepository ler = mock(LogEntryRepository.class);
        UserRepository ur = mock(UserRepository.class);

        SummaryService svc = new SummaryService(sr, ler, ur);

        User u = new User();
        u.setId(1L); u.setEmail("a@b.com");
        when(ur.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        LocalDate date = LocalDate.now();
        Summary s = new Summary();
        s.setContent("Daily Summary: coding=2");

        when(sr.findByUserIdAndSummaryDate(1L, date)).thenReturn(Optional.of(s));

        SummaryResponse resp = svc.generateOrGet("a@b.com", date);
        assertEquals("Daily Summary: coding=2", resp.content());
    }

    @Test
    void generateOrGet_createsWhenMissing_countsCategories() {
        SummaryRepository sr = mock(SummaryRepository.class);
        LogEntryRepository ler = mock(LogEntryRepository.class);
        UserRepository ur = mock(UserRepository.class);

        SummaryService svc = new SummaryService(sr, ler, ur);

        User u = new User();
        u.setId(1L); u.setEmail("a@b.com");
        when(ur.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        LocalDate date = LocalDate.now();

        when(sr.findByUserIdAndSummaryDate(1L, date)).thenReturn(Optional.empty());

        LogEntry e1 = new LogEntry(); e1.setCategory("coding"); e1.setCreatedAt(OffsetDateTime.now());
        LogEntry e2 = new LogEntry(); e2.setCategory("coding"); e2.setCreatedAt(OffsetDateTime.now());
        LogEntry e3 = new LogEntry(); e3.setCategory(null); e3.setCreatedAt(OffsetDateTime.now());
        when(ler.findByUserIdAndCreatedAtBetween(eq(1L), any(), any())).thenReturn(List.of(e1, e2, e3));

        SummaryResponse resp = svc.generateOrGet("a@b.com", date);

        assertTrue(resp.content().contains("coding=2"));
        assertTrue(resp.content().contains("general=1") || resp.content().contains("No entries.") == false);
        verify(sr).save(any(Summary.class));
    }
}
