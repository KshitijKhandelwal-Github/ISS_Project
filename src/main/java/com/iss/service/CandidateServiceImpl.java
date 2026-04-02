package com.iss.service;

import com.iss.dto.candidate.CandidateDto;
import com.iss.model.Candidate;
import com.iss.repository.AccountsRepository;
import com.iss.repository.CandidateRepository;
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

    @Override
    public List<CandidateDto.CandidateResponse> getAllCandidates() {
        log.info("Fetching all candidates");
        return candidateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateDto.CandidateResponse getCandidateById(Long id) {
        log.info("Fetching candidate with id: {}", id);
        return candidateRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
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
                .orElse(null);
    }

    @Override
    public void deleteCandidate(Long id) {
        log.info("Deleting candidate with id: {}", id);
        if (candidateRepository.existsById(id)) {
            candidateRepository.deleteById(id);
            log.info("Successfully deleted candidate with id: {}", id);
        }
    }

    private CandidateDto.CandidateResponse mapToResponse(Candidate candidate) {
        return CandidateDto.CandidateResponse.builder()
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
            accountsRepository.findById(request.getAccountId())
                    .ifPresent(candidate::setAccounts);
        } else {
            candidate.setAccounts(null);
        }
    }
}
