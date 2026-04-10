package com.iss.controller;

import com.iss.dto.candidate.CandidateDto;
import com.iss.dto.interview.InterviewResponse;
import com.iss.model.Accounts;
import com.iss.model.enums.CandidateStatus;
import com.iss.repository.AccountsRepository;
import com.iss.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@EnableMethodSecurity
public class CandidateController {

    @Autowired
    private CandidateService candidateService;
    @Autowired
    private  AccountsRepository accountsRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('HR')")
    public List<CandidateDto.CandidateResponse> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR')")
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
    @PreAuthorize("hasAnyRole('HR')")
    public ResponseEntity<List<CandidateDto.CandidateResponse>> getCandidatesBySkill(@PathVariable String skill) {
        return ResponseEntity.ok(candidateService.getByPrimarySkill(skill));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('HR', 'CANDIDATE')")
    public ResponseEntity<CandidateDto.CandidateResponse> updateCandidate(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @RequestBody CandidateDto.CandidateRequest request) {

        CandidateDto.CandidateResponse response;

        boolean isCandidate = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CANDIDATE"));

        System.out.println(authentication.getAuthorities());

        if (!isCandidate) {
            if (userId != null) {
                response = candidateService.updateCandidate(userId, request);
                if (response == null) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(response);
            }
        }
        else {

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String email = jwt.getClaim("email");

            Accounts account = accountsRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            response = candidateService.updateCandidate(account.getId(), request);

            if (response == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR')")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}
