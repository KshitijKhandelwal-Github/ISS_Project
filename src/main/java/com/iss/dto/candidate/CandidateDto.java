package com.iss.dto.candidate;

import com.iss.model.enums.CandidateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class CandidateDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CandidateRequest {
        private Long accountId;
        private String cvReference;
        private String primarySkill;
        private String skillDetails;
        private CandidateStatus status;
        private Integer yearsOfExperience;
        private LocalDate lastWorkingDay;
        private Integer noticePeriod;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CandidateResponse {
        private Long accountId;
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
}
