package com.checkingate.events.application.usecase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.checkingate.events.domain.entity.Activity;
import com.checkingate.events.domain.entity.CertificateJob;
import com.checkingate.events.domain.port.ActivityRepository;
import com.checkingate.events.domain.port.CertificateQueue;
import com.checkingate.events.domain.port.CheckInRepository;
import com.checkingate.events.domain.port.EventRepository;
import com.checkingate.events.domain.service.UserAuthorizationService;
import com.checkingate.events.domain.service.UserAuthorizationService.UserInfo;
import com.checkingate.shared.exception.BadRequestException;
import com.checkingate.shared.exception.ForbiddenException;
import com.checkingate.shared.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FinishEventUseCase {

    @Inject
    EventRepository eventRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    CheckInRepository checkInRepository;

    @Inject
    UserAuthorizationService userAuthorizationService;

    @Inject
    CertificateQueue certificateQueue;

    @Transactional
    public void execute(Input input) {
        var user = userAuthorizationService.getUserById(input.userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (!user.isAdmin()) {
            throw new ForbiddenException("user is not an admin");
        }

        var event = eventRepository.findById(input.eventId)
                .orElseThrow(() -> new NotFoundException("event not found"));

        var activities = activityRepository.findByEventId(input.eventId);
        if (activities.isEmpty()) {
            throw new BadRequestException("no activities found for event");
        }

        List<String> activityIds = new ArrayList<>();
        for (var activity : activities) {
            activityIds.add(activity.getId());
            if (!activity.hasEnded()) {
                throw new BadRequestException("activity has not ended");
            }
        }

        var checkIns = checkInRepository.findByActivityIds(activityIds);
        if (checkIns.isEmpty()) {
            throw new BadRequestException("no check-ins found for activities");
        }

        // Deduplicate user IDs
        Set<String> userIdSet = new LinkedHashSet<>();
        for (var checkIn : checkIns) {
            userIdSet.add(checkIn.getUserId());
        }

        var users = userAuthorizationService.getUserInfoBatch(new ArrayList<>(userIdSet));
        if (users.isEmpty()) {
            throw new BadRequestException("no users found for check-ins");
        }

        // Index users and activities
        Map<String, UserInfo> userIndex = new HashMap<>();
        for (var u : users) {
            userIndex.put(u.id(), u);
        }

        Map<String, Activity> activityIndex = new HashMap<>();
        for (var a : activities) {
            activityIndex.put(a.getId(), a);
        }

        // Create certificate jobs
        List<CertificateJob> jobs = new ArrayList<>();
        for (var checkIn : checkIns) {
            var u = userIndex.get(checkIn.getUserId());
            var a = activityIndex.get(checkIn.getActivityId());

            if (u == null || a == null) continue;

            jobs.add(CertificateJob.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .userId(u.id())
                .userName(u.firstName() + " " + u.lastName())
                .userEmail(u.email())
                .activityId(a.getId())
                .activityName(a.getName())
                .activityDate(a.getStartDate())
                .startTime(a.getStartDate())
                .endTime(a.getEndDate())
                .checkedAt(checkIn.getCheckedAt())
                .build()
            );
        }

        certificateQueue.enqueueBatch(jobs);

        // Update event status
        event.markAsCompleted();
        eventRepository.update(event);
    }

    public record Input(String eventId, String userId) {}
}
