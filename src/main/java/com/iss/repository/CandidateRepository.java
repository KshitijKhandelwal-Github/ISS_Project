package com.iss.repository;

import com.iss.model.Candidate;
import com.iss.model.enums.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByIsActive(Boolean isActive);
    List<Candidate> findByPrimarySkill(String primarySkill);
    List<Candidate> findByStatus(CandidateStatus status);
}
