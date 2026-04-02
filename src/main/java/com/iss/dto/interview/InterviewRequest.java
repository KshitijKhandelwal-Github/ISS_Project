package com.iss.dto.interview;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    private LocalDate interviewDate;
    private LocalTime timeSlot;
    private String panelId;
    private Long candidateId;
    private Long hrUserId;
    private InterviewRound round;
    private InterviewStatus status;
}
