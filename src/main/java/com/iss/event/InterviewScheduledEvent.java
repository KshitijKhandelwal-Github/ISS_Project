package com.iss.event;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
public class InterviewScheduledEvent {

    private final Long interviewId;
    private final String candidateName;
    private final String hrName;
    private final String hrEmail;
    private final LocalDate interviewDate;
    private final LocalTime timeSlot;
    private final String panelName;
    private final String panelEmail;
    private final InterviewRound round;
    private final InterviewStatus status;

    public InterviewScheduledEvent(Long interviewId,
                                   String candidateName,
                                   String hrName,
                                   String hrEmail,
                                   String panelName,
                                   String panelEmail,
                                   LocalDate interviewDate,
                                   LocalTime timeSlot,
                                   InterviewRound round,
                                   InterviewStatus status) {
        this.interviewId = interviewId;
        this.candidateName = candidateName;
        this.hrName = hrName;
        this.hrEmail = hrEmail;
        this.interviewDate = interviewDate;
        this.timeSlot = timeSlot;
        this.panelName = panelName;
        this.panelEmail = panelEmail;
        this.round = round;
        this.status = status;
    }
}
