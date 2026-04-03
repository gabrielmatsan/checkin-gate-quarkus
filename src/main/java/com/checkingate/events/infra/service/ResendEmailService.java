package com.checkingate.events.infra.service;

import com.checkingate.events.domain.port.EmailService;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ResendEmailService implements EmailService {

    @Inject
    Mailer mailer;

    @Override
    public void send(SendEmailParams params) {
        Mail mail = Mail.withHtml(params.to(), params.subject(), params.body());

        if (params.attachments() != null) {
            for (var attachment : params.attachments()) {
                mail.addAttachment(
                    attachment.filename(),
                    attachment.content(),
                    attachment.contentType()
                );
            }
        }

        mailer.send(mail);
    }
}
