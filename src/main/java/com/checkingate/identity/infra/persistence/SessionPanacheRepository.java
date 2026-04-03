package com.checkingate.identity.infra.persistence;

import java.util.Optional;

import com.checkingate.identity.domain.entity.Session;
import com.checkingate.identity.domain.port.SessionRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class SessionPanacheRepository implements SessionRepository {

    @Inject
    EntityManager em;

    @Override
    public void save(Session session) {
        em.persist(session);
    }

    @Override
    public Optional<Session> findByRefreshToken(String refreshToken) {
        return em.createQuery("SELECT s FROM Session s WHERE s.refreshToken = :token", Session.class)
                .setParameter("token", refreshToken)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void delete(String id) {
        var session = em.find(Session.class, id);
        if (session != null) em.remove(session);
    }

    @Override
    public void deleteAllByUserId(String userId) {
        em.createQuery("DELETE FROM Session s WHERE s.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
