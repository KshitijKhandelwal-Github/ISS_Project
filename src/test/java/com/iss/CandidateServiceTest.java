package com.iss;

import com.iss.dto.candidate.CandidateDto;
import com.iss.model.Accounts;
import com.iss.model.Candidate;
import com.iss.model.Interview;
import com.iss.model.enums.CandidateStatus;
import com.iss.model.enums.RoleType;
import com.iss.model.enums.UserStatus;
import com.iss.repository.AccountsRepository;
import com.iss.repository.CandidateRepository;
import com.iss.repository.InterviewRepository;
import com.iss.service.CandidateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private AccountsRepository  accountsRepository;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private Accounts candidateAccount;
    private Candidate candidate;
    private CandidateDto.CandidateRequest request;

    @BeforeEach
    void setUp() {
        candidateAccount = Accounts.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@candidate.com")
                .password("password")
                .role(RoleType.ROLE_CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();
        accountsRepository.save(candidateAccount);

        candidate = Candidate.builder()
                .id(candidateAccount.getId())
                .name("John Doe")
                .primarySkill("Java")
                .skillDetails("Spring Boot, Hibernate")
                .status(CandidateStatus.SCREENED)
                .yearsOfExperience(4)
                .noticePeriod(30)
                .isActive(true)
                .build();

        request = new CandidateDto.CandidateRequest();
        request.setPrimarySkill("Java");
        request.setSkillDetails("Spring Boot, Hibernate");
        request.setStatus(CandidateStatus.SCREENED);
        request.setYearsOfExperience(4);
        request.setNoticePeriod(30);
        request.setIsActive(true);
    }

    @Test
    void create_ShouldReturnSavedCandidate() {
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        CandidateDto.CandidateResponse response = candidateService.createCandidate(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getPrimarySkill()).isEqualTo("Java");
        assertThat(response.getStatus()).isEqualTo(CandidateStatus.SCREENED);
        verify(candidateRepository, times(1)).save(any(Candidate.class));
    }

    @Test
    void getById_WhenExists_ShouldReturnCandidate() {
        candidate.setAccounts(candidateAccount);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        CandidateDto.CandidateResponse response = candidateService.getCandidateById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getAccountId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.getCandidateById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Candidate not found with id: 99");
    }

    @Test
    void getAll_ShouldReturnAllCandidates() {
        Candidate candidate2 = Candidate.builder()
                .id(2L).name("Jane Doe")
                .primarySkill("ReactJS")
                .status(CandidateStatus.SCHEDULED)
                .isActive(true).build();

        when(candidateRepository.findAll()).thenReturn(List.of(candidate, candidate2));

        List<CandidateDto.CandidateResponse> result = candidateService.getAllCandidates();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Doe");
    }

    @Test
    void getActive_ShouldReturnOnlyActiveCandidates() {
        when(candidateRepository.findByIsActive(true)).thenReturn(List.of(candidate));

        List<CandidateDto.CandidateResponse> result = candidateService.getActiveCandidates(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
    }

    @Test
    void getByStatus_ShouldFilterByStatus() {
        when(candidateRepository.findByStatus(CandidateStatus.SCREENED)).thenReturn(List.of(candidate));

        List<CandidateDto.CandidateResponse> result = candidateService.getByStatus(CandidateStatus.SCREENED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CandidateStatus.SCREENED);
    }

    @Test
    void getByPrimarySkill_ShouldFilterBySkill() {
        when(candidateRepository.findByPrimarySkill("Java")).thenReturn(List.of(candidate));

        List<CandidateDto.CandidateResponse> result = candidateService.getByPrimarySkill("Java");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrimarySkill()).isEqualTo("Java");
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturn() {
        request.setName("John Updated");
        request.setStatus(CandidateStatus.SCHEDULED);

        Candidate updated = Candidate.builder()
                .id(1L).name("John Updated")
                .primarySkill("Java")
                .status(CandidateStatus.SCHEDULED)
                .isActive(true).build();

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(updated);

        doNothing().when(applicationEventPublisher).publishEvent(any());

        CandidateDto.CandidateResponse response = candidateService.updateCandidate(1L, request);

        assertThat(response.getName()).isEqualTo("John Updated");
        assertThat(response.getStatus()).isEqualTo(CandidateStatus.SCHEDULED);
    }

    @Test
    void delete_ShouldSoftDeleteCandidate() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        candidateService.softDeleteCandidate(1L);

        // Verify save was called (soft delete sets active=false)
        verify(candidateRepository, times(1)).save(any(Candidate.class));
        assertThat(candidate.getIsActive()).isFalse();
    }

    @Test
    void delete_ShouldHardDeleteCandidate() {
        Interview fakeInterview = new Interview();
        when(interviewRepository.findByCandidateId(1L)).thenReturn(List.of(fakeInterview));

        when(candidateRepository.existsById(1L)).thenReturn(true);
        when(interviewRepository.findByCandidateId(1L)).thenReturn(List.of()); // Return empty list or mock interviews

        candidateService.deleteCandidate(1L);

        verify(interviewRepository, times(1)).deleteAll(anyList());
        verify(candidateRepository, times(1)).deleteById(1L);
        verify(accountsRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {

        assertThatThrownBy(() -> candidateService.deleteCandidate(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Candidate not found with id: 99");
    }
}
