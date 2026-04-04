package com.iss.controller;

import com.iss.dto.candidate.CandidateDto;
import com.iss.model.enums.CandidateStatus;
import com.iss.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping
    @PreAuthorize("hasAnyRole('HR','TECHNICAL_PANEL')")
    public List<CandidateDto.CandidateResponse> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR','TECHNICAL_PANEL', 'CANDIDATE')")
    public ResponseEntity<CandidateDto.CandidateResponse> getCandidateById(@PathVariable Long id) {
        CandidateDto.CandidateResponse response = candidateService.getCandidateById(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL')")
    public ResponseEntity<List<CandidateDto.CandidateResponse>>getCandidatesByStatus(@PathVariable CandidateStatus status) {
        return ResponseEntity.ok(candidateService.getByStatus(status));
    }

    @GetMapping("/active/{isActive}")
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL')")
    public ResponseEntity<List<CandidateDto.CandidateResponse>> getCandidatesByActive(@PathVariable Boolean isActive) {
        return ResponseEntity.ok(candidateService.getActiveCandidates(isActive));
    }

    @GetMapping("/skill/{skill}")
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL')")
    public ResponseEntity<List<CandidateDto.CandidateResponse>> getCandidatesBySkill(@PathVariable String skill) {
        return ResponseEntity.ok(candidateService.getByPrimarySkill(skill));
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateDto.CandidateResponse> createCandidate(@RequestBody CandidateDto.CandidateRequest request) {
        CandidateDto.CandidateResponse response = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE')")
    public ResponseEntity<CandidateDto.CandidateResponse> updateCandidate(@PathVariable Long id, @RequestBody CandidateDto.CandidateRequest request) {
        CandidateDto.CandidateResponse response = candidateService.updateCandidate(id, request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR','CANDIDATE')")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}
