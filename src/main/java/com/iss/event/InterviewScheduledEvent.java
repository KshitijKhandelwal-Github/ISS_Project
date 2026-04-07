package com.iss.event;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class InterviewScheduledEvent {

    private final Long interviewId;
    private final String candidateName;
    private final String hrName;
    private final String hrEmail;
    private final LocalDate interviewDate;
    private final LocalTime timeSlot;
    private final String panelName;
    private final InterviewRound round;
    private final InterviewStatus status;

    public InterviewScheduledEvent(Long interviewId,
                                   String candidateName,
                                   String hrName,
                                   String hrEmail,
                                   LocalDate interviewDate,
                                   LocalTime timeSlot,
                                   String panelName,
                                   InterviewRound round,
                                   InterviewStatus status) {
        this.interviewId = interviewId;
        this.candidateName = candidateName;
        this.hrName = hrName;
        this.hrEmail = hrEmail;
        this.interviewDate = interviewDate;
        this.timeSlot = timeSlot;
        this.panelName = panelName;
        this.round = round;
        this.status = status;
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

    public String getHrEmail() {
        return hrEmail;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public LocalTime getTimeSlot() {
        return timeSlot;
    }

    public String getPanelName() {
        return panelName;
    }

    public InterviewRound getRound() {
        return round;
    }

    public InterviewStatus getStatus() {
        return status;
    }
}
