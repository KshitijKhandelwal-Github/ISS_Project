package com.iss.repository;

import com.iss.model.Interview;
import com.iss.model.enums.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByCandidateId(Long candidateId);
    List<Interview> findByRound(InterviewRound round);
    List<Interview> findByHrUserIdOrPanelUserId(Long hrUserId, Long panelUserId);

    boolean existsByPanelUserIdAndInterviewDateAndTimeSlot(Long panelUserId, java.time.LocalDate interviewDate, java.time.LocalTime timeSlot);

    boolean existsByPanelUserIdAndInterviewDateAndTimeSlotAndIdNot(Long panelUserId, java.time.LocalDate interviewDate, java.time.LocalTime timeSlot, Long id);
}
