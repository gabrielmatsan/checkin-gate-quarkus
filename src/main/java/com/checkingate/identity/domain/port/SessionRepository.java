package com.checkingate.identity.domain.port;

import java.util.Optional;

import com.checkingate.identity.domain.entity.Session;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findByRefreshToken(String refreshToken);
    void delete(String id);
    void deleteAllByUserId(String userId);
}
