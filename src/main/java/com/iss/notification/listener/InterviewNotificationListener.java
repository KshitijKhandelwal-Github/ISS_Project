package com.iss.notification.listener;

import com.iss.event.CandidateStatusChangedEvent;
import com.iss.event.InterviewScheduledEvent;
import com.iss.notification.dto.NotificationEvent;
import com.iss.notification.kafka.NotificationKafkaPublisher;
import com.iss.notification.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InterviewNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(InterviewNotificationListener.class);

    private final EmailNotificationService emailNotificationService;
    private final ObjectProvider<NotificationKafkaPublisher> notificationKafkaPublisher;
    private final String defaultRecipientEmail;

    public InterviewNotificationListener(EmailNotificationService emailNotificationService,
                                         ObjectProvider<NotificationKafkaPublisher> notificationKafkaPublisher,
                                         @Value("${app.notifications.email.default-recipient}") String defaultRecipientEmail) {
        this.emailNotificationService = emailNotificationService;
        this.notificationKafkaPublisher = notificationKafkaPublisher;
        this.defaultRecipientEmail = defaultRecipientEmail;
    }

    @EventListener
    public void onInterviewScheduled(InterviewScheduledEvent event) {
        logger.info(
                "Interview scheduled event received. interviewId={}, candidateName={}, hrName={}",
                event.getInterviewId(),
                event.getCandidateName(),
                event.getHrName()
        );
        NotificationEvent notificationEvent = new NotificationEvent(
                "INTERVIEW_SCHEDULED",
                event.getHrEmail(),
                "Interview scheduled for " + event.getCandidateName(),
                "Interview for " + event.getCandidateName()
                        + " is scheduled on " + event.getInterviewDate()
                        + " at " + event.getTimeSlot()
                        + " with panel " + event.getPanelName()
                        + " for " + event.getRound()
                        + ". Current status: " + event.getStatus() + ".",
                "INTERVIEW",
                event.getInterviewId()
        );
        notify(notificationEvent);
    }

    @EventListener
    public void onCandidateStatusChanged(CandidateStatusChangedEvent event) {
        logger.info(
                "Candidate status change event received. candidateId={}, candidateName={}, oldStatus={}, newStatus={}",
                event.getCandidateId(),
                event.getCandidateName(),
                event.getOldStatus(),
                event.getNewStatus()
        );
        NotificationEvent notificationEvent = new NotificationEvent(
                "CANDIDATE_STATUS_CHANGED",
                defaultRecipientEmail,
                "Candidate status updated for " + event.getCandidateName(),
                "Candidate " + event.getCandidateName()
                        + " moved from " + event.getOldStatus()
                        + " to " + event.getNewStatus() + ".",
                "CANDIDATE",
                event.getCandidateId()
        );
        notify(notificationEvent);
    }

    private void notify(NotificationEvent notificationEvent) {
        emailNotificationService.sendEmail(notificationEvent);
        notificationKafkaPublisher.ifAvailable(publisher -> publisher.publish(notificationEvent));
    }
}
