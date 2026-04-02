package com.iss.service;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;

import java.util.List;

public interface InterviewService {
    InterviewResponse scheduleInterview(InterviewRequest request);
    List<InterviewResponse> getAllInterviews();
    InterviewResponse getInterviewById(Long id);
}
