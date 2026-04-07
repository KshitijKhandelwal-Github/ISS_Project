package com.iss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.controller.CandidateController;
import com.iss.dto.candidate.CandidateDto;

import com.iss.model.enums.CandidateStatus;
import com.iss.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CandidateService candidateService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getAllCandidates_Success() throws Exception {
        when(candidateService.getAllCandidates())
                .thenReturn(List.of(new CandidateDto.CandidateResponse()));

        mockMvc.perform(get("/api/candidates")
                        .with(csrf()))
                .andExpect(status().isOk());
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
    @WithMockUser(authorities = "ROLE_CANDIDATE")
    void getCandidateById_Success() throws Exception {
        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();
        res.setAccountId(1L);

        when(candidateService.getCandidateById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/candidates/1")
                        .with(csrf()))
                .andExpect(status().isOk());
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
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getCandidateByActive_Success() throws Exception {
        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();
        res.setAccountId(1L);
        res.setIsActive(true);

        when(candidateService.getByPrimarySkill("Java")).thenReturn(List.of(res));

        mockMvc.perform(get("/api/candidates/active/true")
                        .with(csrf()))
                .andExpect(status().isOk());
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
    }

    @Test
    @WithMockUser(authorities = "ROLE_CANDIDATE")
    void createCandidate_Success() throws Exception {
        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();
        req.setAccountId(1L);

        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();

        when(candidateService.createCandidate(any())).thenReturn(res);

        mockMvc.perform(post("/api/candidates")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "ROLE_TECHNICAL_PANEL")
    void createCandidate_Failure() throws Exception {
        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();
        req.setAccountId(1L);

        CandidateDto.CandidateResponse res = new CandidateDto.CandidateResponse();

        when(candidateService.createCandidate(any())).thenReturn(res);

        mockMvc.perform(post("/api/candidates")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_CANDIDATE")
    void updateCandidate_Success() throws Exception {
        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        when(candidateService.updateCandidate(eq(1L), any()))
                .thenReturn(new CandidateDto.CandidateResponse());

        mockMvc.perform(put("/api/candidates/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_TECHNICAL_PANEL")
    void updateCandidate_ShouldReturn403() throws Exception {
        CandidateDto.CandidateRequest req = new CandidateDto.CandidateRequest();

        when(candidateService.updateCandidate(eq(1L), any()))
                .thenReturn(new CandidateDto.CandidateResponse());

        mockMvc.perform(put("/api/candidates/1")
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
    }

    @Test
    @WithMockUser(authorities = "ROLE_TECHNICAL_PANEL")
    void deleteCandidate_Failure() throws Exception {
        mockMvc.perform(delete("/api/candidates/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
