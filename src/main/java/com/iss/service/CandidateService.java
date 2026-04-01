package com.iss.service;

import com.iss.dto.candidate.CandidateRequest;
import com.iss.dto.candidate.CandidateResponse;

import java.util.List;

public interface CandidateService {
    List<CandidateResponse> getAllCandidates();
    CandidateResponse getCandidateById(Long id);
    CandidateResponse createCandidate(CandidateRequest request);
    CandidateResponse updateCandidate(Long id, CandidateRequest request);
    void deleteCandidate(Long id);
}
