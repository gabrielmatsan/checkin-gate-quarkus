package com.checkingate.events.application.usecase;

import java.time.Instant;
import java.util.List;

import com.checkingate.events.domain.entity.Event;
import com.checkingate.events.domain.port.EventRepository;
import com.checkingate.events.domain.service.UserAuthorizationService;
import com.checkingate.shared.exception.BadRequestException;
import com.checkingate.shared.exception.ForbiddenException;
import com.checkingate.shared.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateEventUseCase {

    @Inject
    EventRepository eventRepository;

    @Inject
    UserAuthorizationService userAuthorizationService;

    @Transactional
    public Event execute(Input input) {
        var user = userAuthorizationService.getUserById(input.userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (!user.isAdmin()) {
            throw new ForbiddenException("user is not an admin");
        }

        Event event = Event.builder()
            .name(input.name)
            .allowedDomains(input.allowedDomains)
            .description(input.description)
            .startDate(input.startDate)
            .endDate(input.endDate)
            .build();

        if (!event.isStartDateBeforeEndDate()) {
            throw new BadRequestException("start date must be before end date");
        }

        return eventRepository.save(event);
    }

    public record Input(
        String userId,
        String name,
        List<String> allowedDomains,
        String description,
        Instant startDate,
        Instant endDate
    ) {}
}
