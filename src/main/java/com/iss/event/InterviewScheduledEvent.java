package com.iss.event;

public class InterviewScheduledEvent {

    private final Long interviewId;
    private final String candidateName;
    private final String hrName;

    public InterviewScheduledEvent(Long interviewId, String candidateName, String hrName) {
        this.interviewId = interviewId;
        this.candidateName = candidateName;
        this.hrName = hrName;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getHrName() {
        return hrName;
    }
}
