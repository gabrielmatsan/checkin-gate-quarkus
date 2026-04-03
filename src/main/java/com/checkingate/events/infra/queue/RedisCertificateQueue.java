package com.checkingate.events.infra.queue;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.entity.CertificateJob;
import com.checkingate.events.domain.port.CertificateQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisCertificateQueue implements CertificateQueue {

    private static final String QUEUE_KEY = "certificate:jobs";

    private final ListCommands<String, String> listCommands;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    public RedisCertificateQueue(RedisDataSource redisDataSource) {
        this.listCommands = redisDataSource.list(String.class);
    }

    @Override
    public void enqueue(CertificateJob job) {
        listCommands.lpush(QUEUE_KEY, serialize(job));
    }

    @Override
    public void enqueueBatch(List<CertificateJob> jobs) {
        for (var job : jobs) {
            listCommands.lpush(QUEUE_KEY, serialize(job));
        }
    }

    @Override
    public Optional<CertificateJob> dequeueWithTimeout(Duration timeout) {
        try {
            var result = listCommands.brpop(timeout, QUEUE_KEY);
            if (result == null) return Optional.empty();
            return Optional.of(deserialize(result.value()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public long size() {
        return listCommands.llen(QUEUE_KEY);
    }

    private String serialize(CertificateJob job) {
        try {
            return objectMapper.writeValueAsString(job);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize certificate job", e);
        }
    }

    private CertificateJob deserialize(String json) {
        try {
            return objectMapper.readValue(json, CertificateJob.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize certificate job", e);
        }
    }
}
