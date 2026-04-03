package com.checkingate.events.infra.persistence;

import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.entity.CheckIn;
import com.checkingate.events.domain.port.CheckInRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class CheckInPanacheRepository implements CheckInRepository {

    @Inject
    EntityManager em;

    @Override
    public CheckIn save(CheckIn checkIn) {
        em.persist(checkIn);
        return checkIn;
    }

    @Override
    public Optional<CheckIn> findByUserAndActivity(String userId, String activityId) {
        return em.createQuery(
                "SELECT c FROM CheckIn c WHERE c.userId = :userId AND c.activityId = :activityId", CheckIn.class)
                .setParameter("userId", userId)
                .setParameter("activityId", activityId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<CheckIn> findByActivityIds(List<String> activityIds) {
        return em.createQuery("SELECT c FROM CheckIn c WHERE c.activityId IN :ids", CheckIn.class)
                .setParameter("ids", activityIds)
                .getResultList();
    }

    @Override
    public List<CheckIn> findByUserId(String userId) {
        return em.createQuery("SELECT c FROM CheckIn c WHERE c.userId = :userId", CheckIn.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
