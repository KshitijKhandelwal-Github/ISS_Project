package com.iss.model;

import com.iss.model.enums.CandidateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String cvReference;

    private String primarySkill;

    @Column(columnDefinition = "TEXT")
    private String skillDetails;

    @Enumerated(EnumType.STRING)
    private CandidateStatus status;

    private Integer yearsOfExperience;

    private LocalDate lastWorkingDay;

    private Integer noticePeriod;

    private Boolean isActive;
}
