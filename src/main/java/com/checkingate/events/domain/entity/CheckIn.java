package com.checkingate.events.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "activity_id", nullable = false, length = 36)
    private String activityId;

    @Column(name = "checked_at")
    private Instant checkedAt;

    protected CheckIn() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String activityId;

        private Builder() {}

        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder activityId(String activityId) { this.activityId = activityId; return this; }

        public CheckIn build() {
            CheckIn checkIn = new CheckIn();
            checkIn.id = UUID.randomUUID().toString();
            checkIn.userId = userId;
            checkIn.activityId = activityId;
            checkIn.checkedAt = Instant.now();
            return checkIn;
        }
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getActivityId() { return activityId; }
    public Instant getCheckedAt() { return checkedAt; }
}
