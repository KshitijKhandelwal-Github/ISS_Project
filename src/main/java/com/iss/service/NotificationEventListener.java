package com.iss.service;

import com.iss.event.CandidateStatusChangedEvent;
import com.iss.event.InterviewScheduledEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void onCandidateStatusChanged(CandidateStatusChangedEvent event) {
        String title = "Candidate status updated";
        String message = event.getCandidateName()
                + " moved from " + event.getOldStatus()
                + " to " + event.getNewStatus();

        notificationService.addNotification("CANDIDATE_STATUS_CHANGED", title, message);
        log.info("Notification stored. type=CANDIDATE_STATUS_CHANGED candidateId={}", event.getCandidateId());
    }

    @EventListener
    public void onInterviewScheduled(InterviewScheduledEvent event) {
        String title = "Interview scheduled";
        String message = "Interview scheduled for "
                + event.getCandidateName()
                + " by " + event.getHrName();

        notificationService.addNotification("INTERVIEW_SCHEDULED", title, message);
        log.info("Notification stored. type=INTERVIEW_SCHEDULED interviewId={}", event.getInterviewId());
    }
}
