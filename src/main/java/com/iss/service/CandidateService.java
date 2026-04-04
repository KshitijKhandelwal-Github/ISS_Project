package com.iss.service;

import com.iss.dto.candidate.CandidateDto;
import com.iss.model.enums.CandidateStatus;

import java.util.List;

public interface CandidateService {
    List<CandidateDto.CandidateResponse> getAllCandidates();
    List<CandidateDto.CandidateResponse> getActiveCandidates(Boolean isActive);
    List<CandidateDto.CandidateResponse> getByStatus(CandidateStatus status);
    List<CandidateDto.CandidateResponse> getByPrimarySkill(String skill);
    CandidateDto.CandidateResponse getCandidateById(Long id);
    CandidateDto.CandidateResponse createCandidate(CandidateDto.CandidateRequest request);
    CandidateDto.CandidateResponse updateCandidate(Long id, CandidateDto.CandidateRequest request);
    void softDeleteCandidate(Long id);
    void deleteCandidate(Long id);
}
