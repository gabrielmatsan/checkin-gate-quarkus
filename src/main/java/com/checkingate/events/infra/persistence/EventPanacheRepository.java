package com.checkingate.events.infra.persistence;

import java.util.Optional;

import com.checkingate.events.domain.entity.Event;
import com.checkingate.events.domain.port.EventRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class EventPanacheRepository implements EventRepository {

    @Inject
    EntityManager em;

    @Override
    public Event save(Event event) {
        em.persist(event);
        return event;
    }

    @Override
    public Optional<Event> findById(String id) {
        return Optional.ofNullable(em.find(Event.class, id));
    }

    @Override
    public void update(Event event) {
        em.merge(event);
    }
}
