package com.checkingate.events.infra.pdf;

import java.io.ByteArrayOutputStream;

import com.checkingate.events.domain.port.CertificateGenerator;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OpenHtmlCertificateGenerator implements CertificateGenerator {

    @Inject
    Template certificate;

    @Override
    public byte[] generate(CertificateData data) {
        String html = certificate
                .data("recipientName", data.recipientName())
                .data("eventName", data.eventName())
                .data("eventDate", data.eventDate())
                .data("workload", data.workload())
                .data("directorName", data.directorName())
                .data("coordinatorName", data.coordinatorName())
                .data("certificateDate", data.certificateDate())
                .render();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate PDF", e);
        }
    }
}
