package com.iss.model;

import com.iss.model.enums.CandidateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Accounts accounts;

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

    @OneToMany(mappedBy = "candidate", fetch = FetchType.LAZY)
    private List<Interview> interviews;

    @PrePersist
    @PreUpdate
    public void syncNameWithAccount() {
        if (this.accounts != null) {
            this.name = this.accounts.getFullName();
        }
    }
}
