package com.iss.service;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InterviewService {

    InterviewResponse scheduleInterview(InterviewRequest request);

    List<InterviewResponse> getAllInterviews();

    InterviewResponse getInterviewById(Long id);
}
