package com.iss.service;

import com.iss.model.Accounts;
import com.iss.model.enums.RoleType;
import com.iss.repository.AccountsRepository;
import com.iss.model.Candidate;
import com.iss.repository.CandidateRepository;
import com.iss.exception.ResourceNotFoundException;
import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import com.iss.model.Interview;
import com.iss.event.InterviewScheduledEvent;
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

        Interview interview = new Interview();
        applyInterviewRequest(interview, request, candidate, hrUser);

        Interview savedInterview = interviewRepository.save(interview);
        applicationEventPublisher.publishEvent(
                new InterviewScheduledEvent(savedInterview.getId(), candidate.getName(), hrUser.getFullName())
        );

        return mapToResponse(savedInterview);
    }

    @Override
    public InterviewResponse updateInterview(Long id, InterviewRequest request) {
        validateInterviewRequest(request);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + request.getCandidateId()));

        Accounts hrUser = findHrUser(request.getHrUserId());
        applyInterviewRequest(interview, request, candidate, hrUser);
        return mapToResponse(interviewRepository.save(interview));
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

    private void applyInterviewRequest(Interview interview,
                                       InterviewRequest request,
                                       Candidate candidate,
                                       Accounts hrUser) {
        interview.setInterviewDate(request.getInterviewDate());
        interview.setTimeSlot(request.getTimeSlot());
        interview.setPanelName(request.getPanelName());
        interview.setCandidate(candidate);
        interview.setHrUser(hrUser);
        interview.setRound(request.getRound());
        interview.setStatus(request.getStatus());
    }

    private InterviewResponse mapToResponse(Interview interview) {
        InterviewResponse response = new InterviewResponse();
        response.setId(interview.getId());
        response.setInterviewDate(interview.getInterviewDate());
        response.setTimeSlot(interview.getTimeSlot());
        response.setPanelName(interview.getPanelName());
        response.setCandidateId(interview.getCandidate().getId());
        response.setCandidateName(interview.getCandidate().getName());
        response.setHrUserId(interview.getHrUser().getId());
        response.setHrName(interview.getHrUser().getFullName());
        response.setRound(interview.getRound());
        response.setStatus(interview.getStatus());
        return response;
    }
}