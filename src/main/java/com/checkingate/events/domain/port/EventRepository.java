package com.checkingate.events.domain.port;

import java.util.Optional;

import com.checkingate.events.domain.entity.Event;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(String id);
    void update(Event event);
}
