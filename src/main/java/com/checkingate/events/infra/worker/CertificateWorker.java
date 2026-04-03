package com.checkingate.events.infra.worker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.checkingate.events.domain.entity.CertificateJob;
import com.checkingate.events.domain.port.CertificateGenerator;
import com.checkingate.events.domain.port.CertificateQueue;
import com.checkingate.events.domain.port.EmailService;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class CertificateWorker {

    private static final Logger LOG = Logger.getLogger(CertificateWorker.class);
    private static final int MAX_CONCURRENCY = 5;

    @Inject
    CertificateQueue certificateQueue;

    @Inject
    CertificateGenerator certificateGenerator;

    @Inject
    EmailService emailService;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ExecutorService executor;

    void onStart(@Observes StartupEvent ev) {
        running.set(true);
        executor = Executors.newFixedThreadPool(MAX_CONCURRENCY);
        Thread.startVirtualThread(this::pollLoop);
        LOG.info("Certificate worker started");
    }

    void onStop(@Observes ShutdownEvent ev) {
        running.set(false);
        if (executor != null) {
            executor.shutdown();
        }
        LOG.info("Certificate worker stopped");
    }

    private void pollLoop() {
        while (running.get()) {
            try {
                var jobOpt = certificateQueue.dequeueWithTimeout(Duration.ofSeconds(10));
                jobOpt.ifPresent(job -> executor.submit(() -> processJob(job)));
            } catch (Exception e) {
                if (running.get()) {
                    LOG.error("Failed to dequeue job", e);
                }
            }
        }
    }

    private void processJob(CertificateJob job) {
        LOG.infof("Processing certificate job %s for user %s <%s>, event %s, activity %s",
                job.getJobId(), job.getUserName(), job.getUserEmail(),
                job.getEventName(), job.getActivityName());

        try {
            String workload = calculateWorkload(job);
            String eventDate = formatDate(job.getActivityDate());
            String certificateDate = formatDate(Instant.now());

            var data = new CertificateGenerator.CertificateData(
                job.getUserName(),
                job.getEventName(),
                eventDate,
                workload,
                "Dr. Joao Silva",
                "Dra. Maria Santos",
                certificateDate
            );

            byte[] pdfBytes = certificateGenerator.generate(data);
            LOG.infof("Certificate generated for job %s, size: %d bytes", job.getJobId(), pdfBytes.length);

            String emailBody = buildCertificateEmailBody(job.getUserName(), job.getEventName());

            emailService.send(new EmailService.SendEmailParams(
                job.getUserEmail(),
                "Certificado - " + job.getEventName(),
                emailBody,
                java.util.List.of(new EmailService.Attachment(
                    "certificado-" + job.getJobId() + ".pdf",
                    pdfBytes,
                    "application/pdf"
                ))
            ));

            LOG.infof("Certificate email sent for job %s to %s", job.getJobId(), job.getUserEmail());
        } catch (Exception e) {
            LOG.errorf(e, "Failed to process certificate job %s", job.getJobId());
        }
    }

    private String calculateWorkload(CertificateJob job) {
        Duration duration = Duration.between(job.getStartTime(), job.getEndTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        if (minutes == 0) {
            return hours == 1 ? "1 hora" : hours + " horas";
        }
        if (hours == 0) {
            return minutes == 1 ? "1 minuto" : minutes + " minutos";
        }

        String hoursLabel = hours > 1 ? "horas" : "hora";
        String minutesLabel = minutes > 1 ? "minutos" : "minuto";
        return String.format("%d %s e %d %s", hours, hoursLabel, minutes, minutesLabel);
    }

    private String formatDate(Instant instant) {
        var date = instant.atZone(java.time.ZoneId.of("America/Sao_Paulo")).toLocalDate();
        String[] months = {
            "", "janeiro", "fevereiro", "marco", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        };
        return String.format("%d de %s de %d", date.getDayOfMonth(), months[date.getMonthValue()], date.getYear());
    }

    private String buildCertificateEmailBody(String name, String eventName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4F46E5; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border-radius: 0 0 8px 8px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Parabens!</h1>
                    </div>
                    <div class="content">
                        <p>Ola, <strong>%s</strong>!</p>
                        <p>E com grande satisfacao que enviamos seu certificado de participacao no evento <strong>%s</strong>.</p>
                        <p>O certificado esta em anexo neste email em formato PDF. Guarde-o em um local seguro!</p>
                        <p>Agradecemos sua participacao e esperamos ve-lo(a) em nossos proximos eventos.</p>
                        <p>Atenciosamente,<br>Equipe Checkin Gate</p>
                    </div>
                    <div class="footer">
                        <p>Este e um email automatico, por favor nao responda.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, eventName);
    }
}
