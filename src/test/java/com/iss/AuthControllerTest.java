package com.iss;

import com.iss.auth.controller.AuthController;
import com.iss.repository.AccountsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Load only controller
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountsRepository accountsRepository;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Test
    void getUser_ShouldReturnJwtClaims() throws Exception {
        mockMvc.perform(get("/api/auth")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("sub", "user1")
                                .claim("email", "test@example.com")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value("user1"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUser_ShouldHandleEmptyClaims() throws Exception {
        mockMvc.perform(get("/api/auth")
                        .with(jwt().jwt(jwt -> jwt.claims(claims -> {})))) // ✅ fixed
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }
}