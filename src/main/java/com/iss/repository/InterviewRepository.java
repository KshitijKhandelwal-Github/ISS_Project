package com.iss.repository;

import com.iss.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    java.util.List<Interview> findByCandidateId(Long candidateId);
}
