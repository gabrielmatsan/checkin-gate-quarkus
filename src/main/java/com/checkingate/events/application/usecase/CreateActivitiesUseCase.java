package com.checkingate.events.application.usecase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.checkingate.events.domain.entity.Activity;
import com.checkingate.events.domain.port.ActivityRepository;
import com.checkingate.events.domain.service.UserAuthorizationService;
import com.checkingate.shared.exception.BadRequestException;
import com.checkingate.shared.exception.ConflictException;
import com.checkingate.shared.exception.ForbiddenException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateActivitiesUseCase {

    @Inject
    ActivityRepository activityRepository;

    @Inject
    UserAuthorizationService userAuthorizationService;

    @Transactional
    public List<Activity> execute(Input input) {
        if (input.activities.size() > 10) {
            throw new BadRequestException("only 10 activities at a time");
        }

        if (!userAuthorizationService.isUserAdmin(input.userId)) {
            throw new ForbiddenException("user is not an admin");
        }

        // Validate duplicate names in input
        Set<String> nameSet = new HashSet<>();
        List<String> names = new ArrayList<>();
        for (var a : input.activities) {
            if (!nameSet.add(a.name)) {
                throw new BadRequestException("duplicate activity name in input: " + a.name);
            }
            names.add(a.name);
        }

        // Check for existing activities with same names
        var existing = activityRepository.findByEventIdAndNames(input.eventId, names);
        if (!existing.isEmpty()) {
            throw new ConflictException("activities with the same names already exist: " + existing.get(0).getName());
        }

        List<Activity> activities = input.activities.stream()
                .map(a -> Activity.builder()
                    .name(a.name)
                    .eventId(input.eventId)
                    .description(a.description)
                    .startDate(a.startDate)
                    .endDate(a.endDate)
                    .latitude(a.latitude)
                    .longitude(a.longitude)
                    .maxDistance(a.maxDistance)
                    .build()
                )
                .toList();

        return activityRepository.saveAll(new ArrayList<>(activities));
    }

    public record Input(String userId, String eventId, List<ActivityInput> activities) {}

    public record ActivityInput(
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        Double latitude,
        Double longitude,
        Double maxDistance
    ) {}
}
