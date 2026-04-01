package com.iss.controller;

import com.iss.model.Candidate;
import com.iss.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    @GetMapping
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        return candidateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Candidate createCandidate(@RequestBody Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id, @RequestBody Candidate candidateDetails) {
        return candidateRepository.findById(id)
                .map(candidate -> {
                    candidate.setName(candidateDetails.getName());
                    candidate.setPrimarySkill(candidateDetails.getPrimarySkill());
                    candidate.setStatus(candidateDetails.getStatus());
                    candidate.setYearsOfExperience(candidateDetails.getYearsOfExperience());
                    candidate.setLastWorkingDay(candidateDetails.getLastWorkingDay());
                    candidate.setSkillDetails(candidateDetails.getSkillDetails());
                    return ResponseEntity.ok(candidateRepository.save(candidate));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
