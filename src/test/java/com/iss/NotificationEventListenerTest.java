package com.iss;

import com.iss.event.CandidateStatusChangedEvent;
import com.iss.event.InterviewScheduledEvent;
import com.iss.model.enums.CandidateStatus;
import com.iss.model.enums.InterviewRound;
import com.iss.model.enums.InterviewStatus;
import com.iss.service.NotificationEventListener;
import com.iss.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventListener listener;

    // TEST 1: Candidate Status Changed
    @Test
    void shouldHandleCandidateStatusChangedEvent() {

        CandidateStatusChangedEvent event = new CandidateStatusChangedEvent(
                1L,
                "John Doe",
                CandidateStatus.SCREENED,
                CandidateStatus.SELECTED
        );

        listener.onCandidateStatusChanged(event);

        verify(notificationService).addNotification(
                eq("CANDIDATE_STATUS_CHANGED"),
                eq("Candidate status updated"),
                eq("John Doe moved from SCREENED to SELECTED")
        );
    }

    // TEST 2: Interview Scheduled
    @Test
    void shouldHandleInterviewScheduledEvent() {

        InterviewScheduledEvent event = new InterviewScheduledEvent(
                10L,
                "Jane Doe",
                "HR Alice",
                "alic.hr@iss.com",
                "Panel Bob",
                "Bob.panel@iss.com",
                LocalDate.now(),
                LocalTime.now(),
                InterviewRound.R1,
                InterviewStatus.ON_HOLD
        );

        listener.onInterviewScheduled(event);

        verify(notificationService).addNotification(
                eq("INTERVIEW_SCHEDULED"),
                eq("Interview scheduled"),
                eq("Interview scheduled for Jane Doe by HR Alice")
        );
    }
}