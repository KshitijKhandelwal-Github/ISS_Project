package com.iss.service;

import com.iss.dto.candidate.CandidateDto;
import com.iss.exception.ResourceNotFoundException;
import com.iss.model.Accounts;
import com.iss.model.Candidate;
import com.iss.model.Interview;
import com.iss.model.enums.CandidateStatus;
import com.iss.model.enums.RoleType;
import com.iss.repository.AccountsRepository;
import com.iss.repository.CandidateRepository;
import com.iss.repository.InterviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandidateServiceImpl implements CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Override
    public List<CandidateDto.CandidateResponse> getAllCandidates() {
        log.info("Fetching all candidates");
        return candidateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto.CandidateResponse> getActiveCandidates(Boolean isActive) {
        log.info("Fetching all active candidates");
        return candidateRepository.findByIsActive(isActive).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto.CandidateResponse> getByStatus(CandidateStatus status) {
        log.info("Fetching all candidates with status {}", status);
        return candidateRepository.findByStatus(status).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto.CandidateResponse> getByPrimarySkill(String skill) {
        log.info("Fetching all candidates with primary skill {}", skill);
        return candidateRepository.findByPrimarySkill(skill).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CandidateDto.CandidateResponse getCandidateById(Long id) {
        log.info("Fetching candidate with id: {}", id);
        return candidateRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
    }

    @Override
    public CandidateDto.CandidateResponse createCandidate(CandidateDto.CandidateRequest request) {
        log.info("Creating new candidate for account id: {}", request.getAccountId());
        Candidate candidate = new Candidate();
        mapRequestToEntity(request, candidate);
        Candidate savedCandidate = candidateRepository.save(candidate);
        log.info("Successfully created candidate with id: {}", savedCandidate.getId());
        return mapToResponse(savedCandidate);
    }

    @Override
    public CandidateDto.CandidateResponse updateCandidate(Long id, CandidateDto.CandidateRequest request) {
        log.info("Updating candidate with id: {}", id);
        return candidateRepository.findById(id)
                .map(candidate -> {
                    mapRequestToEntity(request, candidate);
                    Candidate updatedCandidate = candidateRepository.save(candidate);
                    log.info("Successfully updated candidate with id: {}", id);
                    return mapToResponse(updatedCandidate);
                })
                .orElseThrow( () ->  new ResourceNotFoundException("Candidate not found with id: " + id));
    }

    @Override
    public void softDeleteCandidate(Long id) {
        log.info("Soft deleting candidate with id: {}", id);
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        candidate.setIsActive(false);
        candidateRepository.save(candidate);
        log.info("Successfully soft deleted candidate with id: {}", id);
    }

    @Override
    public void deleteCandidate(Long id) {
        log.info("Deleting candidate with id: {}", id);
        if (candidateRepository.existsById(id)) {
            List<Interview> candidateInterviews = interviewRepository.findByCandidateId(id);
            interviewRepository.deleteAll(candidateInterviews);
            candidateRepository.deleteById(id);
            accountsRepository.deleteById(id);
            log.info("Successfully deleted candidate with id: {}", id);
        }
        else{
            throw new ResourceNotFoundException("Candidate not found with id: " + id);
        }
    }

    private CandidateDto.CandidateResponse mapToResponse(Candidate candidate) {
        return CandidateDto.CandidateResponse.builder()
                .accountId(candidate.getId())
                .name(candidate.getName())
                .accountId(candidate.getAccounts() != null ? candidate.getAccounts().getId() : null)
                .cvReference(candidate.getCvReference())
                .primarySkill(candidate.getPrimarySkill())
                .skillDetails(candidate.getSkillDetails())
                .status(candidate.getStatus())
                .yearsOfExperience(candidate.getYearsOfExperience())
                .lastWorkingDay(candidate.getLastWorkingDay())
                .noticePeriod(candidate.getNoticePeriod())
                .isActive(candidate.getIsActive())
                .build();
    }

    private void mapRequestToEntity(CandidateDto.CandidateRequest request, Candidate candidate) {
        candidate.setCvReference(request.getCvReference());
        candidate.setPrimarySkill(request.getPrimarySkill());
        candidate.setSkillDetails(request.getSkillDetails());
        candidate.setStatus(request.getStatus());
        candidate.setYearsOfExperience(request.getYearsOfExperience());
        candidate.setLastWorkingDay(request.getLastWorkingDay());
        candidate.setNoticePeriod(request.getNoticePeriod());
        candidate.setIsActive(request.getIsActive());

        if (request.getAccountId() != null) {
            Accounts candidateAccount = accountsRepository.findById(request.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));

            // Check if the account has the correct role
            if (candidateAccount.getRole() != RoleType.ROLE_CANDIDATE) {
                throw new IllegalArgumentException("The provided account does not have the CANDIDATE role.");
            }
            candidateAccount.setFullName(request.getName());
            candidate.setAccounts(candidateAccount);
        } else {
            candidate.setAccounts(null);
        }
    }
}
