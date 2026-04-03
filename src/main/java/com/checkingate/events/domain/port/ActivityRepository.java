package com.checkingate.events.domain.port;

import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.entity.Activity;

public interface ActivityRepository {
    List<Activity> saveAll(List<Activity> activities);
    Optional<Activity> findById(String id);
    List<Activity> findByEventId(String eventId);
    List<Activity> findByEventIdAndNames(String eventId, List<String> names);
}
