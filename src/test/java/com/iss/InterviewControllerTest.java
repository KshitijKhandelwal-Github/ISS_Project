package com.iss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.controller.InterviewController;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import com.iss.service.InterviewService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private InterviewService interviewService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void scheduleInterview_Success() throws Exception {
        InterviewRequest req = new InterviewRequest();

        when(interviewService.scheduleInterview(any()))
                .thenReturn(new InterviewResponse());

        mockMvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "ROLE_TECHNICAL_PANEL")
    void getAllInterviews_Success() throws Exception {
        when(interviewService.getAllInterviews()).thenReturn(List.of());

        mockMvc.perform(get("/api/interviews"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_CANDIDATE")
    void getInterviewByCandidateId_Success() throws Exception {
        when(interviewService.getInterviewsByCandidate(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/interviews/candidate/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void getInterviewsByRound_Success() throws Exception {
        when(interviewService.getInterviewByRound(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/interviews/round/R1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_CANDIDATE")
    void getInterviewById_Success() throws Exception {
        when(interviewService.getInterviewById(1L))
                .thenReturn(new InterviewResponse());

        mockMvc.perform(get("/api/interviews/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void updateInterview_Success() throws Exception {
        InterviewRequest req = new InterviewRequest();

        when(interviewService.updateInterview(eq(1L), any()))
                .thenReturn(new InterviewResponse());

        mockMvc.perform(put("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_HR")
    void deleteInterview_Success() throws Exception {
        mockMvc.perform(delete("/api/interviews/1"))
                .andExpect(status().isNoContent());
    }
}