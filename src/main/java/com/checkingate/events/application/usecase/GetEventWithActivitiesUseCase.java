package com.checkingate.events.application.usecase;

import java.util.List;

import com.checkingate.events.domain.entity.Activity;
import com.checkingate.events.domain.entity.Event;
import com.checkingate.events.domain.port.ActivityRepository;
import com.checkingate.events.domain.port.EventRepository;
import com.checkingate.shared.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetEventWithActivitiesUseCase {

    @Inject
    EventRepository eventRepository;

    @Inject
    ActivityRepository activityRepository;

    public Output execute(String eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event not found"));

        var activities = activityRepository.findByEventId(eventId);

        return new Output(event, activities);
    }

    public record Output(Event event, List<Activity> activities) {}
}
