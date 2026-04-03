package com.checkingate.events.domain.port;

public interface CertificateGenerator {
    byte[] generate(CertificateData data);

    record CertificateData(
        String recipientName,
        String eventName,
        String eventDate,
        String workload,
        String directorName,
        String coordinatorName,
        String certificateDate
    ) {}
}
