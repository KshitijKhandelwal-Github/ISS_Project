package com.iss;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import com.iss.model.Accounts;
import com.iss.model.Candidate;
import com.iss.model.Interview;
import com.iss.model.enums.*;
import com.iss.repository.AccountsRepository;
import com.iss.repository.CandidateRepository;
import com.iss.repository.InterviewRepository;
import com.iss.service.InterviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private Candidate candidate;
    private Accounts hrAccount;
    private Interview interview;
    private InterviewRequest request;

    @BeforeEach
    void setUp() {
        hrAccount = Accounts.builder()
                .id(1L)
                .fullName("admin")
                .email("admin@candidate.com")
                .role(RoleType.ROLE_HR)
                .status(UserStatus.ACTIVE)
                .build();

        Accounts candidateAccount = Accounts.builder()
                .id(2L)
                .fullName("John Doe")
                .email("john@candidate.com")
                .role(RoleType.ROLE_CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();

        candidate = Candidate.builder()
                .id(2L)
                .accounts(candidateAccount)
                .name("John Doe")
                .primarySkill("Java")
                .status(CandidateStatus.SCREENED)
                .isActive(true)
                .build();

        interview = Interview.builder()
                .id(1L)
                .interviewDate(LocalDate.of(2026, 4, 10))
                .timeSlot(LocalTime.of(10, 0))
                .panelName("Jane Smith")
                .candidate(candidate)
                .hrUser(hrAccount)
                .round(InterviewRound.R1)
                .status(InterviewStatus.ON_HOLD)
                .build();

        request = new InterviewRequest();
        request.setInterviewDate(LocalDate.of(2026, 4, 10));
        request.setTimeSlot(LocalTime.of(10, 0));
        request.setPanelName("Jane Smith");
        request.setCandidateId(2L);
        request.setHrUserId(1L);
        request.setRound(InterviewRound.R1);
        request.setStatus(InterviewStatus.ON_HOLD);
    }

    @Test
    void create_ShouldReturnScheduledInterview() {
        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(hrAccount));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        InterviewResponse response = interviewService.scheduleInterview(request);

        assertThat(response).isNotNull();
        assertThat(response.getCandidateId()).isEqualTo(2L);
        assertThat(response.getHrUserId()).isEqualTo(1L);
        verify(interviewRepository, times(1)).save(any(Interview.class));
    }

    @Test
    void create_WhenCandidateNotFound_ShouldThrowException() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());
        request.setCandidateId(99L);

        assertThatThrownBy(() -> interviewService.scheduleInterview(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Candidate not found with id: 99");
    }

    @Test
    void getById_WhenExists_ShouldReturnInterview() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        InterviewResponse response = interviewService.getInterviewById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPanelName()).isEqualTo("Jane Smith");
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewService.getInterviewById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Interview not found with id: 99");
    }

    @Test
    void getAll_ShouldReturnAllInterviews() {
        when(interviewRepository.findAll()).thenReturn(List.of(interview));

        List<InterviewResponse> result = interviewService.getAllInterviews();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRound()).isEqualTo(InterviewRound.R1);
    }

    @Test
    void getByCandidate_ShouldReturnInterviewsForCandidate() {
        when(candidateRepository.existsById(2L)).thenReturn(true);
        when(interviewRepository.findByCandidateId(2L)).thenReturn(List.of(interview));

        List<InterviewResponse> result = interviewService.getInterviewsByCandidate(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCandidateId()).isEqualTo(2L);
    }

    @Test
    void update_ShouldUpdateInterview() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(hrAccount));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        InterviewResponse response = interviewService.updateInterview(1L, request);

        assertThat(response).isNotNull();
    }

    @Test
    void delete_ShouldDeleteInterview() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        interviewService.deleteInterview(1L);

        verify(interviewRepository, times(1)).delete(interview);
    }
}
