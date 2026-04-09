package com.iss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.controller.CandidateController;
import com.iss.dto.candidate.CandidateDto;

import com.iss.model.Accounts;
import com.iss.model.enums.CandidateStatus;
import com.iss.repository.AccountsRepository;
import com.iss.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CandidateService candidateService;
    @MockBean
    private AccountsRepository accountsRepository;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getAllCandidates_Success() throws Exception {
        when(candidateService.getAllCandidates())
                .thenReturn(List.of(new CandidateDto.CandidateResponse()));

        mockMvc.perform(get("/api/candidates")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(candidateService).getAllCandidates();
    }

    @Test
    @WithMockUser(authorities = "ROLE_CANDIDATE")
    void getAllCandidates_Failure() throws Exception {
        when(candidateService.getAllCandidates())
                .thenReturn(List.of(new CandidateDto.CandidateResponse()));

        mockMvc.perform(get("/api/candidates")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getCandidateById_Success() throws Exception {
        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();
        res.setAccountId(1L);

        when(candidateService.getCandidateById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/candidates/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(candidateService).getCandidateById(1L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getCandidateById_NotFound() throws Exception {

        when(candidateService.getCandidateById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/candidates/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(candidateService).getCandidateById(1L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getCandidateBySkill_Success() throws Exception {
        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();
        res.setAccountId(1L);
        res.setPrimarySkill("Java");

        when(candidateService.getByPrimarySkill("Java")).thenReturn(List.of(res));

        mockMvc.perform(get("/api/candidates/skill/Java")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(candidateService).getByPrimarySkill("Java");
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getCandidateByActive_Success() throws Exception {
        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();
        res.setAccountId(1L);
        res.setIsActive(true);

        when(candidateService.getActiveCandidates(true)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/candidates/active/true")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(candidateService).getActiveCandidates(true);
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getCandidateByStatus_Success() throws Exception {
        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();
        res.setAccountId(1L);
        res.setStatus(CandidateStatus.SCHEDULED);

        when(candidateService.getByStatus(CandidateStatus.SCHEDULED)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/candidates/status/SCHEDULED")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(candidateService).getByStatus(CandidateStatus.SCHEDULED);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_HR"})
    void updateCandidate_asHr_Success() throws Exception {
        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        when(candidateService.updateCandidate(eq(1L), any()))
                .thenReturn(new CandidateDto.CandidateResponse());

        mockMvc.perform(put("/api/candidates/update?userId=1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(candidateService).updateCandidate(eq(1L), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void updateCandidate_asHr_NotFound() throws Exception {

        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        when(candidateService.updateCandidate(eq(1L), any()))
                .thenReturn(null);

        mockMvc.perform(put("/api/candidates/update?userId=1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());

        verify(candidateService).updateCandidate(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "HR")
    void updateCandidate_asHr_MissingUserId_ShouldReturn400() throws Exception {

        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        mockMvc.perform(put("/api/candidates/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(candidateService, never()).updateCandidate(any(), any());
    }

    @Test
    void updateCandidate_asCandidate_Success() throws Exception {

        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        Accounts account = new Accounts();
        account.setId(5L);

        when(accountsRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.of(account));

        when(candidateService.updateCandidate(eq(5L), any()))
                .thenReturn(new CandidateDto.CandidateResponse());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email", "candidate@test.com")
                .build();

        JwtAuthenticationToken auth = new JwtAuthenticationToken(
                jwt,
                List.of(() -> "ROLE_CANDIDATE")
        );

        mockMvc.perform(put("/api/candidates/update")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(accountsRepository).findByEmail("candidate@test.com");
        verify(candidateService).updateCandidate(eq(5L), any());
    }

    @Test
    void updateCandidate_asCandidate_UserNotFound() throws Exception {

        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        when(accountsRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.empty());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email", "candidate@test.com")
                .build();

        JwtAuthenticationToken auth = new JwtAuthenticationToken(
                jwt,
                List.of(() -> "ROLE_CANDIDATE")
        );

        mockMvc.perform(put("/api/candidates/update")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        verify(accountsRepository).findByEmail("candidate@test.com");
    }

    @Test
    void updateCandidate_asCandidate_NotFound() throws Exception {

        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        Accounts account = new Accounts();
        account.setId(5L);

        when(accountsRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.of(account));

        when(candidateService.updateCandidate(eq(5L), any()))
                .thenReturn(null);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email", "candidate@test.com")
                .build();

        JwtAuthenticationToken auth = new JwtAuthenticationToken(
                jwt,
                List.of(() -> "ROLE_CANDIDATE")
        );

        mockMvc.perform(put("/api/candidates/update")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());

        verify(candidateService).updateCandidate(eq(5L), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_TECHNICAL_PANEL")
    void updateCandidate_ShouldReturn403() throws Exception {
        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        when(candidateService.updateCandidate(eq(1L), any()))
                .thenReturn(new CandidateDto.CandidateResponse());

        mockMvc.perform(put("/api/candidates/update?userId=1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void deleteCandidate_Success() throws Exception {
        mockMvc.perform(delete("/api/candidates/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(candidateService).deleteCandidate(1L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_TECHNICAL_PANEL")
    void deleteCandidate_Failure() throws Exception {
        mockMvc.perform(delete("/api/candidates/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
