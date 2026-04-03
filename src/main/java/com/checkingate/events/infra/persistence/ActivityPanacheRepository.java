package com.checkingate.events.infra.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.entity.Activity;
import com.checkingate.events.domain.port.ActivityRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ActivityPanacheRepository implements ActivityRepository {

    @Inject
    EntityManager em;

    @Override
    public List<Activity> saveAll(List<Activity> activities) {
        List<Activity> saved = new ArrayList<>();
        for (Activity activity : activities) {
            em.persist(activity);
            saved.add(activity);
        }
        return saved;
    }

    @Override
    public Optional<Activity> findById(String id) {
        return Optional.ofNullable(em.find(Activity.class, id));
    }

    @Override
    public List<Activity> findByEventId(String eventId) {
        return em.createQuery("SELECT a FROM Activity a WHERE a.eventId = :eventId", Activity.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    @Override
    public List<Activity> findByEventIdAndNames(String eventId, List<String> names) {
        return em.createQuery("SELECT a FROM Activity a WHERE a.eventId = :eventId AND a.name IN :names", Activity.class)
                .setParameter("eventId", eventId)
                .setParameter("names", names)
                .getResultList();
    }
}
