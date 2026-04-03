package com.checkingate.identity.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Session() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String refreshToken;
        private String ipAddress;
        private String userAgent;
        private Instant expiresAt;

        private Builder() {}

        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }

        public Session build() {
            Session session = new Session();
            session.id = UUID.randomUUID().toString();
            session.userId = userId;
            session.refreshToken = refreshToken;
            session.ipAddress = ipAddress;
            session.userAgent = userAgent;
            session.expiresAt = expiresAt;
            session.createdAt = Instant.now();
            return session;
        }
    }

    public boolean isExpired() {
        return this.expiresAt.isBefore(Instant.now());
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getRefreshToken() { return refreshToken; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
}
