package com.iss.service;

import com.iss.dto.interview.InterviewRequest;
import com.iss.dto.interview.InterviewResponse;

import java.util.List;

public interface InterviewService {

    InterviewResponse scheduleInterview(InterviewRequest request);

    InterviewResponse updateInterview(Long id, InterviewRequest request);

    List<InterviewResponse> getAllInterviews();

    List<InterviewResponse> getInterviewsByCandidate(Long candidateId);

    InterviewResponse getInterviewById(Long id);

    void deleteInterview(Long id);
}
