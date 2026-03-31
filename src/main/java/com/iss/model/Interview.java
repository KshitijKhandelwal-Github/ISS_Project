package com.iss.model;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime timeSlot;

    private String panelName;

    private String candidateName;

    private String hrName;

    @Enumerated(EnumType.STRING)
    private InterviewRound round;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;
}
