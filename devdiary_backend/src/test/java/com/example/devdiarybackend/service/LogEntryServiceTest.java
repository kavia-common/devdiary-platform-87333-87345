package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.LogEntry;
import com.example.devdiarybackend.domain.User;
import com.example.devdiarybackend.repository.LogEntryRepository;
import com.example.devdiarybackend.repository.UserRepository;
import com.example.devdiarybackend.web.dto.CreateLogEntryRequest;
import com.example.devdiarybackend.web.dto.LogEntryResponse;
import com.example.devdiarybackend.web.dto.PagedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LogEntryService.
 */
class LogEntryServiceTest {

    @Test
    void create_persistsEntry_forUser() {
        LogEntryRepository ler = mock(LogEntryRepository.class);
        UserRepository ur = mock(UserRepository.class);
        LogEntryService svc = new LogEntryService(ler, ur);

        User u = new User();
        u.setId(5L);
        u.setEmail("u@e.com");
        when(ur.findByEmail("u@e.com")).thenReturn(Optional.of(u));

        LogEntry created = new LogEntry();
        created.setId(22L);
        created.setContent("did work");
        created.setCategory("coding");
        created.setTags("java,api");
        created.setCreatedAt(OffsetDateTime.now());

        when(ler.save(any(LogEntry.class))).thenReturn(created);

        CreateLogEntryRequest req = new CreateLogEntryRequest("did work", "coding", "java,api");
        LogEntryResponse resp = svc.create("u@e.com", req);

        assertEquals(22L, resp.id());
        assertEquals("did work", resp.content());
        assertEquals("coding", resp.category());
        assertEquals("java,api", resp.tags());
    }

    @Test
    void listMine_returnsPagedEntries() {
        LogEntryRepository ler = mock(LogEntryRepository.class);
        UserRepository ur = mock(UserRepository.class);
        LogEntryService svc = new LogEntryService(ler, ur);

        User u = new User();
        u.setId(5L);
        u.setEmail("u@e.com");
        when(ur.findByEmail("u@e.com")).thenReturn(Optional.of(u));

        LogEntry e1 = new LogEntry(); e1.setId(1L); e1.setContent("A"); e1.setCreatedAt(OffsetDateTime.now());
        LogEntry e2 = new LogEntry(); e2.setId(2L); e2.setContent("B"); e2.setCreatedAt(OffsetDateTime.now());

        Page<LogEntry> page = new PageImpl<>(List.of(e1, e2), PageRequest.of(0, 2), 2);
        when(ler.findByUserIdOrderByCreatedAtDesc(eq(5L), any())).thenReturn(page);

        PagedResponse<LogEntryResponse> resp = svc.listMine("u@e.com", 0, 2);
        assertEquals(2, resp.items().size());
        assertEquals(2, resp.totalElements());
        assertEquals(1, resp.totalPages());
    }
}
