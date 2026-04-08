package com.iss;

import com.iss.auth.controller.KeycloakAuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KeycloakAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "app.security.mode=keycloak")

class KeycloakAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void redirectToKeycloak_ShouldReturn302WithLocationHeader() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "/oauth2/authorization/keycloak"));
    }

    @Test
    void logoutSuccess_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/api/auth/logout/success"))
                .andExpect(status().isOk())
                .andExpect(content().string("Keycloak logout completed"));
    }
}