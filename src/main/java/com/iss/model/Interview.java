package com.iss.model;

import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate interviewDate;

    @Column(nullable = false)
    private LocalTime timeSlot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hr_user_id", nullable = false)
    private Accounts hrUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_user_id",nullable = false)
    private Accounts panelUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private InterviewRound round;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewStatus status;

}
