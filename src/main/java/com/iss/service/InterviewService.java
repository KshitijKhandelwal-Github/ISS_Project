package com.iss.service;

import com.iss.dto.InterviewRequest;
import com.iss.dto.InterviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InterviewService {

    InterviewResponse scheduleInterview(InterviewRequest request);

    List<InterviewResponse> getAllInterviews();

    InterviewResponse getInterviewById(Long id);
}
