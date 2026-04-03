package com.checkingate.events.domain.port;

public interface EmailService {
    void send(SendEmailParams params);

    record SendEmailParams(
        String to,
        String subject,
        String body,
        java.util.List<Attachment> attachments
    ) {}

    record Attachment(
        String filename,
        byte[] content,
        String contentType
    ) {}
}
