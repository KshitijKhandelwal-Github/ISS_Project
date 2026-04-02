package com.iss.controller;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import com.iss.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<InterviewResponse> scheduleInterview(@RequestBody InterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.scheduleInterview(request));
    }

    @GetMapping
    public ResponseEntity<List<InterviewResponse>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponse> getInterviewById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }
}

