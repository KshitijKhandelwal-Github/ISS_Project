package com.iss.dto.interview;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
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
}
