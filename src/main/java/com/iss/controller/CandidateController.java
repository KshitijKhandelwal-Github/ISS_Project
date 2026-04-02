package com.iss.controller;

import com.iss.dto.candidate.CandidateDto;
import com.iss.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping
    public List<CandidateDto.CandidateResponse> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDto.CandidateResponse> getCandidateById(@PathVariable Long id) {
        CandidateDto.CandidateResponse response = candidateService.getCandidateById(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CandidateDto.CandidateResponse> createCandidate(@RequestBody CandidateDto.CandidateRequest request) {
        CandidateDto.CandidateResponse response = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateDto.CandidateResponse> updateCandidate(@PathVariable Long id, @RequestBody CandidateDto.CandidateRequest request) {
        CandidateDto.CandidateResponse response = candidateService.updateCandidate(id, request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}
