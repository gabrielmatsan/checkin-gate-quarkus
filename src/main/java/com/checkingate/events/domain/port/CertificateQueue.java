package com.checkingate.events.domain.port;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.entity.CertificateJob;

public interface CertificateQueue {
    void enqueue(CertificateJob job);
    void enqueueBatch(List<CertificateJob> jobs);
    Optional<CertificateJob> dequeueWithTimeout(Duration timeout);
    long size();
}
