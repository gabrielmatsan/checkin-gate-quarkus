package com.checkingate.events.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class CertificateJob {

    private String jobId;
    private String eventId;
    private String eventName;
    private String userId;
    private String userName;
    private String userEmail;
    private String activityId;
    private String activityName;
    private Instant activityDate;
    private Instant startTime;
    private Instant endTime;
    private Instant checkedAt;
    private Instant enqueuedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId;
        private String eventName;
        private String userId;
        private String userName;
        private String userEmail;
        private String activityId;
        private String activityName;
        private Instant activityDate;
        private Instant startTime;
        private Instant endTime;
        private Instant checkedAt;

        private Builder() {}

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder eventName(String eventName) { this.eventName = eventName; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder activityId(String activityId) { this.activityId = activityId; return this; }
        public Builder activityName(String activityName) { this.activityName = activityName; return this; }
        public Builder activityDate(Instant activityDate) { this.activityDate = activityDate; return this; }
        public Builder startTime(Instant startTime) { this.startTime = startTime; return this; }
        public Builder endTime(Instant endTime) { this.endTime = endTime; return this; }
        public Builder checkedAt(Instant checkedAt) { this.checkedAt = checkedAt; return this; }

        public CertificateJob build() {
            CertificateJob job = new CertificateJob();
            job.jobId = UUID.randomUUID().toString();
            job.eventId = eventId;
            job.eventName = eventName;
            job.userId = userId;
            job.userName = userName;
            job.userEmail = userEmail;
            job.activityId = activityId;
            job.activityName = activityName;
            job.activityDate = activityDate;
            job.startTime = startTime;
            job.endTime = endTime;
            job.checkedAt = checkedAt;
            job.enqueuedAt = Instant.now();
            return job;
        }
    }

    // Getters and setters for Jackson serialization
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }
    
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    
    public Instant getActivityDate() { return activityDate; }
    public void setActivityDate(Instant activityDate) { this.activityDate = activityDate; }
    
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    
    public Instant getCheckedAt() { return checkedAt; }
    public void setCheckedAt(Instant checkedAt) { this.checkedAt = checkedAt; }
    
    public Instant getEnqueuedAt() { return enqueuedAt; }
    public void setEnqueuedAt(Instant enqueuedAt) { this.enqueuedAt = enqueuedAt; }
}
