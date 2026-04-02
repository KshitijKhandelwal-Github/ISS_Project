package com.iss.model;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate interviewDate;

    @Column(nullable = false)
    private LocalTime timeSlot;

    @Column(nullable = false, length = 100)
    private String panelName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hr_user_id", nullable = false)
    private Accounts hrUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private InterviewRound round;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
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

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Accounts getHrUser() {
        return hrUser;
    }

    public void setHrUser(Accounts hrUser) {
        this.hrUser = hrUser;
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

