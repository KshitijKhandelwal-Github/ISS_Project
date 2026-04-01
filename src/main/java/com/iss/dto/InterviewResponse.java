package com.iss.dto;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class InterviewResponse {

    private Long id;
    private LocalDate interviewDate;
    private LocalTime timeSlot;
    private String panelName;
    private Long candidateId;
    private String candidateName;
    private Long hrUserId;
    private String hrName;
    private InterviewRound round;
    private InterviewStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public LocalTime getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(LocalTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public Long getHrUserId() {
        return hrUserId;
    }

    public void setHrUserId(Long hrUserId) {
        this.hrUserId = hrUserId;
    }

    public String getHrName() {
        return hrName;
    }

    public void setHrName(String hrName) {
        this.hrName = hrName;
    }

    public InterviewRound getRound() {
        return round;
    }

    public void setRound(InterviewRound round) {
        this.round = round;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }
}
