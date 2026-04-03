package com.checkingate.events.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(length = 500)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    private Double latitude;

    private Double longitude;

    @Column(name = "max_distance")
    private Double maxDistance;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Activity() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String eventId;
        private String description;
        private Instant startDate;
        private Instant endDate;
        private Double latitude;
        private Double longitude;
        private Double maxDistance;

        private Builder() {}

        public Builder name(String name) { this.name = name; return this; }
        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder startDate(Instant startDate) { this.startDate = startDate; return this; }
        public Builder endDate(Instant endDate) { this.endDate = endDate; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
        public Builder maxDistance(Double maxDistance) { this.maxDistance = maxDistance; return this; }

        public Activity build() {
            Activity activity = new Activity();
            activity.id = UUID.randomUUID().toString();
            activity.name = name;
            activity.eventId = eventId;
            activity.description = description;
            activity.startDate = startDate;
            activity.endDate = endDate;
            activity.latitude = latitude;
            activity.longitude = longitude;
            activity.maxDistance = maxDistance;
            activity.createdAt = Instant.now();
            return activity;
        }
    }

    public boolean hasLocationRestriction() {
        return latitude != null && longitude != null && maxDistance != null;
    }

    public boolean isWithinAllowedDistance(double lat, double lng) {
        if (!hasLocationRestriction()) return true;
        double distance = HaversineCalculator.distance(latitude, longitude, lat, lng);
        return distance <= maxDistance;
    }

    public boolean isCheckInAllowed(Instant checkInTime) {
        return hasStarted() && !hasEnded();
    }

    public boolean hasStarted() {
        return startDate.isBefore(Instant.now());
    }

    public boolean hasEnded() {
        return endDate.isBefore(Instant.now());
    }

    public boolean isStartDateBeforeEndDate() {
        return startDate.isBefore(endDate);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEventId() { return eventId; }
    public String getDescription() { return description; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getMaxDistance() { return maxDistance; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
