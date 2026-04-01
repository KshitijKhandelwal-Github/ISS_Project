package com.iss.model;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "interviews")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id")
    private TechnicalPanelProfile panel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id")
    private HrProfile hr;

    private String candidateName;
    private String panelName;
    private String hrName;

    @Enumerated(EnumType.STRING)
    private InterviewRound round;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

}
