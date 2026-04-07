package com.iss.controller;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import com.iss.model.enums.InterviewRound;
import com.iss.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@EnableMethodSecurity
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<InterviewResponse> scheduleInterview( @RequestBody InterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.scheduleInterview(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL', 'CANDIDATE')")
    public ResponseEntity<List<InterviewResponse>> getAllInterviews(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(interviewService.getInterviewsByUser(userId));
        }
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL','CANDIDATE')")
    public ResponseEntity<List<InterviewResponse>> getInterviewByCandidateId(@PathVariable Long candidateId) {
        return ResponseEntity.ok(interviewService.getInterviewsByCandidate(candidateId));
    }

    @GetMapping("round/{round}")
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL')")
    public ResponseEntity<List<InterviewResponse>> getInterviewsByRound(@PathVariable InterviewRound round) {
        return ResponseEntity.ok(interviewService.getInterviewByRound(round));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'TECHNICAL_PANEL', 'CANDIDATE')")
    public ResponseEntity<InterviewResponse> getInterviewById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<InterviewResponse> updateInterview(@PathVariable Long id,
                                                             @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(interviewService.updateInterview(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
}
