package com.checkingate.events.application.usecase;

import com.checkingate.events.domain.entity.CheckIn;
import com.checkingate.events.domain.port.ActivityRepository;
import com.checkingate.events.domain.port.CheckInRepository;
import com.checkingate.events.domain.port.EventRepository;
import com.checkingate.events.domain.service.UserAuthorizationService;
import com.checkingate.shared.exception.BadRequestException;
import com.checkingate.shared.exception.ConflictException;
import com.checkingate.shared.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CheckInActivityUseCase {

    @Inject
    CheckInRepository checkInRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    UserAuthorizationService userAuthorizationService;

    @Transactional
    public CheckIn execute(Input input) {
        // 1. Find activity
        var activity = activityRepository.findById(input.activityId)
                .orElseThrow(() -> new NotFoundException("activity not found"));

        // 2. Check existing check-in
        var existing = checkInRepository.findByUserAndActivity(input.userId, input.activityId);
        if (existing.isPresent()) {
            throw new ConflictException("user already checked in");
        }

        // 3. Validate location if required
        if (activity.hasLocationRestriction()) {
            if (input.latitude == null || input.longitude == null) {
                throw new BadRequestException("location is required for this activity");
            }
            if (!activity.isWithinAllowedDistance(input.latitude, input.longitude)) {
                throw new BadRequestException("check-in not allowed outside activity area");
            }
        }

        // 4. Find event and validate domain
        var event = eventRepository.findById(activity.getEventId())
                .orElseThrow(() -> new NotFoundException("event not found"));

        String userEmail = userAuthorizationService.getUserEmail(input.userId);
        if (userEmail == null || userEmail.isBlank()) {
            throw new NotFoundException("user email not found");
        }

        if (!event.isAllowedDomain(userEmail)) {
            throw new ForbiddenDomainException("user domain not allowed");
        }

        // 5. Create and save check-in
        CheckIn checkIn = CheckIn.builder()
            .userId(input.userId)
            .activityId(input.activityId)
            .build();
        return checkInRepository.save(checkIn);
    }

    public record Input(String userId, String activityId, Double latitude, Double longitude) {}

    public static class ForbiddenDomainException extends BadRequestException {
        public ForbiddenDomainException(String message) {
            super(message);
        }
    }
}
