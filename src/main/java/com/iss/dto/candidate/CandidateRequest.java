package com.iss.dto.candidate;

import com.iss.model.enums.CandidateStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRequest {
    private Long accountId; // Optional: Only if linked to a login account
    private String name;
    private String cvReference;
    private String primarySkill;
    private String skillDetails;
    private CandidateStatus status;
    private Integer yearsOfExperience;
    private LocalDate lastWorkingDay;
    private Integer noticePeriod;
    private Boolean isActive;
}
