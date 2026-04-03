package com.checkingate.events.application.usecase;

import java.util.List;

import com.checkingate.events.domain.entity.Activity;
import com.checkingate.events.domain.entity.CheckIn;
import com.checkingate.events.domain.entity.Event;
import com.checkingate.events.domain.port.ActivityRepository;
import com.checkingate.events.domain.port.CheckInRepository;
import com.checkingate.events.domain.port.EventRepository;
import com.checkingate.shared.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetEventDetailsUseCase {

    @Inject
    EventRepository eventRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    CheckInRepository checkInRepository;

    public Output execute(String eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event not found"));

        var activities = activityRepository.findByEventId(eventId);

        List<String> activityIds = activities.stream().map(Activity::getId).toList();
        List<CheckIn> checkIns = activityIds.isEmpty()
                ? List.of()
                : checkInRepository.findByActivityIds(activityIds);

        return new Output(event, activities, checkIns);
    }

    public record Output(Event event, List<Activity> activities, List<CheckIn> checkIns) {}
}
