package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.Integration;
import com.example.devdiarybackend.domain.User;
import com.example.devdiarybackend.domain.UserIntegration;
import com.example.devdiarybackend.repository.IntegrationRepository;
import com.example.devdiarybackend.repository.UserIntegrationRepository;
import com.example.devdiarybackend.repository.UserRepository;
import com.example.devdiarybackend.web.dto.ConnectIntegrationRequest;
import com.example.devdiarybackend.web.dto.IntegrationResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IntegrationService.
 */
class IntegrationServiceTest {

    @Test
    void listAll_mapsResponses() {
        IntegrationRepository ir = mock(IntegrationRepository.class);
        UserIntegrationRepository uir = mock(UserIntegrationRepository.class);
        UserRepository ur = mock(UserRepository.class);

        IntegrationService svc = new IntegrationService(ir, uir, ur);

        Integration i1 = new Integration(); i1.setId(1L); i1.setKey("github"); i1.setName("GitHub");
        when(ir.findAll()).thenReturn(List.of(i1));

        List<IntegrationResponse> res = svc.listAll();
        assertEquals(1, res.size());
        assertEquals("github", res.get(0).key());
        assertEquals("GitHub", res.get(0).name());
    }

    @Test
    void connect_createsOrUpdatesUserIntegration() {
        IntegrationRepository ir = mock(IntegrationRepository.class);
        UserIntegrationRepository uir = mock(UserIntegrationRepository.class);
        UserRepository ur = mock(UserRepository.class);

        IntegrationService svc = new IntegrationService(ir, uir, ur);

        User u = new User(); u.setId(10L); u.setEmail("a@b.com");
        when(ur.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        Integration integ = new Integration(); integ.setId(2L); integ.setKey("github");
        when(ir.findByKey("github")).thenReturn(Optional.of(integ));
        when(uir.findByUserIdAndIntegrationId(10L, 2L)).thenReturn(Optional.empty());

        svc.connect("a@b.com", new ConnectIntegrationRequest("github", "{\"token\":\"x\"}"));

        verify(uir).save(any(UserIntegration.class));
    }
}
