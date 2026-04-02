package com.iss.service;

import com.iss.dto.candidate.CandidateDto;

import java.util.List;

public interface CandidateService {
    List<CandidateDto.CandidateResponse> getAllCandidates();
    CandidateDto.CandidateResponse getCandidateById(Long id);
    CandidateDto.CandidateResponse createCandidate(CandidateDto.CandidateRequest request);
    CandidateDto.CandidateResponse updateCandidate(Long id, CandidateDto.CandidateRequest request);
    void deleteCandidate(Long id);
}
