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
public class InterviewResponse {

    private Long id;
    private LocalDate interviewDate;
    private LocalTime timeSlot;
    private Long panelId;
    private String panelName;
    private Long candidateId;
    private String candidateName;
    private Long hrUserId;
    private String hrName;
    private Long panelUserId;
    private String panelUserName;
    private InterviewRound round;
    private InterviewStatus status;
}
