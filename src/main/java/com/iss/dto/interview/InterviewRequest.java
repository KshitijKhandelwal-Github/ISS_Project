package com.iss.dto.interview;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    private LocalDate interviewDate;
    private LocalTime timeSlot;
    private String panelName;
    private Long candidateId;
    private Long hrUserId;
    private Long panelUserId;
    private InterviewRound round;
    private InterviewStatus status;

}
