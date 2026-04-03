package com.checkingate.events.domain.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allowed_domains", columnDefinition = "jsonb")
    private List<String> allowedDomains;

    @Column(length = 500)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "event_status")
    private EventStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Event() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<String> allowedDomains;
        private String description;
        private Instant startDate;
        private Instant endDate;

        private Builder() {}

        public Builder name(String name) { this.name = name; return this; }
        public Builder allowedDomains(List<String> allowedDomains) { this.allowedDomains = allowedDomains; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder startDate(Instant startDate) { this.startDate = startDate; return this; }
        public Builder endDate(Instant endDate) { this.endDate = endDate; return this; }

        public Event build() {
            Event event = new Event();
            event.id = UUID.randomUUID().toString();
            event.name = name;
            event.allowedDomains = allowedDomains;
            event.description = description;
            event.startDate = startDate;
            event.endDate = endDate;
            event.status = EventStatus.draft;
            event.createdAt = Instant.now();
            return event;
        }
    }

    public boolean isAllowedDomain(String email) {
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            return true;
        }
        String domain = extractDomain(email);
        return allowedDomains.contains(domain);
    }

    public boolean isCheckInWithinEventTime(Instant checkInTime) {
        return checkInTime.isAfter(startDate) && checkInTime.isBefore(endDate);
    }

    public boolean isStartDateBeforeEndDate() {
        return startDate.isBefore(endDate);
    }

    public void markAsCompleted() {
        this.status = EventStatus.completed;
    }

    private static String extractDomain(String email) {
        String[] parts = email.split("@");
        if (parts.length != 2) return "";
        return parts[1];
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getAllowedDomains() { return allowedDomains; }
    public String getDescription() { return description; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public EventStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAllowedDomains(List<String> allowedDomains) { this.allowedDomains = allowedDomains; }
    public void setStatus(EventStatus status) { this.status = status; }
}
