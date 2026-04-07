package com.iss.notification.dto;

public class NotificationEvent {

    private String type;
    private String recipientEmail;
    private String subject;
    private String message;
    private String referenceType;
    private Long referenceId;

    public NotificationEvent() {
    }

    public NotificationEvent(String type,
                             String recipientEmail,
                             String subject,
                             String message,
                             String referenceType,
                             Long referenceId) {
        this.type = type;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}
