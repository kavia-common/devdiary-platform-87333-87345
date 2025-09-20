package com.example.devdiarybackend.web;

import com.example.devdiarybackend.DevDiaryBackendApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests using H2 and MockMvc for primary API flows.
 */
@SpringBootTest(classes = DevDiaryBackendApplication.class)
@AutoConfigureMockMvc
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void initUserAndLogin() throws Exception {
        // Register (idempotent if run multiple times may fail due to unique, so ignore duplicate by catching 4xx)
        String payload = """
            {"email":"test@example.com","displayName":"Test","password":"secret"}
            """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        // Login
        String login = """
            {"email":"test@example.com","password":"secret"}
            """;
        String resp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(resp);
        token = node.get("token").asText();
    }

    @Test
    void userMe_returnsProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.displayName", is("Test")));
    }

    @Test
    void logEntries_createAndList() throws Exception {
        String create = """
            {"content":"Worked on API","category":"coding","tags":"java,api"}
            """;
        mockMvc.perform(post("/api/log-entries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(create))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.content", is("Worked on API")));

        mockMvc.perform(get("/api/log-entries?page=0&size=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", not(empty())));
    }

    @Test
    void summaries_generateOrGet() throws Exception {
        String body = """
            {"date":"%s"}
            """.formatted(LocalDate.now().toString());
        mockMvc.perform(post("/api/summaries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", notNullValue()))
                .andExpect(jsonPath("$.content", not(blankOrNullString())));
    }

    @Test
    void integrations_listAndConnect() throws Exception {
        mockMvc.perform(get("/api/integrations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        String connect = """
            {"key":"github","configJson":"{\\"token\\":\\"abc\\"}"}
            """;
        mockMvc.perform(post("/api/integrations/connect")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(connect))
                .andExpect(status().isOk());
    }

    @Test
    void activity_feed_and_analytics_daily() throws Exception {
        // Feed (should return empty or items)
        mockMvc.perform(get("/api/activity?page=0&size=20")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)));

        var start = LocalDate.now().minusDays(7).toString();
        var end = LocalDate.now().toString();
        mockMvc.perform(get("/api/analytics/daily?start=" + start + "&end=" + end)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void endpoints_requireAuth_whenMissingToken() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/log-entries")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/summaries")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/integrations")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/activity")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/analytics/daily?start=2020-01-01&end=2020-01-02")).andExpect(status().isUnauthorized());
    }
}
