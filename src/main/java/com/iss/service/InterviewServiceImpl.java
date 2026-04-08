package com.iss.service;

import com.iss.model.Accounts;
import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.RoleType;
import com.iss.repository.AccountsRepository;
import com.iss.model.Candidate;
import com.iss.repository.CandidateRepository;
import com.iss.exception.ResourceNotFoundException;
import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import com.iss.event.InterviewScheduledEvent;
import com.iss.model.Interview;
import com.iss.repository.InterviewRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final AccountsRepository userAccountRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public InterviewServiceImpl(InterviewRepository interviewRepository,
                                CandidateRepository candidateRepository,
                                AccountsRepository userAccountRepository,
                                ApplicationEventPublisher applicationEventPublisher) {
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.userAccountRepository = userAccountRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public InterviewResponse scheduleInterview(InterviewRequest request) {
        validateInterviewRequest(request);
        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + request.getCandidateId()));

        Accounts hrUser = findHrUser(request.getHrUserId());
        Accounts panelUser = findPanelUser(request.getPanelId());

        Interview interview = new Interview();
        applyInterviewRequest(interview, request, candidate, hrUser, panelUser);

        Interview savedInterview = interviewRepository.save(interview);
        applicationEventPublisher.publishEvent(
                new InterviewScheduledEvent(
                        savedInterview.getId(),
                        candidate.getName(),
                        hrUser.getFullName(),
                        hrUser.getEmail(),
                        panelUser.getFullName(),
                        panelUser.getEmail(),
                        savedInterview.getInterviewDate(),
                        savedInterview.getTimeSlot(),
                        savedInterview.getRound(),
                        savedInterview.getStatus()
                )
        );

        return mapToResponse(savedInterview);
    }

    @Override
    public InterviewResponse updateInterview(Long id, InterviewRequest request) {

        // 1. Find the existing record (The "Original")
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));

        // 2. PARTIAL UPDATES: Only update fields that are NOT NULL in the request
        if (request.getInterviewDate() != null) {
            interview.setInterviewDate(request.getInterviewDate());
        }
        if (request.getTimeSlot() != null) {
            interview.setTimeSlot(request.getTimeSlot());
        }
        if (request.getRound() != null) {
            interview.setRound(request.getRound());
        }
        if (request.getStatus() != null) {
            interview.setStatus(request.getStatus());
        }

        // 3. OPTIONAL RELATIONSHIPS: Only look up and link if IDs are provided
        if (request.getCandidateId() != null) {
            Candidate candidate = candidateRepository.findById(request.getCandidateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Candidate not found with id: " + request.getCandidateId()));
            interview.setCandidate(candidate);
        }

        if (request.getHrUserId() != null) {
            Accounts hrUser = findHrUser(request.getHrUserId());
            interview.setHrUser(hrUser);
        }

        if (request.getPanelId() != null) {
            Accounts panelUser = findPanelUser(request.getPanelId());
            interview.setPanelUser(panelUser);
        }

        // 4. Save the modified "Original"
        Interview savedInterview = interviewRepository.save(interview);
        return mapToResponse(savedInterview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> getAllInterviews() {
        return interviewRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsByCandidate(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new ResourceNotFoundException("Candidate not found with id: " + candidateId);
        }
        return interviewRepository.findByCandidateId(candidateId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsByUser(Long userId) {
        if (!userAccountRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return interviewRepository.findByHrUserIdOrPanelUserId(userId, userId).stream().map(this::mapToResponse).toList();
    }




    @Override
    public List<InterviewResponse> getInterviewByRound(InterviewRound round) {
        return interviewRepository.findByRound(round).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewResponse getInterviewById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
        return mapToResponse(interview);
    }

    @Override
    public void deleteInterview(Long id) {
        interviewRepository.delete(
                interviewRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id))
        );
    }

    private void validateInterviewRequest(InterviewRequest request) {
        if (request.getTimeSlot().isBefore(java.time.LocalTime.of(8, 0)) || request.getTimeSlot().isAfter(java.time.LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("Interview time slot must be between 08:00 and 20:00");
        }
    }

    private Accounts findHrUser(Long hrUserId) {
        Accounts hrUser = userAccountRepository.findById(hrUserId)
                .orElseThrow(() -> new ResourceNotFoundException("HR user not found with id: " + hrUserId));
        if (hrUser.getRole() != RoleType.ROLE_HR) {
            throw new IllegalArgumentException("Selected user is not an HR user: " + hrUserId);
        }
        return hrUser;
    }

    private Accounts findPanelUser(Long panelUserId) {
        if (panelUserId == null) {
            return null;
        }

        Accounts panelUser = userAccountRepository.findById(panelUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Technical panel user not found with id: " + panelUserId));
        if (panelUser.getRole() != RoleType.ROLE_TECHNICAL_PANEL) {
            throw new IllegalArgumentException("Selected user is not a technical panel user: " + panelUserId);
        }
        return panelUser;
    }

    private void applyInterviewRequest(Interview interview,
                                       InterviewRequest request,
                                       Candidate candidate,
                                       Accounts hrUser,
                                       Accounts panelUser) {
        interview.setInterviewDate(request.getInterviewDate());
        interview.setTimeSlot(request.getTimeSlot());
        interview.setPanelUser(panelUser);
        interview.setCandidate(candidate);
        interview.setHrUser(hrUser);
        interview.setPanelUser(panelUser);
        interview.setRound(request.getRound());
        interview.setStatus(request.getStatus());
    }

    private InterviewResponse mapToResponse(Interview interview) {
        InterviewResponse response = new InterviewResponse();
        response.setId(interview.getId());
        response.setInterviewDate(interview.getInterviewDate());
        response.setTimeSlot(interview.getTimeSlot());
        response.setPanelId(interview.getPanelUser().getId());
        response.setPanelName(interview.getPanelUser().getFullName());
        response.setCandidateId(interview.getCandidate().getId());
        response.setCandidateName(interview.getCandidate().getName());
        response.setHrUserId(interview.getHrUser().getId());
        response.setHrName(interview.getHrUser().getFullName());
        if (interview.getPanelUser() != null) {
            response.setPanelUserId(interview.getPanelUser().getId());
            response.setPanelUserName(interview.getPanelUser().getFullName());
        }
        response.setRound(interview.getRound());
        response.setStatus(interview.getStatus());
        return response;
    }
}
