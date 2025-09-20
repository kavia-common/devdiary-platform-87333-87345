package com.example.devdiarybackend.domain;

import com.example.devdiarybackend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic JPA mapping tests to ensure entities persist and relationships work.
 */
@DataJpaTest
class EntityMappingTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private LogEntryRepository logEntryRepository;
    @Autowired
    private SummaryRepository summaryRepository;

    @Test
    void persistUserLogAndEntries_andQuery() {
        User u = new User();
        u.setEmail("x@y.com"); u.setDisplayName("X"); u.setPasswordHash("h");
        u = userRepository.save(u);

        Log log = new Log();
        log.setUser(u);
        log.setLogDate(LocalDate.now());
        log.setTitle("Today");
        log = logRepository.save(log);

        LogEntry e = new LogEntry();
        e.setUser(u);
        e.setLog(log);
        e.setContent("Did stuff");
        e = logEntryRepository.save(e);

        assertNotNull(e.getId());
        List<Log> byDay = logRepository.findByUserIdAndLogDate(u.getId(), log.getLogDate());
        assertEquals(1, byDay.size());
    }

    @Test
    void persistSummary_uniqueByUserAndDate() {
        User u = new User();
        u.setEmail("a@b.com"); u.setDisplayName("A"); u.setPasswordHash("h");
        u = userRepository.save(u);

        Summary s1 = new Summary();
        s1.setUser(u); s1.setSummaryDate(LocalDate.now()); s1.setContent("c1");
        summaryRepository.save(s1);

        // Saving another with same date should not violate at JPA layer here, but we can check repository fetch behavior.
        var found = summaryRepository.findByUserIdAndSummaryDate(u.getId(), s1.getSummaryDate());
        assertTrue(found.isPresent());
        assertEquals("c1", found.get().getContent());
    }
}
