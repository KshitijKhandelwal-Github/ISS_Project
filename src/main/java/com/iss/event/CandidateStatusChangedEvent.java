package com.iss.event;

import com.iss.model.enums.CandidateStatus;

public class CandidateStatusChangedEvent {

    private final Long candidateId;
    private final String candidateName;
    private final CandidateStatus oldStatus;
    private final CandidateStatus newStatus;

    public CandidateStatusChangedEvent(Long candidateId,
                                       String candidateName,
                                       CandidateStatus oldStatus,
                                       CandidateStatus newStatus) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public CandidateStatus getOldStatus() {
        return oldStatus;
    }

    public CandidateStatus getNewStatus() {
        return newStatus;
    }
}
